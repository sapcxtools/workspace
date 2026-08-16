import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { CxdevOrderHistoryComponent } from './components/cxdev-order-history/cxdev-order-history.component';
import { CxdevOrderTrackingComponent } from './components/cxdev-order-tracking-component/cxdev-order-tracking-component';

@NgModule({
	declarations: [],
	imports: [CommonModule],
	providers: [
		provideConfig(<CmsConfig>{
			cmsComponents: {
				AccountOrderHistoryComponent: {
					component: CxdevOrderHistoryComponent,
				},
				AccountOrderDetailsTrackingDetailsComponent: {
					component: CxdevOrderTrackingComponent,
				},
			},
		}),
	],
})
export class CxdevOrderModule {}
