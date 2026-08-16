import { ValidatorFn } from '@angular/forms';
import { Translatable } from '@spartacus/core';

export interface CxdevFormData {
	id?: string;
	title?: string;
	type?: string;
	description?: Translatable | string;
	recipients?: string[];
	dynamicRecipients?: boolean;
	endpoint: string;
	formFields: CxdevFormField[];
	machines?: string[];
	successMessage?: Translatable | string;
	errorMessage?: Translatable | string;
	submitMessage?: Translatable | string;
}

export interface CxdevFormField {
	id: string;
	description?: string;
	name: string;
	fieldType: string;
	required?: boolean;
	hidden?: boolean;
	label?: Translatable | string;
	value?: any;
	defaultValue?: any;
	formFieldValues?: CxdevFormFieldValue[];
	minValue?: number;
	maxValue?: number;
	minLength?: number;
	maxLength?: number;
	placeholder?: Translatable | string;
	validators?: ValidatorFn[];
}

export interface CxdevFormFieldValue {
	id: string;
	label: string;
	childFields?: CxdevFormField[];
}

export enum CxdevFormFieldType {
	TEXT = 'text',
	EMAIL = 'email',
	TEXTAREA = 'textarea',
	SELECT = 'select',
	CHECKBOX = 'checkbox',
	CHECKBOXES = 'checkboxs',
	RADIO = 'radio',
	NUMBER = 'number',
	DATE = 'date',
	WEEK = 'week',
	FILE = 'file',
	COLOR = 'color',
	PASSWORD = 'password',
}
