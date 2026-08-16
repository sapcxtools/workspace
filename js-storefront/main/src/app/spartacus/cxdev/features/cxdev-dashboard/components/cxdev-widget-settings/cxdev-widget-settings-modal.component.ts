import { NgComponentOutlet } from '@angular/common';
import {
	Component,
	computed,
	inject,
	Injector,
	OnInit,
	signal,
	WritableSignal,
} from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { ICON_TYPE, IconModule, LaunchDialogService } from '@spartacus/storefront';
import { take } from 'rxjs';
import { CxdevDashboardWidgetConfig } from '../../cxdev-dashboard.model';
import { CxdevGenericWidgetSettingsComponent } from '../cxdev-generic-widget-settings/cxdev-generic-widget-settings.component';
import { CXDEV_WIDGET_SETTINGS_CONTEXT } from './cxdev-widget-settings-context';
import { CXDEV_WIDGET_SETTINGS_COMPONENTS } from './cxdev-widget-settings.registry';
import { widgetSettingsKeyFor } from '../../cxdev-dashboard-widget-settings-key';

/**
 * Dialog with the settings of a widget, opened from the widget frame in edit
 * mode via the LaunchDialogService. The widget-specific settings component is
 * resolved via the settings registry. Generic settings are shown for all widgets.
 */
@Component({
	selector: 'cxdev-widget-settings-modal',
	imports: [NgComponentOutlet, I18nModule, IconModule],
	templateUrl: './cxdev-widget-settings-modal.component.html',
})
export class CxdevWidgetSettingsModalComponent implements OnInit {
	private launchDialogService = inject(LaunchDialogService);
	private injector = inject(Injector);

	iconType = ICON_TYPE;
	genericSettingsComponent = CxdevGenericWidgetSettingsComponent;

	protected config: WritableSignal<CxdevDashboardWidgetConfig | undefined> = signal(undefined);

	settingsComponent = computed(() => {
		const type = this.config()?.widget.contentComponentType;
		return type ? (CXDEV_WIDGET_SETTINGS_COMPONENTS[type] ?? null) : null;
	});

	settingsInjector = computed(() =>
		Injector.create({
			providers: [
				{
					provide: CXDEV_WIDGET_SETTINGS_CONTEXT,
					useValue: this.config()
						? { config: this.config(), settingsKey: widgetSettingsKeyFor(this.config()!) }
						: undefined,
				},
			],
			parent: this.injector,
		}),
	);

	ngOnInit(): void {
		this.launchDialogService.data$.pipe(take(1)).subscribe((data) => {
			this.config.set(data?.config);
		});
	}

	close(): void {
		this.launchDialogService.closeDialog('closed');
	}
}
