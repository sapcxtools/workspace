import { Component, inject } from '@angular/core';
import { CmsComponentData, MediaComponent } from '@spartacus/storefront';
import { CxdevDashboardProductListCmsComponent } from '../../cxdev-dashboard.model';
import { AsyncPipe } from '@angular/common';
import {
	ProductSearchPage,
	ProductSearchService,
	SearchConfig,
	TranslatePipe,
} from '@spartacus/core';
import { Observable, tap } from 'rxjs';
import { RouterLink } from '@angular/router';

@Component({
	selector: 'cxdev-dashboard-product-list',
	imports: [AsyncPipe, TranslatePipe, MediaComponent, RouterLink],
	templateUrl: './cxdev-dashboard-product-list.component.html',
})
export class CxdevDashboardProductListComponent {
	productSearchService = inject(ProductSearchService);
	component: CmsComponentData<CxdevDashboardProductListCmsComponent> = inject(CmsComponentData);
	data$: Observable<CxdevDashboardProductListCmsComponent> = this.component.data$.pipe(
		tap((data) => {
			const searchConfig: SearchConfig = {
				pageSize: data.productCount,
				currentPage: 0,
				filters: `category:${data.category}`,
			};
			this.productSearchService.search(':relevance', searchConfig);
		}),
	);
	equipment$: Observable<ProductSearchPage> = this.productSearchService.getResults();
}
