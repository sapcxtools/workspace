import {
	B2BUnit,
	Breadcrumb,
	CmsComponent,
	Facet,
	Product,
	SortModel,
	User,
} from '@spartacus/core';

export interface CxdevServiceTicketSearchPage {
	breadcrumbs: Breadcrumb[];
	facets: Facet[];
	pagination: CxdevPagination;
	results: CxdevServiceTicket[];
	sorts: SortModel[];
}

export interface CxdevServiceTicket {
	code?: string;
	type?: string;
	typeCode?: string;
	status?: string;
	title?: string;
	description?: string;
	machines?: Product[];
	allMachines?: boolean;
	customer?: User;
	creationTime?: string | Date;
	isNew?: boolean;
	parentAccountGroup?: B2BUnit;
	form?: string;
}

export interface CxdevPagination {
	currentPage?: number;
	pageSize?: number;
	sort?: string;
	totalPages?: number;
	totalResults?: number;
}

export interface CxdevServiceRequestFormComponentData extends CmsComponent {
	formId: string;
}
