import { Type } from '@angular/core';
import { CxdevDashboardWeatherSettingsComponent } from '../cxdev-dashboard-weather/cxdev-dashboard-weather-settings.component';
import { CxdevDashboardClockSettingsComponent } from '../cxdev-dashboard-clock/cxdev-dashboard-clock-settings.component';

/**
 * Maps a widget's contentComponentType (CMS flexType) to the component
 * rendered in the widget settings overlay for that widget type.
 */
export const CXDEV_WIDGET_SETTINGS_COMPONENTS: Record<string, Type<unknown>> = {
	CxdevDashboardWeatherComponent: CxdevDashboardWeatherSettingsComponent,
	CxdevDashboardClockComponent: CxdevDashboardClockSettingsComponent,
};
