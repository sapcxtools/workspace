import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { FacetDisplayType } from '../../../models/facet.model';
import { SearchPage } from '../../../models/search.model';

@Component({
	selector: 'cxdev-facet-display',
	templateUrl: './facet-display.component.html',
	standalone: false,
})
export class FacetDisplayComponent {
	@Input('searchResult') searchResult$!: Observable<SearchPage>;
	FacetDisplayType = FacetDisplayType;

	constructor(protected router: Router) {}

	clearAll(page: SearchPage) {
		const sortValue = page.sorts?.find((value) => value.selected);

		if (sortValue === undefined) {
			this.router.navigate([], { queryParams: {} });
		} else {
			this.router.navigate([], { queryParams: { sortCode: sortValue!.code } });
		}
	}

	showClearAllLink(page: SearchPage): boolean {
		return (page.breadcrumbs?.length || 0) > 1;
	}
}
