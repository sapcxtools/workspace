import { Injectable, InjectionToken } from '@angular/core';
import { Converter } from '@spartacus/core';
import { CxdevDashboardConfig } from './cxdev-dashboard.model';
import { widgetSettingsKeyFor } from './cxdev-dashboard-widget-settings-key';

export const CXDEV_DASHBOARD_CONFIG_SERIALIZER = new InjectionToken<
	Converter<CxdevDashboardConfig, any>
>('CxdevDashboardConfigSerializer');

@Injectable({ providedIn: 'root' })
export class CxdevDashboardConfigSerializer implements Converter<CxdevDashboardConfig, any> {
	convert(source: CxdevDashboardConfig, target?: any): any {
		target = target ?? { ...source };
		return {
			...target,
			widgetConfigs: (source.widgetConfigs ?? []).map((w) => {
				const { active, ...widgetConfig } = w as CxdevDashboardConfig['widgetConfigs'][number] & {
					active?: boolean;
				};

				return {
					...widgetConfig,
					settings: w.settings
						? this.serializeSettings(w.settings, widgetSettingsKeyFor(w))
						: undefined,
				};
			}),
		};
	}

	private serializeSettings(
		settings: Record<string, string>,
		widgetSettingsInstanceId: string,
	): { key: string; value: string }[] {
		return Object.entries(settings).map(([key, value]) => ({
			key: this.serializeSettingKey(key, widgetSettingsInstanceId),
			value,
		}));
	}

	private serializeSettingKey(key: string, widgetSettingsInstanceId: string) {
		return `__widget:${widgetSettingsInstanceId}:${key}`;
	}
}
