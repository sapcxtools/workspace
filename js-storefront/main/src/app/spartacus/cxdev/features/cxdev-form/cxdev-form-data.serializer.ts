import { Injectable, InjectionToken } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Converter } from '@spartacus/core';
import { CxdevFormData, CxdevFormField, CxdevFormFieldValue } from './cxdev-form.model';

export interface CxdevFormDataSerializerSource {
	formData: CxdevFormData;
	formGroup: FormGroup;
}

export const CXDEV_FORM_DATA_SERIALIZER = new InjectionToken<
	Converter<CxdevFormDataSerializerSource, CxdevFormData>
>('CxdevFormDataSerializer');

@Injectable({
	providedIn: 'root',
})
export class CxdevFormDataSerializer implements Converter<CxdevFormDataSerializerSource, CxdevFormData> {
	convert(source: CxdevFormDataSerializerSource, target?: CxdevFormData): CxdevFormData {
		const formData = target ?? source.formData;

		return {
			...source.formData,
			...formData,
			formFields: this.serializeFormFields(source.formData.formFields ?? [], source.formGroup),
		};
	}

	private serializeFormFields(fields: CxdevFormField[], group: FormGroup): CxdevFormField[] {
		return fields.map((field) => this.serializeField(field, group));
	}

	private serializeField(field: CxdevFormField, group: FormGroup): CxdevFormField {
		const value = group.get(field.id)?.value;

		return {
			...field,
			value,
			formFieldValues: field.formFieldValues?.map((option) =>
				this.serializeFieldValue(field, option, value, group),
			),
		};
	}

	private serializeFieldValue(
		field: CxdevFormField,
		option: CxdevFormFieldValue,
		selectedValue: unknown,
		group: FormGroup,
	): CxdevFormFieldValue {
		if (!option.childFields?.length || String(option.id) !== String(selectedValue)) {
			return option;
		}

		const childGroup = group.get(`${field.id}Children`);

		if (!(childGroup instanceof FormGroup)) {
			return option;
		}

		return {
			...option,
			childFields: this.serializeFormFields(option.childFields, childGroup),
		};
	}
}
