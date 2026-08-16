import { Facet, SearchState } from '@spartacus/core';

declare module '@spartacus/core' {
	interface Facet {
		from?: number;
		to?: number;
		removeQuery?: SearchState;
	}
}

export interface CxdevFacet extends Facet {
	facetDisplayType: FacetDisplayType;
}

export interface CxdevRangeFacet extends CxdevFacet {
	type?: FacetRangeType;
	from?: number;
	to?: number;
	solrStatsField: {
		name: string;
		min: number;
		max: number;
		count?: number;
	};
}

export enum FacetDisplayType {
	RADIO = 'radio',
	CHECKBOX = 'checkbox',
	SELECT = 'select',
	RANGE = 'range',
	SLIDER = 'slider',
	AUTOCOMPLETE = 'autocomplete',
}

export enum FacetRangeType {
	INT = 'int',
	LONG = 'long',
	DATE = 'date',
}
