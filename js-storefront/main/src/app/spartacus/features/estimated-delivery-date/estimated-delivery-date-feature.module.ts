import { NgModule } from '@angular/core';
import { I18nConfig, provideConfig } from '@spartacus/core';
import {
	estimatedDeliveryDateTranslationChunksConfig,
	estimatedDeliveryDateTranslationsEn,
} from '@spartacus/estimated-delivery-date/assets';
import { EstimatedDeliveryDateRootModule } from '@spartacus/estimated-delivery-date/root';

@NgModule({
	declarations: [],
	imports: [EstimatedDeliveryDateRootModule],
	providers: [
		provideConfig(<I18nConfig>{
			i18n: {
				resources: { en: estimatedDeliveryDateTranslationsEn },
				chunks: estimatedDeliveryDateTranslationChunksConfig,
			},
		}),
	],
})
export class EstimatedDeliveryDateFeatureModule {}
