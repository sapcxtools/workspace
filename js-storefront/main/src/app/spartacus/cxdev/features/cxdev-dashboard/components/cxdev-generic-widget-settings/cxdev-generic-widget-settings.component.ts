import { Component, computed, inject } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { CxdevDashboardWidgetSettingsService } from '../../cxdev-dashboard-widget-settings.service';
import { CXDEV_WIDGET_SETTINGS_CONTEXT } from '../cxdev-widget-settings/cxdev-widget-settings-context';

export const SETTING_BACKGROUND_COLOR = 'backgroundColor';

interface CxdevWidgetThemeColorOption {
	labelKey: string;
	value: string;
}

const DEFAULT_CUSTOM_BACKGROUND_COLOR = '#ffffff';

const THEME_BACKGROUND_COLORS: CxdevWidgetThemeColorOption[] = [
	{
		labelKey: 'dashboard.widgetSettings.themeBackgroundDefault',
		value: 'var(--color-background-elevated)',
	},
	{
		labelKey: 'dashboard.widgetSettings.themeBackgroundRaised',
		value: 'var(--color-background-raised)',
	},
	{
		labelKey: 'dashboard.widgetSettings.themeBackgroundEmphasis',
		value: 'var(--color-background-emphasis)',
	},
	{
		labelKey: 'dashboard.widgetSettings.themeBackgroundAccent',
		value: 'var(--color-background-accent)',
	},
	{
		labelKey: 'dashboard.widgetSettings.themeBackgroundInfo',
		value: 'var(--color-background-info)',
	},
];

@Component({
	selector: 'cxdev-generic-widget-settings',
	imports: [I18nModule],
	templateUrl: './cxdev-generic-widget-settings.component.html',
})
export class CxdevGenericWidgetSettingsComponent {
	protected context = inject(CXDEV_WIDGET_SETTINGS_CONTEXT);
	protected widgetSettingsService = inject(CxdevDashboardWidgetSettingsService);

	protected settingsKey = this.context.settingsKey;
	protected settings = this.widgetSettingsService.settingsFor(this.settingsKey);

	themeColors = THEME_BACKGROUND_COLORS;

	backgroundColor = computed(() => this.settings()[SETTING_BACKGROUND_COLOR] ?? '');
	customBackgroundColor = computed(() =>
		this.backgroundColor().startsWith('#')
			? this.backgroundColor()
			: DEFAULT_CUSTOM_BACKGROUND_COLOR,
	);
	hasCustomBackgroundColor = computed(() => !!this.settings()[SETTING_BACKGROUND_COLOR]);

	isSelectedBackgroundColor(color: string) {
		return this.backgroundColor() === color;
	}

	updateBackgroundColor(color: string) {
		this.widgetSettingsService.updateSettings(this.settingsKey, {
			[SETTING_BACKGROUND_COLOR]: color,
		});
	}

	resetBackgroundColor() {
		this.widgetSettingsService.updateSettings(this.settingsKey, {
			[SETTING_BACKGROUND_COLOR]: '',
		});
	}
}
