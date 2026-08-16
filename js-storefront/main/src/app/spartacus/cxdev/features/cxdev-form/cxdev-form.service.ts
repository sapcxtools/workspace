import { inject, Injectable } from '@angular/core';
import { CxdevFormData, CxdevFormField } from './cxdev-form.model';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ConverterService, OccEndpointsService } from '@spartacus/core';
import { concatMap, Observable, of } from 'rxjs';
import { CXDEV_FORM_DATA_NORMALIZER } from './cxdev-form-data.normalizer';
import { CXDEV_FORM_DATA_SERIALIZER } from './cxdev-form-data.serializer';

@Injectable()
export class CxdevFormService {
	protected http = inject(HttpClient);
	protected endpointService = inject(OccEndpointsService);
	protected converter = inject(ConverterService);

	getFormDataForId(formId: string): Observable<CxdevFormData> {
		return this.http
			.get<CxdevFormData>(
				this.endpointService.buildUrl('serviceRequestForm', { urlParams: { formId } }),
			)
			.pipe(
				concatMap((data) => of(data || ({} as CxdevFormData))),
				this.converter.pipeable(CXDEV_FORM_DATA_NORMALIZER),
			);
	}

	createFormGroupForData(formData: CxdevFormData): FormGroup {
		return this.createFormGroupForFields(formData?.formFields || []);
	}

	createFormGroupForFields(fields: CxdevFormField[]): FormGroup {
		return fields.reduce((group: FormGroup, field: CxdevFormField) => {
			const control = new FormControl<any>(this.getInitialValue(field), this.getValidators(field));
			group.addControl(field.id, control);

			this.updateChildControls(group, field, control.value);

			return group;
		}, new FormGroup({}));
	}

	updateChildControls(
		group: FormGroup,
		field: CxdevFormField,
		selectedValue: any,
	): CxdevFormField[] | null {
		const childGroupName = this.getChildGroupName(field);
		const childFields = this.getSelectedChildFields(field, selectedValue);

		if (group.contains(childGroupName)) {
			group.removeControl(childGroupName);
		}

		if (childFields?.length) {
			group.addControl(childGroupName, this.createFormGroupForFields(childFields));
			return childFields;
		}

		return null;
	}

	getChildFormGroup(group: FormGroup, field: CxdevFormField): FormGroup | null {
		return (group.get(this.getChildGroupName(field)) as FormGroup | null) || null;
	}

	getChildGroupName(field: CxdevFormField): string {
		return `${field.id}Children`;
	}

	getSelectedChildFields(field: CxdevFormField, selectedValue: any): CxdevFormField[] | null {
		return (
			field.formFieldValues?.find((option) => String(option.id) === String(selectedValue))
				?.childFields || null
		);
	}

	protected getInitialValue(field: CxdevFormField): any {
		return field.value ?? field.defaultValue ?? '';
	}

	protected getValidators(field: CxdevFormField) {
		const validators = [...(field.validators || [])];

		if (field.required) {
			validators.push(Validators.required);
		}

		return validators;
	}

	submitForm(formData: CxdevFormData | undefined, formGroup: FormGroup) {
		const url = this.endpointService.buildUrl(formData?.endpoint || 'createServiceTicket');
		const body = this.converter.convert(
			{ formData: formData ?? ({} as CxdevFormData), formGroup },
			CXDEV_FORM_DATA_SERIALIZER,
		);

		return this.http.post(url, body, {
			responseType: 'text',
		});
	}
}
