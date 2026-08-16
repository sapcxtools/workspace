import {
	Component,
	computed,
	ElementRef,
	inject,
	Signal,
	signal,
	ViewContainerRef,
	WritableSignal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CmsComponentData } from '@spartacus/storefront';
import { LaunchDialogService } from '@spartacus/storefront';
import { CxdevFormListCmsComponentData, CxdevFormListViewMode } from '../../cxdev-form-list.model';
import { CxdevFormListComponent } from '../cxdev-form-list/cxdev-form-list.component';

@Component({
	selector: 'cxdev-form-list-cms',
	imports: [CxdevFormListComponent],
	templateUrl: './cxdev-form-list-cms.component.html',
})
export class CxdevFormListCmsComponent {
	private component = inject(CmsComponentData<CxdevFormListCmsComponentData>);
	private launchDialogService = inject(LaunchDialogService);
	private elementRef = inject(ElementRef);
	private vcr = inject(ViewContainerRef);

	protected readonly CxdevFormListViewMode = CxdevFormListViewMode;

	protected data: Signal<CxdevFormListCmsComponentData> = toSignal(this.component.data$, {
		initialValue: {} as CxdevFormListCmsComponentData,
	});

	protected formIds: Signal<string[]> = computed(() =>
		this.data().forms ? this.data().forms.split(' ') : [],
	);

	protected viewMode: Signal<CxdevFormListViewMode> = computed(
		() => this.data().viewMode ?? CxdevFormListViewMode.INLINE,
	);

	protected isInlineVisible: WritableSignal<boolean> = signal(false);

	toggleInline(): void {
		this.isInlineVisible.update((v) => !v);
	}

	openModal(): void {
		this.launchDialogService
			.openDialog('CXDEV_FORM_LIST', this.elementRef, this.vcr, {
				formIds: this.formIds(),
				headline: this.data().headline,
			})
			?.subscribe();
	}
}
