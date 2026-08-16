import { CmsComponent } from '@spartacus/core';

export enum CxdevFormListViewMode {
	INLINE = 'INLINE',
	TOGGLE_INLINE = 'TOGGLE_INLINE',
	TOGGLE_MODAL = 'TOGGLE_MODAL',
}

export interface CxdevFormListCmsComponentData extends CmsComponent {
	forms: string;
	viewMode: CxdevFormListViewMode;
	headline?: string;
	description?: string;
	toggleText?: string;
}
