import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Facet, FacetValue } from '@spartacus/core';
import { FacetService } from '@spartacus/storefront';
import { FacetDisplayType } from '../../../../models/facet.model';

@Component({
	selector: 'cxdev-select-facet',
	templateUrl: './select-facet.component.html',
	standalone: false,
})
export class SelectFacetComponent {
	@Input()
	set facet(value: Facet) {
		this.initializeFacetDisplay(value);
		this._facet = value;
	}

	_facet!: Facet;

	selectedOption: FacetValue | undefined;

	constructor(
		protected facetService: FacetService,
		protected router: Router,
	) {}

	initializeFacetDisplay(facet: Facet) {
		this.selectedOption = undefined;
		facet.values?.forEach((value) => {
			if (value.selected) this.selectedOption = value;
		});
	}

	onSubmit(value: FacetValue) {
		if (!value) {
			this.clearFacet();
		} else {
			const params: { [p: string]: string } = this.facetService.getLinkParams(
				value?.query?.query?.value!,
			);
			this.router.navigate([], { queryParams: { query: params['query'] } });
		}
	}

	clearFacet() {
		const params: { [p: string]: string } = this.facetService.getLinkParams(
			this._facet.removeQuery!.query?.value!,
		);
		this.router.navigate([], { queryParams: { query: params['query'] } });
	}

	protected readonly FacetDisplayType = FacetDisplayType;
}
