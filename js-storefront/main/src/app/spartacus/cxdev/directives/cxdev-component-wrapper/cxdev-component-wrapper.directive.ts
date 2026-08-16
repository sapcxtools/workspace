import {
	ChangeDetectorRef,
	ComponentRef,
	Directive,
	ElementRef,
	EventEmitter,
	Injector,
	Input,
	Output,
	Renderer2,
	Type,
	ViewContainerRef,
} from '@angular/core';
import {
	ContentSlotComponentData,
	DynamicAttributeService,
	EventService,
	isNotUndefined,
} from '@spartacus/core';
import {
	CmsComponentsService,
	ComponentHandlerService,
	ComponentCreateEvent,
	ComponentDestroyEvent,
	ComponentEvent,
} from '@spartacus/storefront';
import { Subscription, filter, tap, finalize } from 'rxjs';
import { CxdevCmsInjectorService } from './cxdev-cms-injector.service';
import { CxdevDataAcquisition } from './cxdev-data-acquisition.enum';

@Directive({
	selector: '[cxdevComponentWrapper]',
})
export class CxdevComponentWrapperDirective {
	@Input() cxdevComponentWrapper!: ContentSlotComponentData;
	@Input() cxdevComponentWrapperType?: CxdevDataAcquisition;
	@Output() cxdevComponentRef = new EventEmitter<ComponentRef<any>>();

	/**
	 * This property in unsafe, i.e.
	 * - cmpRef can be set later because of lazy loading or deferred loading
	 * - cmpRef can be not set at all if for example, web components are used as cms components
	 */
	protected cmpRef?: ComponentRef<any>;

	private launcherResource?: Subscription;

	constructor(
		protected vcr: ViewContainerRef,
		protected cmsComponentsService: CmsComponentsService,
		protected injector: Injector,
		protected dynamicAttributeService: DynamicAttributeService,
		protected renderer: Renderer2,
		protected componentHandler: ComponentHandlerService,
		protected cmsInjector: CxdevCmsInjectorService,
		protected eventService: EventService,
	) {}

	ngOnInit() {
		this.cmsComponentsService
			.determineMappings([this.cxdevComponentWrapper.flexType ?? ''])
			.subscribe(() => {
				if (this.cmsComponentsService.shouldRender(this.cxdevComponentWrapper.flexType ?? '')) {
					this.launchComponent();
				}
			});
	}

	private launchComponent() {
		const componentMapping = this.cmsComponentsService.getMapping(
			this.cxdevComponentWrapper.flexType ?? '',
		);

		if (!componentMapping) {
			return;
		}

		this.launcherResource = this.componentHandler
			.getLauncher(
				componentMapping,
				this.vcr,
				this.cmsInjector.getInjector(
					this.cxdevComponentWrapper.flexType ?? '',
					this.cxdevComponentWrapper.uid ?? '',
					this.cxdevComponentWrapper.content ?? {},
					this.cxdevComponentWrapperType,
					this.injector,
				),
				this.cmsComponentsService.getModule(this.cxdevComponentWrapper.flexType ?? ''),
			)
			?.pipe(
				filter(isNotUndefined),
				tap(({ elementRef, componentRef }) => {
					this.cmpRef = componentRef;

					this.cxdevComponentRef.emit(componentRef);

					this.dispatchEvent(ComponentCreateEvent, elementRef);
					this.decorate(elementRef);
					this.injector.get(ChangeDetectorRef).markForCheck();
				}),
				finalize(() => this.dispatchEvent(ComponentDestroyEvent)),
			)
			.subscribe();
	}

	/**
	 * Dispatch the component event.
	 *
	 * The event is dispatched during creation and removal of the component.
	 */
	protected dispatchEvent(event: Type<ComponentEvent>, elementRef?: ElementRef) {
		const payload = {
			typeCode: this.cxdevComponentWrapper.typeCode,
			id: this.cxdevComponentWrapper.uid,
		} as ComponentEvent;
		if (event === ComponentCreateEvent) {
			(payload as ComponentCreateEvent).host = elementRef?.nativeElement;
		}
		this.eventService.dispatch(payload, event);
	}

	private decorate(elementRef: ElementRef): void {
		this.dynamicAttributeService.addAttributesToComponent(
			elementRef.nativeElement,
			this.renderer,
			this.cxdevComponentWrapper,
		);
	}

	ngOnDestroy() {
		if (this.launcherResource) {
			this.launcherResource.unsubscribe();
		}
	}
}
