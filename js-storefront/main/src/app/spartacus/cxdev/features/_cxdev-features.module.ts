import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideConfig, CmsConfig } from '@spartacus/core';
import { CxdevDashboardComponent } from './cxdev-dashboard/cxdev-dashboard.component';
import { CxdevDashboardTeaserComponent } from './cxdev-dashboard/components/cxdev-dashboard-teaser/cxdev-dashboard-teaser.component';
import { CxdevDashboardProductListComponent } from './cxdev-dashboard/components/cxdev-dashboard-product-list/cxdev-dashboard-product-list.component';
import { CxdevDashboardServiceRequestsComponent } from './cxdev-dashboard/components/cxdev-dashboard-service-requests/cxdev-dashboard-service-requests.component';
import { CxdevDashboardNotepadComponent } from './cxdev-dashboard/components/cxdev-dashboard-notepad/cxdev-dashboard-notepad.component';
import { CxdevDashboardWeatherComponent } from './cxdev-dashboard/components/cxdev-dashboard-weather/cxdev-dashboard-weather.component';
import { CxdevDashboardTodoComponent } from './cxdev-dashboard/components/cxdev-dashboard-todo/cxdev-dashboard-todo.component';
import { CxdevDashboardSpacerComponent } from './cxdev-dashboard/components/cxdev-dashboard-spacer/cxdev-dashboard-spacer.component';
import { CxdevDashboardClockComponent } from './cxdev-dashboard/components/cxdev-dashboard-clock/cxdev-dashboard-clock.component';
import { CxdevWidgetSettingsModalComponent } from './cxdev-dashboard/components/cxdev-widget-settings/cxdev-widget-settings-modal.component';
import { CxdevWidgetSelectorModalComponent } from './cxdev-dashboard/components/cxdev-widget-selector/cxdev-widget-selector-modal.component';
import { CxdevOrderModule } from './cxdev-order/cxdev-order.module';
import { CxdevRegistrationFormComponent } from './cxdev-registration-form/cxdev-registration-form.component';
import { CxdevServiceRequestModule } from './cxdev-service-request/cxdev-service-request.module';
import { FacetDisplayModule } from './cxdev-facets/facet-display/facet-display.module';
import { CxdevProductFacetComponent } from './cxdev-facets/cxdev-product-facet/cxdev-product-facet.component';
import { CxdevProductFacetModule } from './cxdev-facets/cxdev-product-facet/cxdev-product-facet.module';
import { LayoutConfig, DIALOG_TYPE } from '@spartacus/storefront';
import { DpSavedCartFormDialogComponent } from './cxdev-saved-cart/cxdev-saved-cart.component';
import { CxdevFormModule } from './cxdev-form/cxdev-form.module';
import { CxdevFormListModule } from './cxdev-form-list/cxdev-form-list.module';
import { CxdevServiceRequestFormComponent } from './cxdev-service-request/components/cxdev-service-request-form/cxdev-service-request-form.component';
import {
	CXDEV_DASHBOARD_CONFIG_NORMALIZER,
	CxdevDashboardConfigNormalizer,
} from './cxdev-dashboard/cxdev-dashboard-config.normalizer';
import {
	CXDEV_DASHBOARD_CONFIG_SERIALIZER,
	CxdevDashboardConfigSerializer,
} from './cxdev-dashboard/cxdev-dashboard-config.serializer';
import { CxdevNewsOverviewComponent } from './cxdev-news/cxdev-news-overview/cxdev-news-overview.component';
import { CxdevLatestNewsComponent } from './cxdev-news/cxdev-latest-news/cxdev-latest-news.component';

@NgModule({
	declarations: [],
	imports: [
		CommonModule,
		CxdevOrderModule,
		CxdevServiceRequestModule,
		FacetDisplayModule,
		CxdevProductFacetModule,
		CxdevFormModule,
		CxdevFormListModule,
	],
	providers: [
		{
			provide: CXDEV_DASHBOARD_CONFIG_NORMALIZER,
			useClass: CxdevDashboardConfigNormalizer,
			multi: true,
		},
		{
			provide: CXDEV_DASHBOARD_CONFIG_SERIALIZER,
			useClass: CxdevDashboardConfigSerializer,
			multi: true,
		},
		provideConfig(<CmsConfig>{
			cmsComponents: {
				CxdevServiceTicketComponent: {
					component: CxdevServiceRequestFormComponent,
				},
				DashboardComponent: {
					component: CxdevDashboardComponent,
				},
				CxdevDashboardTeaserComponent: {
					component: CxdevDashboardTeaserComponent,
				},
				CxdevDashboardProductListComponent: {
					component: CxdevDashboardProductListComponent,
				},
				CxdevDashboardServiceRequestsComponent: {
					component: CxdevDashboardServiceRequestsComponent,
				},
				CxdevDashboardNotepadComponent: {
					component: CxdevDashboardNotepadComponent,
				},
				CxdevDashboardWeatherComponent: {
					component: CxdevDashboardWeatherComponent,
				},
				CxdevDashboardTodoComponent: {
					component: CxdevDashboardTodoComponent,
				},
				CxdevDashboardSpacerComponent: {
					component: CxdevDashboardSpacerComponent,
				},
				CxdevDashboardClockComponent: {
					component: CxdevDashboardClockComponent,
				},
				OrganizationUserRegistrationComponent: {
					component: CxdevRegistrationFormComponent,
				},
				ProductRefinementComponent: {
					component: CxdevProductFacetComponent,
				},
				CxdevLatestNewsComponent: {
					component: CxdevLatestNewsComponent,
				},
				CxdevNewsOverviewComponent: {
					component: CxdevNewsOverviewComponent,
				},
			},
		}),
		provideConfig(<LayoutConfig>{
			launch: {
				SAVED_CART: {
					inline: true,
					component: DpSavedCartFormDialogComponent,
					dialogType: DIALOG_TYPE.DIALOG,
				},
				CXDEV_WIDGET_SETTINGS: {
					component: CxdevWidgetSettingsModalComponent,
					dialogType: DIALOG_TYPE.DIALOG,
					inlineRoot: true,
				},
				CXDEV_WIDGET_SELECTOR: {
					component: CxdevWidgetSelectorModalComponent,
					dialogType: DIALOG_TYPE.DIALOG,
					inlineRoot: true,
				},
			},
		}),
	],
})
export class CxdevFeaturesModule {}
