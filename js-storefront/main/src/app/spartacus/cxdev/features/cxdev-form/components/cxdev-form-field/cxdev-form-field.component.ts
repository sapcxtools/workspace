import { Component, DestroyRef, inject, input, InputSignal, OnInit, signal } from '@angular/core';
import { CxdevFormField } from '../../cxdev-form.model';
import { CommonModule } from '@angular/common';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { FormErrorsModule } from '@spartacus/storefront';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CxdevFormService } from '../../cxdev-form.service';

@Component({
	selector: 'cxdev-form-field',
	imports: [CommonModule, ReactiveFormsModule, FormErrorsModule],
	templateUrl: './cxdev-form-field.component.html',
})
export class CxdevFormFieldComponent implements OnInit {
	field: InputSignal<CxdevFormField> = input.required();
	formGroup: InputSignal<FormGroup> = input.required();

	childFields = signal(null as CxdevFormField[] | null);

	protected formService = inject(CxdevFormService);
	protected destroyRef = inject(DestroyRef);

	ngOnInit(): void {
		const control = this.formGroup().get(this.field().id);

		this.updateChildFields(control?.value);

		control?.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((value) => {
			this.updateChildFields(value);
		});
	}

	childFormGroup(): FormGroup | null {
		return this.formService.getChildFormGroup(this.formGroup(), this.field());
	}

	protected updateChildFields(value: any): void {
		const childFields = this.formService.updateChildControls(this.formGroup(), this.field(), value);
		this.childFields.set(childFields);
	}
}
