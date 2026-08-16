import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ConverterService, OccEndpointsService, UserIdService } from '@spartacus/core';
import { map, Observable, switchMap } from 'rxjs';
import { CxdevDashboardConfig, CxdevDashboardWidget } from './cxdev-dashboard.model';
import { CXDEV_DASHBOARD_CONFIG_NORMALIZER } from './cxdev-dashboard-config.normalizer';
import { CXDEV_DASHBOARD_CONFIG_SERIALIZER } from './cxdev-dashboard-config.serializer';

@Injectable()
export class CxdevDashboardService {
	protected http = inject(HttpClient);
	protected endpointService = inject(OccEndpointsService);
	protected userIdService = inject(UserIdService);
	protected converter = inject(ConverterService);

	getDashboardConfigs(): Observable<CxdevDashboardConfig[]> {
		return this.userIdService.getUserId().pipe(
			switchMap((userId) =>
				this.http.get<any[]>(
					this.endpointService.buildUrl('dashboardConfig', { urlParams: { userId } }),
				),
			),
			map((configs) =>
				configs.map((c) => this.converter.convert(c, CXDEV_DASHBOARD_CONFIG_NORMALIZER)),
			),
		);
	}

	deleteDashboardConfig(config: CxdevDashboardConfig): Observable<void> {
		return this.userIdService
			.getUserId()
			.pipe(
				switchMap((userId) =>
					this.http.delete<void>(
						this.endpointService.buildUrl('dashboardConfig', { urlParams: { userId } }),
						{ body: config },
					),
				),
			);
	}

	saveDashboardConfig(config: CxdevDashboardConfig): Observable<CxdevDashboardConfig> {
		return this.userIdService.getUserId().pipe(
			switchMap((userId) =>
				this.http.put<any>(
					this.endpointService.buildUrl('dashboardConfig', { urlParams: { userId } }),
					this.converter.convert(config, CXDEV_DASHBOARD_CONFIG_SERIALIZER),
				),
			),
			map((c) => this.converter.convert(c, CXDEV_DASHBOARD_CONFIG_NORMALIZER)),
		);
	}

	getAvailableWidgets(): Observable<CxdevDashboardWidget[]> {
		return this.userIdService
			.getUserId()
			.pipe(
				switchMap((userId) =>
					this.http.get<CxdevDashboardWidget[]>(
						this.endpointService.buildUrl('dashboardWidgets', { urlParams: { userId } }),
					),
				),
			);
	}
}
