import { Component, computed, inject } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { CxdevDashboardWidgetSettingsService } from '../../cxdev-dashboard-widget-settings.service';
import { CXDEV_WIDGET_SETTINGS_CONTEXT } from '../cxdev-widget-settings/cxdev-widget-settings-context';
import { SETTING_TIMEZONES } from './cxdev-dashboard-clock.component';

/** Settings for the clock widget: manage the list of displayed timezones. */
@Component({
	selector: 'cxdev-dashboard-clock-settings',
	imports: [I18nModule],
	templateUrl: './cxdev-dashboard-clock-settings.component.html',
})
export class CxdevDashboardClockSettingsComponent {
	protected context = inject(CXDEV_WIDGET_SETTINGS_CONTEXT);
	protected widgetSettingsService = inject(CxdevDashboardWidgetSettingsService);

	protected settingsKey = this.context.settingsKey;
	protected settings = this.widgetSettingsService.settingsFor(this.settingsKey);

	availableTimezones: string[] = Intl.supportedValuesOf('timeZone');

	timezones = computed<string[]>(() => {
		try {
			const parsed = JSON.parse(this.settings()[SETTING_TIMEZONES] ?? '[]');
			return Array.isArray(parsed) ? parsed : [];
		} catch {
			return [];
		}
	});

	addTimezone(select: HTMLSelectElement) {
		const timezone = select.value;
		if (!timezone || this.timezones().includes(timezone)) return;
		this.saveTimezones([...this.timezones(), timezone]);
		select.value = '';
	}

	removeTimezone(timezone: string) {
		this.saveTimezones(this.timezones().filter((tz) => tz !== timezone));
	}

	private saveTimezones(timezones: string[]) {
		this.widgetSettingsService.updateSettings(this.settingsKey, {
			[SETTING_TIMEZONES]: JSON.stringify(timezones),
		});
	}
}
