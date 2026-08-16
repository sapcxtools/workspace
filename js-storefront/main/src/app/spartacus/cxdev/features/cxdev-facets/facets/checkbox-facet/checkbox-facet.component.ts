import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Facet, FacetValue } from '@spartacus/core';
import { FacetCollapseState, FacetService } from '@spartacus/storefront';
import { Observable } from 'rxjs';

@Component({
	selector: 'cxdev-checkbox-facet',
	templateUrl: './checkbox-facet.component.html',
	standalone: false,
})
export class CheckboxFacetComponent implements OnInit {
	protected _facet!: Facet;
	state$?: Observable<FacetCollapseState>;

	constructor(
		protected facetService: FacetService,
		protected router: Router,
	) {}

	@Input()
	set facet(value: Facet) {
		this._facet = value;
		this.state$ = this.facetService.getState(value);
	}

	get facet(): Facet {
		return this._facet;
	}

	ngOnInit(): void {}

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
			this.facet.removeQuery!.query?.value!,
		);
		this.router.navigate([], { queryParams: { query: params['query'] } });
	}

	showClearLink() {
		const select = this.facet.values!.find((facetValue) => facetValue.selected == true);
		return !!select;
	}
}
