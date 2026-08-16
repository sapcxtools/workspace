import { Injectable, InjectionToken } from '@angular/core';
import { Converter } from '@spartacus/core';
import { CxdevDashboardConfig, CxdevDashboardWidgetConfig } from './cxdev-dashboard.model';
import { attachWidgetSettingsKey } from './cxdev-dashboard-widget-settings-key';

export const CXDEV_DASHBOARD_CONFIG_NORMALIZER = new InjectionToken<
	Converter<any, CxdevDashboardConfig>
>('CxdevDashboardConfigNormalizer');

@Injectable({ providedIn: 'root' })
export class CxdevDashboardConfigNormalizer implements Converter<any, CxdevDashboardConfig> {
	convert(source: any, target?: CxdevDashboardConfig): CxdevDashboardConfig {
		target = target ?? { ...source };
		return {
			...target,
			widgetConfigs: (source.widgetConfigs ?? []).map((w: any) => {
				const deserializedSettings = w.settings ? this.deserializeSettings(w.settings) : undefined;
				const widgetConfig = {
					...w,
					settings: deserializedSettings?.settings,
				} as CxdevDashboardWidgetConfig;

				if (deserializedSettings?.widgetSettingsInstanceId) {
					this.attachWidgetSettingsInstanceId(
						widgetConfig,
						deserializedSettings.widgetSettingsInstanceId,
					);
				}

				return widgetConfig;
			}) as CxdevDashboardWidgetConfig[],
		};
	}

	private deserializeSettings(settings: { key: string; value: string }[]): {
		settings: Record<string, string>;
		widgetSettingsInstanceId?: string;
	} {
		const widgetSettingsInstanceId = this.extractWidgetSettingsInstanceId(settings);

		return {
			settings: Object.fromEntries(
				settings.map(({ key, value }) => [this.deserializeSettingKey(key), value]),
			) as Record<string, string>,
			widgetSettingsInstanceId,
		};
	}

	private deserializeSettingKey(key: string) {
		return key.replace(/^__widget:[^:]+:/, '');
	}

	private extractWidgetSettingsInstanceId(settings: { key: string; value: string }[]) {
		const key = settings
			.map((setting) => /^__widget:([^:]+):/.exec(setting.key)?.[1])
			.find(Boolean);
		return key && !/^\d+$/.test(key) ? key : undefined;
	}

	private attachWidgetSettingsInstanceId(
		widgetConfig: CxdevDashboardWidgetConfig,
		widgetSettingsInstanceId: string,
	) {
		attachWidgetSettingsKey(widgetConfig, widgetSettingsInstanceId);
	}
}
