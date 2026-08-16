import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { OccEndpointsService, UserIdService } from '@spartacus/core';
import { Observable, switchMap, take } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class CxdevSavedCartService {
	constructor(
		protected http: HttpClient,
		protected occEndpointsService: OccEndpointsService,
	) {}

	private userIdService = inject(UserIdService);

	saveCartWithCustomParam(
		cartId: string,
		name: string,
		description: string,
		keepActiveCart: boolean,
	): Observable<any> {
		return this.userIdService.getUserId().pipe(
			take(1),
			switchMap((userId) => {
				const url = this.occEndpointsService.buildUrl('saveCart', {
					urlParams: { userId, cartId },
				});

				const body = {
					name,
					description,
					keepActiveCart,
				};

				return this.http.patch(url, body);
			}),
		);
	}
}
