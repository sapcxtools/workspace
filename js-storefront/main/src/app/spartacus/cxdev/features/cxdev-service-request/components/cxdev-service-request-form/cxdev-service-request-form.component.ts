import { Component, inject } from '@angular/core';
import { CxdevFormComponent } from '../../../cxdev-form/components/cxdev-form/cxdev-form.component';
import { CommonModule } from '@angular/common';
import { CxdevServiceRequestFormComponentData } from '../../cxdev-service-request.model';
import { CmsComponentData } from '@spartacus/storefront';

@Component({
	selector: 'cxdev-service-request-form',
	imports: [CommonModule, CxdevFormComponent],
	templateUrl: './cxdev-service-request-form.component.html',
})
export class CxdevServiceRequestFormComponent {
	public component: CmsComponentData<CxdevServiceRequestFormComponentData> = inject(CmsComponentData);
}
