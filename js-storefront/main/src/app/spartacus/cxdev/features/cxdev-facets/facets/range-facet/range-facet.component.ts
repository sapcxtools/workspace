import { Component, ElementRef, Input, OnInit, viewChild } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { FacetService } from '@spartacus/storefront';
import { RangeFacetService } from '../../../../services/range-facet.service';
import { CxdevRangeFacet, FacetRangeType } from '../../../../models/facet.model';

@Component({
	selector: 'cxdev-range-facet',
	templateUrl: './range-facet.component.html',
	standalone: false,
})
export class RangeFacetComponent {
	@Input()
	set facet(value: CxdevRangeFacet) {
		this.initializeForm(value);
		this._facet = value;
	}

	_facet!: CxdevRangeFacet;

	fromControl = viewChild<ElementRef>('rangeFrom');
	toControl = viewChild<ElementRef>('rangeTo');

	facetForm!: UntypedFormGroup;
	FacetRangeType = FacetRangeType;
	min?: number | string;
	max?: number | string;

	constructor(
		protected facetService: RangeFacetService,
		protected defaultFacetService: FacetService,
		protected router: Router,
	) {}

	initializeForm(facet: CxdevRangeFacet) {
		if (facet.type === FacetRangeType.LONG) {
			// add / subtract one month for min and max
			this.min = this.getDateInputFormat(parseInt('' + facet.solrStatsField.min) - 2629800000);
			this.max = this.getDateInputFormat(parseInt('' + facet.solrStatsField.max) + 2629800000);

			this.facetForm = new UntypedFormGroup({
				from: new UntypedFormControl(
					facet.from ? this.getDateInputFormat(facet.from) : this.min,
					Validators.required,
				),
				to: new UntypedFormControl(
					facet.to ? this.getDateInputFormat(facet.to) : this.max,
					Validators.required,
				),
			});
		} else {
			this.min = facet.solrStatsField.min;
			this.max = facet.solrStatsField.max;

			this.facetForm = new UntypedFormGroup({
				from: new UntypedFormControl(facet.from || this.min, Validators.required),
				to: new UntypedFormControl(facet.to || this.max, Validators.required),
			});
		}
	}

	onChange() {
		if (!this.from) {
			this.fromControl()?.nativeElement.focus();
		} else if (!this.to) {
			this.toControl()?.nativeElement.focus();
		} else {
			this.submit();
		}
	}

	submit() {
		let from, to;
		if (this._facet.type === FacetRangeType.LONG) {
			from = Date.parse(this.from);
			to = Date.parse(this.to);
		} else {
			from = this.from;
			to = this.to;
		}

		this._facet = {
			...this._facet,
			from: from,
			to: to,
		};
		const params: { [p: string]: string } = this.facetService.getLinkParams(this._facet);
		this.router.navigate([], { queryParams: { query: params['query'] } });
	}

	private getDateInputFormat(timestamp: string | number) {
		return new Date(parseInt(<string>timestamp)).toISOString().split('T')[0];
	}

	get from() {
		return this.facetForm.get('from')?.value;
	}

	get to() {
		return this.facetForm.get('to')?.value;
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
