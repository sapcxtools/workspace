import { Breadcrumb, Facet, PaginationModel, SearchState, SortModel } from '@spartacus/core';

export interface SearchPage {
	freeTextSearch?: string;
	sorts?: SortModel[];
	pagination?: PaginationModel;
	currentQuery?: SearchState;
	facets?: Facet[];
	breadcrumbs?: Breadcrumb[];
}

export interface SearchRouteParams {
	query?: string;
}
