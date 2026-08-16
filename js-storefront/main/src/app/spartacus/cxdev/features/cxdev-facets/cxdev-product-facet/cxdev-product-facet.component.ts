import { Component, OnDestroy } from '@angular/core';
import { ProductSearchPage, ProductSearchService } from '@spartacus/core';
import { Observable } from 'rxjs';

@Component({
	selector: 'cxdev-product-facet',
	templateUrl: './cxdev-product-facet.component.html',
	standalone: false,
})
export class CxdevProductFacetComponent implements OnDestroy {
	searchResult$: Observable<ProductSearchPage>;

	constructor(public facetService: ProductSearchService) {
		this.searchResult$ = facetService.getResults();
	}

	ngOnDestroy(): void {}
}
