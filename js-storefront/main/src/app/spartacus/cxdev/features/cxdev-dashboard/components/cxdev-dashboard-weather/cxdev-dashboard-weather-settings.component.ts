import { HttpClient } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { CxdevDashboardWidgetSettingsService } from '../../cxdev-dashboard-widget-settings.service';
import { CXDEV_WIDGET_SETTINGS_CONTEXT } from '../cxdev-widget-settings/cxdev-widget-settings-context';
import {
	GEOCODING_URL,
	SETTING_LATITUDE,
	SETTING_LOCATION,
	SETTING_LONGITUDE,
} from './cxdev-dashboard-weather.component';

/** Settings for the weather widget: pick the location the forecast is shown for. */
@Component({
	selector: 'cxdev-dashboard-weather-settings',
	imports: [I18nModule],
	templateUrl: './cxdev-dashboard-weather-settings.component.html',
})
export class CxdevDashboardWeatherSettingsComponent {
	protected http = inject(HttpClient);
	protected context = inject(CXDEV_WIDGET_SETTINGS_CONTEXT);
	protected widgetSettingsService = inject(CxdevDashboardWidgetSettingsService);

	protected settingsKey = this.context.settingsKey;
	protected settings = this.widgetSettingsService.settingsFor(this.settingsKey);

	location = computed(() => this.settings()[SETTING_LOCATION] ?? '');
	searching = signal(false);
	notFound = signal(false);

	searchLocation(name: string) {
		if (!name.trim()) return;
		this.searching.set(true);
		this.notFound.set(false);
		this.http.get<any>(GEOCODING_URL, { params: { name: name.trim(), count: 1 } }).subscribe({
			next: (response) => {
				this.searching.set(false);
				const result = response?.results?.[0];
				if (!result) {
					this.notFound.set(true);
					return;
				}
				this.widgetSettingsService.updateSettings(this.settingsKey, {
					[SETTING_LOCATION]: result.name,
					[SETTING_LATITUDE]: String(result.latitude),
					[SETTING_LONGITUDE]: String(result.longitude),
				});
			},
			error: () => {
				this.searching.set(false);
				this.notFound.set(true);
			},
		});
	}
}
