import { Injectable } from '@angular/core';
import { AuthRedirectService } from '@spartacus/core';

@Injectable({
	providedIn: 'root',
})
export class CxdevAuthRedirectService extends AuthRedirectService {
	override setRedirectUrl(url: string): void {
		// intentionally do nothing, as we do not want to store the attempted URL, but instead always redirect to the dashboard (homepage) after login
	}
}
