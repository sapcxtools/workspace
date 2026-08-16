import { Converter, Translatable } from '@spartacus/core';
import { CxdevFormData, CxdevFormFieldType } from './cxdev-form.model';
import { Injectable, InjectionToken } from '@angular/core';
import { Validators } from '@angular/forms';

export const CXDEV_FORM_DATA_NORMALIZER = new InjectionToken<Converter<CxdevFormData, CxdevFormData>>(
	'CxdevFormDataNormalizer',
);

@Injectable({ providedIn: 'root' })
export class CxdevFormDataNormalizer implements Converter<CxdevFormData, CxdevFormData> {
	convert(source: CxdevFormData, target?: CxdevFormData): CxdevFormData {
		target = target ?? { ...source };

		target.formFields = this.transformFormFields(target.formFields);

		return { ...source, ...target };
	}

	private transformFormFields(fields: any[]): any[] {
		return fields.map((field) => {
			field.fieldType = field.fieldType?.toLowerCase();
			this.setValidatorBasedOnFieldType(field);
			if (field.formFieldValues) {
				field.formFieldValues = this.transformFormFields(field.formFieldValues);
			}
			if (field.childFields) {
				field.childFields = this.transformFormFields(field.childFields);
			}
			return field;
		});
	}

	private setValidatorBasedOnFieldType(field: any) {
		field.validators = field.validators || [];

		switch (field.fieldType) {
			case CxdevFormFieldType.EMAIL:
				field.validators.push(Validators.email);
				break;
			case CxdevFormFieldType.NUMBER:
				field.validators.push(Validators.pattern(/^\d+$/));
				break;
			case CxdevFormFieldType.DATE:
				field.validators.push(Validators.pattern(/^\d{4}-\d{2}-\d{2}$/));
				break;
			case CxdevFormFieldType.WEEK:
				field.validators.push(Validators.pattern(/^\d{4}-W\d{2}$/));
				break;
			default:
				break;
		}
	}
}
