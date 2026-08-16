import { HttpClient } from '@angular/common/http';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { I18nModule } from '@spartacus/core';
import { CmsComponentData } from '@spartacus/storefront';
import { CxdevDashboardWidgetSettingsService } from '../../cxdev-dashboard-widget-settings.service';

export const SETTING_LOCATION = 'location';
export const SETTING_LATITUDE = 'latitude';
export const SETTING_LONGITUDE = 'longitude';

export const GEOCODING_URL = 'https://geocoding-api.open-meteo.com/v1/search';
const FORECAST_URL = 'https://api.open-meteo.com/v1/forecast';

interface CxdevWeather {
	temperature: number;
	windSpeed: number;
	weatherCode: number;
}

/** Maps WMO weather codes (Open-Meteo `weather_code`) to an icon and a translation key. */
const WEATHER_CONDITIONS: { codes: number[]; icon: string; key: string }[] = [
	{ codes: [0], icon: '☀️', key: 'clear' },
	{ codes: [1, 2], icon: '⛅', key: 'partlyCloudy' },
	{ codes: [3], icon: '☁️', key: 'overcast' },
	{ codes: [45, 48], icon: '🌫️', key: 'fog' },
	{ codes: [51, 53, 55, 56, 57], icon: '🌦️', key: 'drizzle' },
	{ codes: [61, 63, 65, 66, 67], icon: '🌧️', key: 'rain' },
	{ codes: [71, 73, 75, 77, 85, 86], icon: '🌨️', key: 'snow' },
	{ codes: [80, 81, 82], icon: '🌦️', key: 'showers' },
	{ codes: [95, 96, 99], icon: '⛈️', key: 'thunderstorm' },
];

@Component({
	selector: 'cxdev-dashboard-weather',
	imports: [I18nModule],
	templateUrl: './cxdev-dashboard-weather.component.html',
})
export class CxdevDashboardWeatherComponent {
	protected http = inject(HttpClient);
	protected componentData: CmsComponentData<any> = inject(CmsComponentData);
	protected widgetSettingsService = inject(CxdevDashboardWidgetSettingsService);

	protected data = toSignal(this.componentData.data$, { initialValue: {} as any });
	protected settingsKey = computed(() => this.data().widgetSettingsKey ?? this.componentData.uid);
	protected settings = computed(() => this.widgetSettingsService.settingsFor(this.settingsKey())());

	location = computed(() => this.settings()[SETTING_LOCATION] ?? '');
	hasLocation = computed(
		() => !!this.settings()[SETTING_LATITUDE] && !!this.settings()[SETTING_LONGITUDE],
	);

	weather = signal<CxdevWeather | undefined>(undefined);
	loadError = signal(false);

	condition = computed(() => {
		const code = this.weather()?.weatherCode;
		return WEATHER_CONDITIONS.find((c) => c.codes.includes(code ?? -1));
	});

	constructor() {
		effect(() => {
			const settings = this.settings();
			const latitude = settings[SETTING_LATITUDE];
			const longitude = settings[SETTING_LONGITUDE];
			this.weather.set(undefined);
			this.loadError.set(false);
			if (latitude && longitude) {
				this.loadWeather(latitude, longitude);
			}
		});
	}

	private loadWeather(latitude: string, longitude: string) {
		this.http
			.get<any>(FORECAST_URL, {
				params: { latitude, longitude, current: 'temperature_2m,weather_code,wind_speed_10m' },
			})
			.subscribe({
				next: (response) =>
					this.weather.set({
						temperature: Math.round(response?.current?.temperature_2m),
						windSpeed: Math.round(response?.current?.wind_speed_10m),
						weatherCode: response?.current?.weather_code,
					}),
				error: () => this.loadError.set(true),
			});
	}
}
