import { InjectionToken } from '@angular/core';
import { CxdevDashboardWidgetConfig } from '../../cxdev-dashboard.model';

/**
 * Context provided to widget-specific settings components rendered
 * inside the widget settings overlay.
 */
export interface CxdevWidgetSettingsContext {
	config: CxdevDashboardWidgetConfig;
	settingsKey: string;
}

export const CXDEV_WIDGET_SETTINGS_CONTEXT = new InjectionToken<CxdevWidgetSettingsContext>(
	'CXDEV_WIDGET_SETTINGS_CONTEXT',
);
