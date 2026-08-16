import { Directive, ElementRef, inject, input, OnDestroy, output } from '@angular/core';

export interface CxdevReorderEvent {
	from: number;
	to: number;
}

interface DragState {
	el: HTMLElement;
	handle: HTMLElement;
	placeholder: HTMLElement;
	from: number;
	grabX: number;
	grabY: number;
	baseLeft: number;
	baseTop: number;
	cols: number;
}

@Directive({
	selector: '[cxdevReorder]',
})
export class CxdevReorderDirective implements OnDestroy {
	cxdevReorder = input(false);
	reordered = output<CxdevReorderEvent>();
	sortingChange = output<boolean>();

	private elementRef = inject<ElementRef<HTMLElement>>(ElementRef);
	private drag: DragState | null = null;

	constructor() {
		const element = this.elementRef.nativeElement;
		element.addEventListener('pointerdown', this.onPointerDown);
		element.addEventListener('keydown', this.onKeyDown);
	}

	ngOnDestroy(): void {
		const element = this.elementRef.nativeElement;
		element.removeEventListener('pointerdown', this.onPointerDown);
		element.removeEventListener('keydown', this.onKeyDown);
		this.cleanupDrag();
	}

	private sortableItems(): HTMLElement[] {
		return [...this.elementRef.nativeElement.children].filter(
			(el): el is HTMLElement =>
				el instanceof HTMLElement &&
				el.dataset['key'] != null &&
				!el.hasAttribute('data-reorder-placeholder'),
		);
	}

	private columnCount(): number {
		const items = this.sortableItems();
		if (items.length < 2) return 1;

		const top = items[0].getBoundingClientRect().top;
		let cols = 1;

		for (let i = 1; i < items.length; i++) {
			if (Math.abs(items[i].getBoundingClientRect().top - top) <= 4) {
				cols++;
			} else {
				break;
			}
		}

		return cols;
	}

	private itemFromHandle(event: Event): HTMLElement | null {
		const target = event.target;
		if (!(target instanceof HTMLElement)) return null;

		const handle = target.closest('[data-reorder-handle]');
		if (!(handle instanceof HTMLElement) || !this.elementRef.nativeElement.contains(handle)) {
			return null;
		}

		const item = handle.closest('[data-key]');
		return item instanceof HTMLElement && item.parentElement === this.elementRef.nativeElement
			? item
			: null;
	}

	private handleFromEvent(event: Event): HTMLElement | null {
		const target = event.target;
		if (!(target instanceof HTMLElement)) return null;

		const handle = target.closest('[data-reorder-handle]');
		return handle instanceof HTMLElement ? handle : null;
	}

	private onPointerDown = (event: PointerEvent) => {
		if (!this.cxdevReorder()) return;

		const el = this.itemFromHandle(event);
		const handle = this.handleFromEvent(event);
		if (!el || !handle) return;

		event.preventDefault();

		const cols = this.columnCount();
		const rect = el.getBoundingClientRect();
		const placeholder = document.createElement(el.tagName);
		placeholder.setAttribute('data-reorder-placeholder', '');
		placeholder.style.setProperty('--column-span', el.style.getPropertyValue('--column-span'));
		placeholder.style.setProperty('--row-span', el.style.getPropertyValue('--row-span'));
		placeholder.style.boxSizing = 'border-box';
		placeholder.style.height = `${rect.height}px`;
		placeholder.style.width = `${rect.width}px`;
		el.after(placeholder);

		Object.assign(el.style, {
			position: 'fixed',
			left: `${rect.left}px`,
			top: `${rect.top}px`,
			width: `${rect.width}px`,
			height: `${rect.height}px`,
			zIndex: '50',
			pointerEvents: 'none',
		});
		el.setAttribute('data-reordering', '');

		this.drag = {
			el,
			handle,
			placeholder,
			from: this.sortableItems().indexOf(el),
			grabX: event.clientX,
			grabY: event.clientY,
			baseLeft: rect.left,
			baseTop: rect.top,
			cols,
		};

		handle.setPointerCapture?.(event.pointerId);
		handle.addEventListener('pointermove', this.onPointerMove);
		handle.addEventListener('pointerup', this.onPointerUp);
		handle.addEventListener('pointercancel', this.onPointerUp);
		this.sortingChange.emit(true);
	};

	private onPointerMove = (event: PointerEvent) => {
		if (!this.drag) return;

		const grid = this.drag.cols > 1;
		this.drag.el.style.top = `${this.drag.baseTop + (event.clientY - this.drag.grabY)}px`;
		if (grid) {
			this.drag.el.style.left = `${this.drag.baseLeft + (event.clientX - this.drag.grabX)}px`;
		}

		const others = this.sortableItems().filter((item) => item !== this.drag?.el);
		const before = others.find((sibling) => {
			const rect = sibling.getBoundingClientRect();
			if (!grid) return event.clientY < rect.top + rect.height / 2;
			if (event.clientY < rect.top) return true;
			if (event.clientY > rect.bottom) return false;
			return event.clientX < rect.left + rect.width / 2;
		});

		if (before) {
			this.elementRef.nativeElement.insertBefore(this.drag.placeholder, before);
		} else {
			this.elementRef.nativeElement.appendChild(this.drag.placeholder);
		}
	};

	private onPointerUp = () => {
		if (!this.drag) return;

		const { from, placeholder } = this.drag;
		const allChildren = [...this.elementRef.nativeElement.children] as HTMLElement[];
		const placeholderIndex = allChildren.indexOf(placeholder);
		let to = 0;

		for (let i = 0; i < placeholderIndex; i++) {
			const child = allChildren[i];
			if (
				child !== this.drag.el &&
				child.dataset['key'] != null &&
				!child.hasAttribute('data-reorder-placeholder')
			) {
				to++;
			}
		}

		this.cleanupDrag();
		if (to !== from) {
			this.reordered.emit({ from, to });
		}
	};

	private onKeyDown = (event: KeyboardEvent) => {
		if (!this.cxdevReorder()) return;

		const delta =
			event.key === 'ArrowUp' || event.key === 'ArrowLeft'
				? -1
				: event.key === 'ArrowDown' || event.key === 'ArrowRight'
					? 1
					: 0;
		if (delta === 0) return;

		const el = this.itemFromHandle(event);
		if (!el) return;

		const items = this.sortableItems();
		const from = items.indexOf(el);
		const to = from + delta;
		if (to < 0 || to >= items.length) return;

		event.preventDefault();
		this.reordered.emit({ from, to });
	};

	private cleanupDrag(): void {
		if (!this.drag) return;

		const { el, handle, placeholder } = this.drag;
		handle.removeEventListener('pointermove', this.onPointerMove);
		handle.removeEventListener('pointerup', this.onPointerUp);
		handle.removeEventListener('pointercancel', this.onPointerUp);
		placeholder.remove();

		for (const property of [
			'position',
			'left',
			'top',
			'width',
			'height',
			'z-index',
			'pointer-events',
		]) {
			el.style.removeProperty(property);
		}
		el.removeAttribute('data-reordering');
		this.drag = null;
		this.sortingChange.emit(false);
	}
}
