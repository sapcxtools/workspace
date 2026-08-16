import { Component, inject, OnInit, signal } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { ICON_TYPE, IconModule, LaunchDialogService } from '@spartacus/storefront';
import { take } from 'rxjs';
import { CxdevDashboardWidget } from '../../cxdev-dashboard.model';

export interface CxdevWidgetSelectorItem {
	widget: CxdevDashboardWidget;
	addable: boolean;
}

@Component({
	selector: 'cxdev-widget-selector-modal',
	imports: [I18nModule, IconModule],
	templateUrl: './cxdev-widget-selector-modal.component.html',
})
export class CxdevWidgetSelectorModalComponent implements OnInit {
	private launchDialogService = inject(LaunchDialogService);

	items = signal<CxdevWidgetSelectorItem[]>([]);
	iconType = ICON_TYPE;

	ngOnInit(): void {
		this.launchDialogService.data$.pipe(take(1)).subscribe((data) => {
			this.items.set(data?.items ?? []);
		});
	}

	select(item: CxdevWidgetSelectorItem): void {
		if (!item.addable) return;

		this.launchDialogService.closeDialog(item.widget);
	}

	close(): void {
		this.launchDialogService.closeDialog('closed');
	}
}
