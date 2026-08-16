import { isPlatformBrowser } from '@angular/common';
import { Component, computed, DestroyRef, inject, PLATFORM_ID, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { I18nModule } from '@spartacus/core';
import { CmsComponentData } from '@spartacus/storefront';
import { CxdevDashboardWidgetSettingsService } from '../../cxdev-dashboard-widget-settings.service';

export const SETTING_TIMEZONES = 'timezones';

@Component({
	selector: 'cxdev-dashboard-clock',
	imports: [I18nModule],
	templateUrl: './cxdev-dashboard-clock.component.html',
})
export class CxdevDashboardClockComponent {
	protected componentData: CmsComponentData<any> = inject(CmsComponentData);
	protected widgetSettingsService = inject(CxdevDashboardWidgetSettingsService);
	protected platformId = inject(PLATFORM_ID);
	protected destroyRef = inject(DestroyRef);

	protected data = toSignal(this.componentData.data$, { initialValue: {} as any });
	protected settingsKey = computed(() => this.data().widgetSettingsKey ?? this.componentData.uid);
	protected settings = computed(() => this.widgetSettingsService.settingsFor(this.settingsKey())());

	protected localTimezone = Intl.DateTimeFormat().resolvedOptions().timeZone;

	now = signal(new Date());

	/** Configured timezones; falls back to the browser's local timezone. */
	timezones = computed<string[]>(() => {
		try {
			const parsed = JSON.parse(this.settings()[SETTING_TIMEZONES] ?? '[]');
			return Array.isArray(parsed) && parsed.length ? parsed : [this.localTimezone];
		} catch {
			return [this.localTimezone];
		}
	});

	constructor() {
		if (isPlatformBrowser(this.platformId)) {
			const interval = setInterval(() => this.now.set(new Date()), 1000);
			this.destroyRef.onDestroy(() => clearInterval(interval));
		}
	}

	timeFor(timezone: string): string {
		try {
			return new Intl.DateTimeFormat(undefined, {
				hour: '2-digit',
				minute: '2-digit',
				second: '2-digit',
				timeZone: timezone,
			}).format(this.now());
		} catch {
			return '–';
		}
	}

	labelFor(timezone: string): string {
		return timezone.split('/').pop()?.replace(/_/g, ' ') ?? timezone;
	}
}
