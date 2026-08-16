import { ChangeContext, LabelType, Options } from '@angular-slider/ngx-slider';
import { formatDate } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { FacetService } from '@spartacus/storefront';
import { RangeFacetService } from '../../../../services/range-facet.service';
import { CxdevRangeFacet, FacetRangeType } from '../../../../models/facet.model';

@Component({
	selector: 'cxdev-slider-facet',
	templateUrl: './slider-facet.component.html',
	standalone: false,
})
export class SliderFacetComponent {
	@Input()
	set facet(value: CxdevRangeFacet) {
		this.initializeSlider(value);
		this._facet = value;
	}

	_facet!: CxdevRangeFacet;

	options!: Options;
	min = 0;
	max = 0;
	from = 0;
	to = 0;
	private debounceTimer?: any;

	constructor(
		protected facetService: RangeFacetService,
		protected defaultFacetService: FacetService,
		protected router: Router,
	) {}

	initializeSlider(facet: CxdevRangeFacet) {
		this.min = facet.solrStatsField.min;
		this.max = facet.solrStatsField.max;
		this.from = facet.from || this.min;
		this.to = facet.to || this.max;

		if (facet.type === FacetRangeType.DATE) {
			this.options = {
				translate: (value: number, label: LabelType): string => {
					return formatDate(new Date(value), 'yyyy-MM-dd', 'en');
				},
				floor: parseInt('' + this.min),
				ceil: parseInt('' + this.max),
			};
		} else {
			this.options = {
				floor: parseFloat('' + this.min),
				ceil: parseFloat('' + this.max),
			};
		}
	}

	onSubmit(event: ChangeContext) {
		// Slider triggers too fast if no timeout is set so it has to be debounced
		clearTimeout(this.debounceTimer);
		this.debounceTimer = setTimeout(() => {
			this._facet = {
				...this._facet,
				from: event.value,
				to: event.highValue,
			};
			const params: { [p: string]: string } = this.facetService.getLinkParams(this._facet);
			this.router.navigate([], { queryParams: { query: params['query'] } });
			this.debounceTimer = false;
		}, 100);
	}

	clearFacet() {
		const params: { [p: string]: string } = this.defaultFacetService.getLinkParams(
			this._facet.removeQuery!.query?.value!,
		);
		this.router.navigate([], { queryParams: { query: params['query'] } });
	}

	showClearLink() {
		return this.from !== this.min || this.to !== this.max;
	}
}
