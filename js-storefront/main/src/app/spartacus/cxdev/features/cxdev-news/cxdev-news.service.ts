import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { LanguageService, OccEndpointsService } from '@spartacus/core';
import { Observable, distinctUntilChanged, map, switchMap } from 'rxjs';

import { CxdevNews, CxdevNewsList } from '../../models/cxdev-news.model';

@Injectable({
	providedIn: 'root',
})
export class CxdevNewsService {
	constructor(
		private readonly http: HttpClient,
		private readonly languageService: LanguageService,
		private readonly occEndpointsService: OccEndpointsService,
	) {}

	getLatestNews(): Observable<CxdevNews[]> {
		return this.languageService.getActive().pipe(
			distinctUntilChanged(),
			switchMap(() => this.loadNews('cxdevNews')),
		);
	}

	getAllNews(): Observable<CxdevNews[]> {
		return this.languageService.getActive().pipe(
			distinctUntilChanged(),
			switchMap(() => this.loadNews('cxdevAllNews')),
		);
	}

	private loadNews(endpoint: string): Observable<CxdevNews[]> {
		let params = new HttpParams().set('fields', 'DEFAULT');

		return this.http
			.get<CxdevNewsList>(this.occEndpointsService.buildUrl(endpoint), { params })
			.pipe(map((response) => response.news ?? []));
	}
}
