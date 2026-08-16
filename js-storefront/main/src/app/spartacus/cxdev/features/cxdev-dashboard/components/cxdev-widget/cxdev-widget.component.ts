import {
	Component,
	computed,
	DOCUMENT,
	effect,
	ElementRef,
	inject,
	input,
	InputSignal,
	output,
	OutputEmitterRef,
	signal,
	ViewContainerRef,
} from '@angular/core';
import { CxdevDashboardWidgetConfig } from '../../cxdev-dashboard.model';
import { CommonModule } from '@angular/common';
import { CxdevComponentWrapperDirective } from '../../../../directives/cxdev-component-wrapper/cxdev-component-wrapper.directive';
import { CxdevDataAcquisition } from '../../../../directives/cxdev-component-wrapper/cxdev-data-acquisition.enum';
import { I18nModule } from '@spartacus/core';
import { ICON_TYPE, IconModule, LaunchDialogService } from '@spartacus/storefront';
import { CxdevDashboardWidgetSettingsService } from '../../cxdev-dashboard-widget-settings.service';
import { widgetSettingsKeyFor } from '../../cxdev-dashboard-widget-settings-key';
import { SETTING_BACKGROUND_COLOR } from '../cxdev-generic-widget-settings/cxdev-generic-widget-settings.component';

@Component({
	selector: 'cxdev-widget',
	imports: [CommonModule, CxdevComponentWrapperDirective, I18nModule, IconModule],
	templateUrl: './cxdev-widget.component.html',
	host: {
		'[style.--column-span]': 'columnSpan()',
		'[style.--row-span]': 'rowSpan()',
		'[style.--cxdev-widget-background-color]': 'backgroundColor()',
		'[class.edit-mode]': 'editMode()',
		'[class.is-resizing]': 'resizing()',
	},
})
export class CxdevWidgetComponent {
	config: InputSignal<CxdevDashboardWidgetConfig> = input.required();
	editMode: InputSignal<boolean> = input.required();

	resized: OutputEmitterRef<{ columnSpan: number; rowSpan: number }> = output();
	removed: OutputEmitterRef<void> = output();

	columnSpan = signal(1);
	rowSpan = signal(1);
	resizing = signal(false);

	element: ElementRef = inject(ElementRef);
	document: Document = inject(DOCUMENT);
	protected vcr = inject(ViewContainerRef);
	protected launchDialogService = inject(LaunchDialogService);
	protected widgetSettingsService = inject(CxdevDashboardWidgetSettingsService);

	dataAcquisitionType = CxdevDataAcquisition.MERGE;

	iconType = ICON_TYPE;

	protected startX!: number;
	protected startY!: number;
	protected startWidth!: number;
	protected startHeight!: number;
	protected columnCount!: number;
	protected columnWidth!: number;
	protected rowHeight!: number;
	protected columnGap!: number;
	protected rowGap!: number;
	protected direction!: 'horizontal' | 'vertical';
	protected widgetSettingsKey = computed(() => widgetSettingsKeyFor(this.config()));
	protected settings = computed(() =>
		this.widgetSettingsService.settingsFor(this.widgetSettingsKey())(),
	);
	protected backgroundColor = computed(() => this.settings()[SETTING_BACKGROUND_COLOR] || null);

	constructor() {
		effect(() => {
			this.columnSpan.set(this.minimumColumnSpan(this.config().columnSpan));
			this.rowSpan.set(this.minimumRowSpan(this.config().rowSpan));
		});
	}

	openSettings() {
		this.launchDialogService
			.openDialog('CXDEV_WIDGET_SETTINGS', this.element, this.vcr, { config: this.config() })
			?.subscribe();
	}

	remove() {
		this.removed.emit();
	}

	startResize(event: MouseEvent, direction: 'horizontal' | 'vertical') {
		event.preventDefault();
		this.updateGridMetrics();
		this.resizing.set(true);
		this.startX = event.clientX;
		this.startY = event.clientY;
		this.direction = direction;
		const itemRect = this.element.nativeElement.getBoundingClientRect();
		this.startWidth = itemRect.width;
		this.startHeight = itemRect.height;
		this.document.addEventListener('mousemove', this.handleMouseMove);
		this.document.addEventListener('mouseup', this.handleMouseUp);
	}

	private handleMouseMove = (event: MouseEvent) => {
		const newSpanX = this.sizeToSpan(
			this.startWidth + event.clientX - this.startX,
			this.columnWidth,
			this.columnGap,
			this.minimumColumnSpan(),
			this.columnCount,
		);
		const newSpanY = this.sizeToSpan(
			this.startHeight + event.clientY - this.startY,
			this.rowHeight,
			this.rowGap,
			this.minimumRowSpan(),
		);

		this.direction === 'horizontal' ? this.columnSpan.set(newSpanX) : this.rowSpan.set(newSpanY);
	};

	private updateGridMetrics() {
		const parent = this.element.nativeElement.parentElement as HTMLElement;
		const parentRect = parent.getBoundingClientRect();
		const styles = getComputedStyle(parent);

		this.columnGap = this.parseCssPixelValue(styles.columnGap || styles.gap);
		this.rowGap = this.parseCssPixelValue(styles.rowGap || styles.gap);
		this.columnCount =
			this.parseGridTrackCount(styles.gridTemplateColumns) ||
			this.parseCssNumber(styles.getPropertyValue('--grid-cols')) ||
			6;
		this.columnWidth =
			this.parseFirstGridTrackSize(styles.gridTemplateColumns) ||
			(parentRect.width - this.columnGap * (this.columnCount - 1)) / this.columnCount;
		this.rowHeight =
			this.parseFirstGridTrackSize(styles.gridAutoRows) ||
			this.parseCssPixelValue(styles.getPropertyValue('--rowHeight')) ||
			this.columnWidth;
	}

	private sizeToSpan(
		pixels: number,
		trackSize: number,
		gap: number,
		minSpan: number,
		maxSpan = Infinity,
	) {
		const span = Math.round((Math.max(trackSize, pixels) + gap) / (trackSize + gap));
		return Math.min(maxSpan, Math.max(minSpan, span));
	}

	private minimumColumnSpan(span = this.config().widget.minColumnSpan) {
		return Math.max(1, span);
	}

	private minimumRowSpan(span = this.config().widget.minRowSpan) {
		return Math.max(1, span);
	}

	private parseGridTrackCount(value: string) {
		return this.gridTrackValues(value).length;
	}

	private parseFirstGridTrackSize(value: string) {
		return this.parseCssPixelValue(this.gridTrackValues(value)[0]);
	}

	private gridTrackValues(value: string) {
		return value
			.split(' ')
			.map((track) => track.trim())
			.filter((track) => !!track && track !== 'none' && track !== 'auto');
	}

	private parseCssNumber(value: string) {
		const parsed = parseInt(value, 10);
		return Number.isFinite(parsed) ? parsed : 0;
	}

	private parseCssPixelValue(value: string) {
		const parsed = parseFloat(value);
		return Number.isFinite(parsed) ? parsed : 0;
	}

	private handleMouseUp = () => {
		this.document.removeEventListener('mousemove', this.handleMouseMove);
		this.document.removeEventListener('mouseup', this.handleMouseUp);
		this.resizing.set(false);

		const columnSpan = this.columnSpan();
		const rowSpan = this.rowSpan();

		if (columnSpan !== this.config().columnSpan || rowSpan !== this.config().rowSpan) {
			this.resized.emit({ columnSpan, rowSpan });
		}
	};
}
