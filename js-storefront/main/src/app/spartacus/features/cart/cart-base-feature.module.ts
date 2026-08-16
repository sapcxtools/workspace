import { NgModule } from '@angular/core';
import {
	cartBaseTranslationChunksConfig,
	cartBaseTranslationsEn,
} from '@spartacus/cart/base/assets';
import {
	ADD_TO_CART_FEATURE,
	CART_BASE_FEATURE,
	CartBaseRootModule,
	MINI_CART_FEATURE,
} from '@spartacus/cart/base/root';
import { CmsConfig, I18nConfig, provideConfig } from '@spartacus/core';

@NgModule({
	declarations: [],
	imports: [CartBaseRootModule],
	providers: [
		provideConfig(<CmsConfig>{
			featureModules: {
				[CART_BASE_FEATURE]: {
					module: () => import('./cart-base-wrapper.module').then((m) => m.CartBaseWrapperModule),
				},
			},
		}),
		provideConfig(<CmsConfig>{
			featureModules: {
				[MINI_CART_FEATURE]: {
					module: () =>
						import('@spartacus/cart/base/components/mini-cart').then((m) => m.MiniCartModule),
				},
			},
		}),
		provideConfig(<CmsConfig>{
			featureModules: {
				[ADD_TO_CART_FEATURE]: {
					module: () =>
						import('@spartacus/cart/base/components/add-to-cart').then((m) => m.AddToCartModule),
				},
			},
		}),
		provideConfig(<I18nConfig>{
			i18n: {
				resources: { en: cartBaseTranslationsEn },
				chunks: cartBaseTranslationChunksConfig,
			},
		}),
	],
})
export class CartBaseFeatureModule {}
