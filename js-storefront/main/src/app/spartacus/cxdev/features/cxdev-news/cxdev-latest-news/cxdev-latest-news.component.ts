import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@spartacus/core';
import { CxdevNewsComponent } from '../cxdev-news.component';

@Component({
	selector: 'cxdev-latest-news',
	imports: [RouterLink, TranslatePipe, CxdevNewsComponent],
	standalone: true,
	templateUrl: './cxdev-latest-news.component.html',
})
export class CxdevLatestNewsComponent {
	hasNews: boolean | null = null;
}
