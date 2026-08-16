/*
 * SPDX-FileCopyrightText: 2026 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { CmsComponent, CmsService } from '@spartacus/core';
import { CmsComponentsService } from '@spartacus/storefront';
import { defer, EMPTY, Observable, of, tap } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Injectable({
	providedIn: 'root',
})
export class ComponentDataProvider {
	constructor(
		protected componentsService: CmsComponentsService,
		protected cmsService: CmsService,
	) {}
	get<T extends CmsComponent>(uid: string, type?: string): Observable<T> {
		return defer(() => {
			let staticComponentData: T | undefined;

			if (type) {
				staticComponentData = this.componentsService.getStaticData<T>(type);
			}

			if (uid) {
				if (staticComponentData) {
					return this.cmsService.getComponentData<T>(uid).pipe(
						map((data) => ({
							...staticComponentData,
							...data,
						})),
						startWith(staticComponentData),
					);
				} else {
					return this.cmsService.getComponentData<T>(uid);
				}
			} else {
				return staticComponentData ? of(staticComponentData) : EMPTY;
			}
		});
	}
}
