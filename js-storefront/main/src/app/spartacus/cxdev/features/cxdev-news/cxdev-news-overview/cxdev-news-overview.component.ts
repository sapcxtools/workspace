import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CxdevNewsComponent } from '../cxdev-news.component';
import { TranslatePipe, TranslationResources } from '@spartacus/core';

export const newsOverviewTranslationsDe: TranslationResources = {
	newsOverview: {
		newsOverview: {
			overviewTitle: 'Alle News',
			emptyOverview: 'Aktuell sind keine News vorhanden.',
		},
	},
};
export const newsOverviewTranslationsEn: TranslationResources = {
	newsOverview: {
		newsOverview: {
			overviewTitle: 'Alle News',
			emptyOverview: 'Aktuell sind keine News vorhanden.',
		},
	},
};

@Component({
	selector: 'cxdev-news-overview',
	standalone: true,
	imports: [CxdevNewsComponent, TranslatePipe],
	templateUrl: './cxdev-news-overview.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CxdevNewsOverviewComponent {
	hasNews: boolean | null = null;
}
