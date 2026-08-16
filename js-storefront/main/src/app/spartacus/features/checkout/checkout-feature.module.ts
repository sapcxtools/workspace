import { NgModule } from '@angular/core';
import { CheckoutB2BRootModule } from '@spartacus/checkout/b2b/root';
import { CHECKOUT_FEATURE, CheckoutRootModule } from '@spartacus/checkout/base/root';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { CxdevCheckoutProgressGuard } from './guards/cxdev-checkout-progress.guard';

@NgModule({
	declarations: [],
	imports: [CheckoutRootModule, CheckoutB2BRootModule],
	providers: [
		provideConfig(<CmsConfig>{
			featureModules: {
				[CHECKOUT_FEATURE]: {
					module: () => import('./checkout-wrapper.module').then((m) => m.CheckoutWrapperModule),
				},
			},
			cmsComponents: {
				CheckoutProgress: {
					guards: [CheckoutAuthGuard, CartNotEmptyGuard, CxdevCheckoutProgressGuard],
				},
				CheckoutProgressMobileTop: {
					guards: [CheckoutAuthGuard, CartNotEmptyGuard, CxdevCheckoutProgressGuard],
				},
				CheckoutProgressMobileBottom: {
					guards: [CheckoutAuthGuard, CartNotEmptyGuard, CxdevCheckoutProgressGuard],
				},
			},
		}),
	],
})
export class CheckoutFeatureModule {}
