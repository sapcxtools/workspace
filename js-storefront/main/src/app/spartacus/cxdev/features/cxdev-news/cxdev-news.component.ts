import { CommonModule } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	EventEmitter,
	inject,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
} from '@angular/core';
import { TranslatePipe } from '@spartacus/core';
import { MediaComponent } from '@spartacus/storefront';
import { Observable, tap } from 'rxjs';

import { CxdevNews } from '../../models/cxdev-news.model';
import { CxdevNewsService } from './cxdev-news.service';

export type CxdevNewsMode = 'latest' | 'all';

@Component({
	selector: 'cxdev-news',
	standalone: true,
	imports: [CommonModule, TranslatePipe, MediaComponent],
	templateUrl: './cxdev-news.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CxdevNewsComponent implements OnChanges {
	@Input()
	mode: CxdevNewsMode = 'latest';

	@Output()
	readonly hasNewsChange = new EventEmitter<boolean>();

	private readonly cxdevNewsService = inject(CxdevNewsService);

	news$!: Observable<CxdevNews[]>;

	get isOverview(): boolean {
		return this.mode === 'all';
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (changes['mode']) {
			this.loadNews();
		}
	}

	private loadNews(): void {
		this.news$ = this.isOverview
			? this.cxdevNewsService.getAllNews()
			: this.cxdevNewsService.getLatestNews();

		this.news$ = this.news$.pipe(
			tap((newsItems) => {
				this.hasNewsChange.emit(newsItems.length > 0);
			}),
		);
	}
}
