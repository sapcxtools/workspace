import { inject, Injectable, Injector } from '@angular/core';
import {
	CmsComponentsService,
	CmsComponentData,
	provideLcpPresenceForCmsComponent,
} from '@spartacus/storefront';
import { ComponentDataProvider } from './cxdev-component-data.provider';
import { CxdevDataAcquisition } from './cxdev-data-acquisition.enum';
import { combineLatest, map, of, switchMap } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class CxdevCmsInjectorService {
	protected cmsComponentsService: CmsComponentsService = inject(CmsComponentsService);
	protected injector: Injector = inject(Injector);

	public getInjector(
		type: string,
		uid: string,
		content: any,
		dataType: CxdevDataAcquisition = CxdevDataAcquisition.STATIC,
		parentInjector?: Injector,
	): Injector {
		const configProviders = this.cmsComponentsService.getMapping(type)?.providers ?? [];
		return Injector.create({
			providers: [
				{
					provide: CmsComponentData,
					useFactory: (dataProvider: ComponentDataProvider) => ({
						uid,
						data$: of(dataType).pipe(
							switchMap((dt) => {
								switch (dt) {
									case CxdevDataAcquisition.STATIC:
										return of(content);
									case CxdevDataAcquisition.DYNAMIC:
										return dataProvider.get(uid, type);
									case CxdevDataAcquisition.MERGE:
										return combineLatest([of(content), dataProvider.get(uid, type)]).pipe(
											map(([staticData, dynamicData]) => ({ ...staticData, ...dynamicData })),
										);
								}
							}),
						),
					}),
					deps: [ComponentDataProvider],
				},
				provideLcpPresenceForCmsComponent(),
				...configProviders,
			],
			parent: parentInjector ?? this.injector,
		});
	}
}
