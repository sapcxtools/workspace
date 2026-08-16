import { CommonModule } from '@angular/common';
import {
	Component,
	computed,
	inject,
	input,
	InputSignal,
	output,
	OutputEmitterRef,
	Signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { GlobalMessageService, GlobalMessageType, I18nModule, Translatable } from '@spartacus/core';
import { switchMap } from 'rxjs';
import { CxdevFormData } from '../../cxdev-form.model';
import { CxdevFormService } from '../../cxdev-form.service';
import { CxdevFormFieldComponent } from '../cxdev-form-field/cxdev-form-field.component';
import { ICON_TYPE, IconModule } from '@spartacus/storefront';

@Component({
	selector: 'cxdev-form',
	imports: [CommonModule, ReactiveFormsModule, I18nModule, CxdevFormFieldComponent, IconModule],
	providers: [CxdevFormService],
	templateUrl: './cxdev-form.component.html',
})
export class CxdevFormComponent {
	formId: InputSignal<string> = input.required();

	onSuccess: OutputEmitterRef<any> = output<any>();
	onError: OutputEmitterRef<any> = output<any>();

	protected formService = inject(CxdevFormService);
	protected globalMessage = inject(GlobalMessageService);

	protected formData: Signal<CxdevFormData> = toSignal(
		toObservable(this.formId).pipe(
			switchMap((formId) => this.formService.getFormDataForId(formId)),
		),
		{ initialValue: {} as CxdevFormData },
	);

	formGroup: Signal<FormGroup> = computed(() =>
		this.formService.createFormGroupForData(this.formData()),
	);

	iconType = ICON_TYPE;

	submit(): void {
		if (this.formGroup().valid) {
			this.formService.submitForm(this.formData(), this.formGroup()).subscribe({
				next: (response) => {
					this.onSuccess.emit(response);
					this.globalMessage.add(
						{
							key: this.formData()?.successMessage || 'forms.successMessage',
						} as Translatable,
						GlobalMessageType.MSG_TYPE_CONFIRMATION,
					);
					this.formGroup().reset();
				},
				error: (error) => {
					this.onError.emit(error);
					this.globalMessage.add(
						{
							key: this.formData()?.errorMessage || 'forms.errorMessage',
						} as Translatable,
						GlobalMessageType.MSG_TYPE_ERROR,
					);
					console.error('Error submitting form', error);
				},
			});
		} else {
			this.formGroup().markAllAsTouched();
		}
	}

	resetForm(): void {
		this.formGroup().reset();
	}
}
