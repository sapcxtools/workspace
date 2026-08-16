import { Component, inject } from '@angular/core';
import { distinctUntilChanged, filter, map, Observable, switchMap } from 'rxjs';
import { AsyncPipe, DatePipe } from '@angular/common';
import { RoutingService, TranslatePipe } from '@spartacus/core';
import { CxdevServiceRequestService } from '../../cxdev-service-request.service';
import { CxdevServiceTicket } from '../../cxdev-service-request.model';

@Component({
	selector: 'cxdev-service-request-detail',
	imports: [AsyncPipe, TranslatePipe, DatePipe],
	providers: [CxdevServiceRequestService],
	templateUrl: './cxdev-service-request-detail.component.html',
})
export class CxdevServiceRequestDetailComponent {
	protected serviceRequestService: CxdevServiceRequestService = inject(CxdevServiceRequestService);
	protected routingService = inject(RoutingService);

	serviceRequestDetails$: Observable<CxdevServiceTicket> = this.routingService.getRouterState().pipe(
		map((routerState) => routerState.state),
		filter((state) => state.semanticRoute === 'serviceRequestDetail'),
		map((state) => state.params?.['id']),
		filter(
			(requestId): requestId is string => typeof requestId === 'string' && requestId.length > 0,
		),
		distinctUntilChanged(),
		switchMap((requestId) => this.serviceRequestService.getServiceRequestDetails(requestId)),
	);
}
