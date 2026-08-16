import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthGuard, CmsConfig, provideConfig } from '@spartacus/core';
import { CmsPageGuard, PageLayoutComponent } from '@spartacus/storefront';
import { CxdevServiceRequestOverviewComponent } from './components/cxdev-service-request-overview/cxdev-service-request-overview.component';
import { CxdevServiceRequestDetailComponent } from './components/cxdev-service-request-detail/cxdev-service-request-detail.component';

@NgModule({
	declarations: [],
	imports: [
		CommonModule,
		RouterModule.forChild([
			{
				// @ts-ignore
				path: null,
				canActivate: [AuthGuard, CmsPageGuard],
				component: PageLayoutComponent,
				data: {
					cxRoute: 'serviceRequestDetail',
				},
			},
		]),
	],
	providers: [
		provideConfig(<CmsConfig>{
			cmsComponents: {
				CxdevServiceRequestOverviewComponent: {
					component: CxdevServiceRequestOverviewComponent,
				},
				CxdevServiceRequestDetailComponent: {
					component: CxdevServiceRequestDetailComponent,
				},
			},
		}),
	],
})
export class CxdevServiceRequestModule {}
