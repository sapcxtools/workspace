import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthRedirectService } from '@spartacus/core';
import { CxdevAuthRedirectService } from './cxdev-auth-redirect.service';

@NgModule({
	declarations: [],
	imports: [CommonModule],
	providers: [
		{
			provide: AuthRedirectService,
			useClass: CxdevAuthRedirectService,
		},
	],
})
export class CxdevServicesModule {}
