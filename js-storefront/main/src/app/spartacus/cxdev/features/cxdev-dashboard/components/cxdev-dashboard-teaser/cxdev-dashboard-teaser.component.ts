import { Component, inject } from '@angular/core';
import { CmsComponentData, ICON_TYPE, IconComponent, MediaComponent } from '@spartacus/storefront';
import { CxdevDashboardTeaserCmsComponent } from '../../cxdev-dashboard.model';
import { Observable } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
	selector: 'cxdev-dashboard-teaser',
	imports: [AsyncPipe, MediaComponent, IconComponent, RouterLink],
	templateUrl: './cxdev-dashboard-teaser.component.html',
})
export class CxdevDashboardTeaserComponent {
	iconTypes = ICON_TYPE;
	component: CmsComponentData<CxdevDashboardTeaserCmsComponent> = inject(CmsComponentData);
}
