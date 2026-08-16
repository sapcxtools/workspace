import { NgModule } from '@angular/core';
import { CXDEV_FORM_DATA_NORMALIZER, CxdevFormDataNormalizer } from './cxdev-form-data.normalizer';
import { CXDEV_FORM_DATA_SERIALIZER, CxdevFormDataSerializer } from './cxdev-form-data.serializer';

@NgModule({
	providers: [
		{ provide: CXDEV_FORM_DATA_NORMALIZER, useClass: CxdevFormDataNormalizer, multi: true },
		{ provide: CXDEV_FORM_DATA_SERIALIZER, useClass: CxdevFormDataSerializer, multi: true },
	],
})
export class CxdevFormModule {}
