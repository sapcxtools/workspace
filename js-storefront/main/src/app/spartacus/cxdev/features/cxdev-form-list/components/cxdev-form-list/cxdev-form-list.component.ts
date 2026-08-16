import {
	Component,
	computed,
	inject,
	input,
	InputSignal,
	Signal,
	signal,
	WritableSignal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { forkJoin, map, of, switchMap } from 'rxjs';
import { CxdevFormData } from '../../../cxdev-form/cxdev-form.model';
import { CxdevFormService } from '../../../cxdev-form/cxdev-form.service';
import { CxdevFormComponent } from '../../../cxdev-form/components/cxdev-form/cxdev-form.component';
import { ICON_TYPE, IconModule } from '@spartacus/storefront';

@Component({
	selector: 'cxdev-form-list',
	imports: [CxdevFormComponent, IconModule],
	providers: [CxdevFormService],
	templateUrl: './cxdev-form-list.component.html',
})
export class CxdevFormListComponent {
	formIds: InputSignal<string[]> = input.required<string[]>();
	iconType = ICON_TYPE;

	private formService = inject(CxdevFormService);

	protected forms: Signal<CxdevFormData[]> = toSignal(
		toObservable(this.formIds).pipe(
			switchMap((ids) =>
				ids.length ? forkJoin(ids.map((id) => this.formService.getFormDataForId(id))) : of([]),
			),
			map((forms) => {
				if (forms.length === 1) {
					this.selectedFormId.set(forms[0].id);
				}
				return forms;
			}),
		),
		{ initialValue: [] },
	);

	protected selectedFormId: WritableSignal<string | null | undefined> = signal(null);

	protected selectedForm: Signal<CxdevFormData | undefined> = computed(() =>
		this.forms().find((f) => f.id === this.selectedFormId()),
	);

	selectForm(formId: string): void {
		this.selectedFormId.set(formId);
	}

	onToggle(event: ToggleEvent, formId: string): void {
		if ((event.target as HTMLDetailsElement)?.open) {
			this.selectForm(formId);
		}
	}
}
