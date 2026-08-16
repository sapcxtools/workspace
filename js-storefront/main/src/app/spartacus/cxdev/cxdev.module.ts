import { NgModule } from '@angular/core';
import { CxdevModalsModule } from './components/modals/_cxdev-modals.module';
import { CxdevConfigurationsModule } from './configurations/_cxdev-configurations.module';
import { CxdevFeaturesModule } from './features/_cxdev-features.module';
import { CxdevInterceptorsModule } from './interceptors/_cxdev-interceptors.module';
import { CxdevServicesModule } from './services/cxdev-services.module';

@NgModule({
	declarations: [],
	imports: [
		CxdevModalsModule,
		CxdevConfigurationsModule,
		CxdevFeaturesModule,
		CxdevInterceptorsModule,
		CxdevServicesModule,
	],
	providers: [],
})
export class CxdevModule {}
