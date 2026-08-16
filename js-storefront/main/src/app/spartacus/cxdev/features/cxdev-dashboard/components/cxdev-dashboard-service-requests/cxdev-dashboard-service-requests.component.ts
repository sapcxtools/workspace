import { Component, inject } from '@angular/core';
import { CxdevDashboardService } from '../../cxdev-dashboard.service';
import { Observable } from 'rxjs';
import { CxdevServiceRequestCount } from '../../cxdev-dashboard.model';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslatePipe, UrlModule } from '@spartacus/core';
import { CxdevComponentWrapperDirective } from '../../../../directives/cxdev-component-wrapper/cxdev-component-wrapper.directive';
import { CxdevDataAcquisition } from '../../../../directives/cxdev-component-wrapper/cxdev-data-acquisition.enum';
import { CxdevServiceRequestService } from '../../../cxdev-service-request/cxdev-service-request.service';

@Component({
	selector: 'cxdev-dashboard-service-requests',
	imports: [AsyncPipe, TranslatePipe, RouterLink, UrlModule, CxdevComponentWrapperDirective],
	templateUrl: './cxdev-dashboard-service-requests.component.html',
})
export class CxdevDashboardServiceRequestsComponent {
	protected serviceRequestService = inject(CxdevServiceRequestService);

	serviceRequestsCount$: Observable<CxdevServiceRequestCount> =
		this.serviceRequestService.getServiceRequestsCount();

	dataAquisitionType = CxdevDataAcquisition.DYNAMIC;
}
