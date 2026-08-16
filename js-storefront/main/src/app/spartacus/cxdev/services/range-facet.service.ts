import { HttpUrlEncodingCodec } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FacetService as CxFacetService } from '@spartacus/storefront';
import { CxdevRangeFacet } from '../models/facet.model';

@Injectable({ providedIn: 'root' })
export class RangeFacetService {
	constructor(
		protected activatedRoute: ActivatedRoute,
		protected facetServiceDelegate: CxFacetService,
	) {}

	getLinkParams(facet: CxdevRangeFacet, currentQuery?: string) {
		let decodedQuery = this.decodeQuery(facet, currentQuery);
		const newParam = `:${facet.solrStatsField.name}:${this.buildParamFromRange(facet.from || 0, facet.to || 0)}`;
		const regExp = this.getRegex(facet.solrStatsField.name);

		if (!!decodedQuery.match(regExp)) {
			// facet is already activated
			decodedQuery = decodedQuery.replace(regExp, newParam);
		} else {
			// facet is not activated, can simply be added to query
			decodedQuery += newParam;
		}
		return this.facetServiceDelegate.getLinkParams(decodedQuery);
	}

	removeFacetValues(facet: CxdevRangeFacet, currentQuery?: string) {
		let decodedQuery = this.decodeQuery(facet, currentQuery);
		const regExp = this.getRegex(facet.solrStatsField.name);

		if (!!decodedQuery.match(regExp)) {
			decodedQuery = decodedQuery.replace(regExp, '');
		}
		return this.facetServiceDelegate.getLinkParams(decodedQuery);
	}

	getRangeFromParam(query: string): { from: string | number; to: string | number } {
		const matches = query.match(/\[(\w+) TO (\w+)\]/g);
		if (!!matches)
			return {
				from: matches[1],
				to: matches[2],
			};
		return {
			from: '0',
			to: '0',
		};
	}

	protected getRegex(facetName: string) {
		return new RegExp(`:${facetName}:\\[\\w+ TO \\w+\\]`, 'g');
	}

	protected decodeQuery(facet: CxdevRangeFacet, currentQuery?: string) {
		if (!currentQuery) {
			currentQuery =
				this.activatedRoute.snapshot.queryParamMap.get('query') || this.getCurrentQuery(facet);
		}
		return new HttpUrlEncodingCodec().decodeValue(currentQuery).replace(/\+/g, ' ');
	}

	protected buildParamFromRange(from: string | number, to: string | number) {
		return `[${from} TO ${to}]`;
	}

	protected getCurrentQuery(facet: CxdevRangeFacet) {
		if (!!facet.values && facet.values.length > 0) {
			// Take a value from the facet and remove the newest changes to get the current Query
			const queryParts = facet.values[0].query?.query?.value?.split(':');
			if (!!queryParts) {
				// Remove last 2 items from array
				return queryParts.slice(0, -2).join(':');
			}
		}
		return '';
	}
}
