import { AsyncPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ActivatedRoute, Params, Router, RouterLink } from '@angular/router';
import { Observable, switchMap } from 'rxjs';
import { TranslatePipe, UrlModule } from '@spartacus/core';

import { CxdevServiceRequestService } from '../../cxdev-service-request.service';
import { CxdevServiceTicketSearchPage } from '../../cxdev-service-request.model';
import { FacetDisplayModule } from '../../../cxdev-facets/facet-display/facet-display.module';
import { PaginationModule } from '@spartacus/storefront';

@Component({
	selector: 'cxdev-service-request-overview',
	imports: [
		AsyncPipe,
		TranslatePipe,
		DatePipe,
		RouterLink,
		UrlModule,
		FacetDisplayModule,
		PaginationModule,
	],
	providers: [CxdevServiceRequestService],
	templateUrl: './cxdev-service-request-overview.component.html',
})
export class CxdevServiceRequestOverviewComponent {
	protected readonly serviceRequestService = inject(CxdevServiceRequestService);
	protected readonly activatedRoute = inject(ActivatedRoute);
	protected readonly router = inject(Router);

	protected readonly serviceRequestsOverviewSource$: Observable<CxdevServiceTicketSearchPage> =
		this.activatedRoute.queryParams.pipe(
			switchMap((queryParams) => this.serviceRequestService.getServiceRequestOverview(queryParams)),
		);

	onFacetValueChange(
		facetName: string,
		valueName: string,
		checked: boolean,
		multiSelect: boolean,
	): void {
		const currentValue = this.activatedRoute.snapshot.queryParamMap.get(facetName);
		const nextValue = this.buildNextFacetValue(currentValue, valueName, checked, multiSelect);

		void this.navigateWithQueryParams({
			[facetName]: nextValue,
			pageNumber: 0,
		});
	}

	onPageChange(pageNumber: number): void {
		if (pageNumber < 0) {
			return;
		}

		void this.navigateWithQueryParams({
			pageNumber,
		});
	}

	private buildNextFacetValue(
		currentValue: string | null,
		valueName: string,
		checked: boolean,
		multiSelect: boolean,
	): string | null {
		if (!multiSelect) {
			return checked ? valueName : null;
		}

		const currentValues = this.parseFacetValues(currentValue);

		const nextValues = checked
			? Array.from(new Set([...currentValues, valueName]))
			: currentValues.filter((value) => value !== valueName);

		return nextValues.length > 0 ? nextValues.join(',') : null;
	}

	private parseFacetValues(value: string | null): string[] {
		if (!value) {
			return [];
		}

		return value
			.split(',')
			.map((entry) => entry.trim())
			.filter(Boolean);
	}

	private navigateWithQueryParams(queryParams: Params): Promise<boolean> {
		return this.router.navigate([], {
			relativeTo: this.activatedRoute,
			queryParams,
			queryParamsHandling: 'merge',
		});
	}
}
