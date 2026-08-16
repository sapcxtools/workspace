import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Params, RouterModule } from '@angular/router';
import {
	FeatureDirective,
	I18nModule,
	isNotUndefined,
	RoutingService,
	TranslationService,
	UrlModule,
} from '@spartacus/core';
import { CmsComponentData, PaginationModule, SortingModule } from '@spartacus/storefront';
import { CxdevOrderHistoryComponentData } from './cxdev-order-history-component.model';
import { combineLatest, filter, map, Observable, switchMap, take, tap } from 'rxjs';
import {
	OrderHistoryFacade,
	ReplenishmentOrderHistoryFacade,
	OrderHistoryList,
	Order,
} from '@spartacus/order/root';

@Component({
	selector: 'cxdev-order-history',
	imports: [
		CommonModule,
		RouterModule,
		I18nModule,
		SortingModule,
		PaginationModule,
		UrlModule,
		FeatureDirective,
	],
	templateUrl: './cxdev-order-history.component.html',
})
export class CxdevOrderHistoryComponent {
	protected routing: RoutingService = inject(RoutingService);
	protected orderHistoryFacade: OrderHistoryFacade = inject(OrderHistoryFacade);
	protected translation: TranslationService = inject(TranslationService);
	protected replenishmentOrderHistoryFacade: ReplenishmentOrderHistoryFacade = inject(
		ReplenishmentOrderHistoryFacade,
	);

	private PAGE_SIZE = 5;
	sortType!: string;
	hasPONumber: boolean | undefined;

	protected data$: Observable<CxdevOrderHistoryComponentData> = inject(CmsComponentData)?.data$.pipe(
		tap((data) => {
			if (data?.context === 'widget') {
				this.PAGE_SIZE = 3;
			}
		}),
	);

	orders$: Observable<OrderHistoryList | undefined> = this.data$.pipe(
		switchMap(() => this.orderHistoryFacade.getOrderHistoryList(this.PAGE_SIZE)),
		tap((orders: OrderHistoryList | undefined) => {
			this.setOrderHistoryParams(orders);
		}),
	);

	setOrderHistoryParams(orders: OrderHistoryList | undefined) {
		if (orders?.pagination?.sort) {
			this.sortType = orders.pagination.sort;
		}
		this.hasPONumber = orders?.orders?.[0]?.purchaseOrderNumber !== undefined;
	}

	hasReplenishmentOrder$: Observable<boolean> = this.replenishmentOrderHistoryFacade
		.getReplenishmentOrderDetails()
		.pipe(map((order) => order && Object.keys(order).length !== 0));

	isLoaded$: Observable<boolean> = this.orderHistoryFacade.getOrderHistoryListLoaded();

	/**
	 * When "Order Return" feature is enabled, this component becomes one tab in
	 * TabParagraphContainerComponent. This can be read from TabParagraphContainer.
	 */
	tabTitleParam$: Observable<number> = this.orders$.pipe(
		map((order) => order?.pagination?.totalResults),
		filter(isNotUndefined),
		take(1),
	);

	ngOnDestroy(): void {
		this.orderHistoryFacade.clearOrderList();
	}

	changeSortCode(sortCode: string): void {
		const event: { sortCode: string; currentPage: number } = {
			sortCode,
			currentPage: 0,
		};
		this.sortType = sortCode;
		this.fetchOrders(event);
	}

	pageChange(page: number): void {
		const event: { sortCode: string; currentPage: number } = {
			sortCode: this.sortType,
			currentPage: page,
		};
		this.fetchOrders(event);
	}

	goToOrderDetail(order: Order): void {
		this.routing.go(
			{
				cxRoute: 'orderDetails',
				params: order,
			},
			{
				queryParams: this.getQueryParams(order),
			},
		);
	}

	getQueryParams(order: Order): Params | null {
		return this.orderHistoryFacade.getQueryParams(order);
	}

	getSortLabels(): Observable<{ byDate: string; byOrderNumber: string }> {
		return combineLatest([
			this.translation.translate('sorting.date'),
			this.translation.translate('sorting.orderNumber'),
		]).pipe(
			map(([textByDate, textByOrderNumber]) => {
				return {
					byDate: textByDate,
					byOrderNumber: textByOrderNumber,
				};
			}),
		);
	}

	private fetchOrders(event: { sortCode: string; currentPage: number }): void {
		this.orderHistoryFacade.loadOrderList(this.PAGE_SIZE, event.currentPage, event.sortCode);
	}
}
