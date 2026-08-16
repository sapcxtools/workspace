import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from '@spartacus/core';
import {
	requestedDeliveryDateTranslationChunksConfig,
	requestedDeliveryDateTranslationsEn,
} from '@spartacus/requested-delivery-date/assets';
import {
	REQUESTED_DELIVERY_DATE_FEATURE,
	RequestedDeliveryDateRootModule,
} from '@spartacus/requested-delivery-date/root';

@NgModule({
	declarations: [],
	imports: [RequestedDeliveryDateRootModule],
	providers: [
		provideConfig(<CmsConfig>{
			featureModules: {
				[REQUESTED_DELIVERY_DATE_FEATURE]: {
					module: () =>
						import('@spartacus/requested-delivery-date').then((m) => m.RequestedDeliveryDateModule),
				},
			},
		}),
		provideConfig(<I18nConfig>{
			i18n: {
				resources: { en: requestedDeliveryDateTranslationsEn },
				chunks: requestedDeliveryDateTranslationChunksConfig,
			},
		}),
	],
})
export class RequestedDeliveryDateModuleFeatureModule {}
