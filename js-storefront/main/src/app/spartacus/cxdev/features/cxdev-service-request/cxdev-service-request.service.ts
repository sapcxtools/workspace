import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { OccEndpointsService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { CxdevServiceTicket, CxdevServiceTicketSearchPage } from './cxdev-service-request.model';
import { CxdevServiceRequestCount } from '../cxdev-dashboard/cxdev-dashboard.model';

@Injectable({
	providedIn: 'root',
})
export class CxdevServiceRequestService {
	protected http = inject(HttpClient);
	protected endpointService = inject(OccEndpointsService);

	getServiceRequestOverview(
		queryParams?: Record<string, string>,
	): Observable<CxdevServiceTicketSearchPage> {
		return this.http.get<CxdevServiceTicketSearchPage>(
			this.endpointService.buildUrl('serviceRequestOverview', {
				queryParams: {
					query: queryParams?.['query'] ?? '',
					pageSize: queryParams?.['pageSize'] ?? '20',
					currentPage: queryParams?.['pageNumber'] ?? '0',
					sort: queryParams?.['sort'] ?? '',
				},
			}),
		);
	}

	getServiceRequestDetails(requestId: string): Observable<CxdevServiceTicket> {
		return this.http.get<CxdevServiceTicket>(
			this.endpointService.buildUrl('serviceRequestDetails', { urlParams: { requestId } }),
		);
	}

	createServiceTicket(): Observable<string> {
		return this.http.get<string>(this.endpointService.buildUrl('createServiceTicket'));
	}

	getServiceRequestsCount(): Observable<CxdevServiceRequestCount> {
		return this.http.get<CxdevServiceRequestCount>(
			this.endpointService.buildUrl('serviceRequestsCount'),
		);
	}
}
