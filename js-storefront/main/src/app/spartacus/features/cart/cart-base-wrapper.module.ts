import { NgModule } from '@angular/core';
import { CartBaseModule } from '@spartacus/cart/base';
import { EstimatedDeliveryDateModule } from '@spartacus/estimated-delivery-date';

@NgModule({
	declarations: [],
	imports: [CartBaseModule, EstimatedDeliveryDateModule],
})
export class CartBaseWrapperModule {}
