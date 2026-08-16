import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, GuardResult } from '@angular/router';
import { CheckoutStepsSetGuard, CheckoutStepService } from '@spartacus/checkout/base/components';
import { Observable } from 'rxjs';
import { switchMap, take, tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class CxdevCheckoutProgressGuard {
	protected checkoutStepService = inject(CheckoutStepService);
	protected checkoutStepsSetGuard = inject(CheckoutStepsSetGuard);

	canActivate(route: ActivatedRouteSnapshot): Observable<GuardResult> {
		return this.checkoutStepService.steps$.pipe(
			take(1),
			tap((steps) => {
				for (const step of steps) {
					step.nameMultiLine = false;
				}
			}),
			switchMap(() => this.checkoutStepsSetGuard.canActivate(route)),
		);
	}
}
