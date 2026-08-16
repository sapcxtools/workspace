import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { DIALOG_TYPE, LayoutConfig } from '@spartacus/storefront';
import { CxdevFormListCmsComponent } from './components/cxdev-form-list-cms/cxdev-form-list-cms.component';
import { CxdevFormListModalComponent } from './components/cxdev-form-list-modal/cxdev-form-list-modal.component';

@NgModule({
	providers: [
		provideConfig(<CmsConfig>{
			cmsComponents: {
				CxdevFormListComponent: {
					component: CxdevFormListCmsComponent,
				},
			},
		}),
		provideConfig(<LayoutConfig>{
			launch: {
				CXDEV_FORM_LIST: {
					component: CxdevFormListModalComponent,
					dialogType: DIALOG_TYPE.DIALOG,
					inlineRoot: true,
				},
			},
		}),
	],
})
export class CxdevFormListModule {}
