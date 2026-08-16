import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { OrderDetailsService } from '@spartacus/order/components';
import { I18nModule } from '@spartacus/core';

@Component({
	selector: 'cxdev-order-tracking-component',
	imports: [AsyncPipe, I18nModule],
	templateUrl: './cxdev-order-tracking-component.html',
})
export class CxdevOrderTrackingComponent {
	protected orderDetailsService: OrderDetailsService = inject(OrderDetailsService);

	order$ = this.orderDetailsService.getOrderDetails();
}
