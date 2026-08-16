import { NgModule } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { AppRoutingModule } from '@spartacus/storefront';
import { SpartacusModule } from './spartacus/spartacus.module';
import { CxdevModule } from './spartacus/cxdev/cxdev.module';

@NgModule({
	imports: [
		StoreModule.forRoot({}),
		EffectsModule.forRoot([]),
		AppRoutingModule,
		SpartacusModule,
		CxdevModule,
	],
})
export class AppModule {}
