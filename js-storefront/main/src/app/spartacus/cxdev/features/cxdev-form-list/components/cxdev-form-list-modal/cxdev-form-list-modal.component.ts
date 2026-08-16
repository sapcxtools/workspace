import { Component, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { take } from 'rxjs';
import { ICON_TYPE, IconModule, LaunchDialogService } from '@spartacus/storefront';
import { CxdevFormListComponent } from '../cxdev-form-list/cxdev-form-list.component';

@Component({
	selector: 'cxdev-form-list-modal',
	imports: [CxdevFormListComponent, IconModule],
	templateUrl: './cxdev-form-list-modal.component.html',
})
export class CxdevFormListModalComponent implements OnInit {
	private launchDialogService = inject(LaunchDialogService);

	protected formIds: WritableSignal<string[]> = signal([]);
	protected headline: WritableSignal<string | undefined> = signal(undefined);

	iconType = ICON_TYPE;

	ngOnInit(): void {
		this.launchDialogService.data$.pipe(take(1)).subscribe((data) => {
			this.formIds.set(data?.formIds ?? []);
			this.headline.set(data?.headline);
		});
	}

	close(): void {
		this.launchDialogService.closeDialog('closed');
	}
}
