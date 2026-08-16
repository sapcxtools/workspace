import { CxdevDashboardWidgetConfig } from './cxdev-dashboard.model';

export const WIDGET_SETTINGS_INSTANCE_ID = '__cxdevWidgetSettingsInstanceId';

export function widgetSettingsKeyFor(widgetConfig: CxdevDashboardWidgetConfig) {
	const trackedConfig = widgetConfig as CxdevDashboardWidgetConfig & {
		[WIDGET_SETTINGS_INSTANCE_ID]?: string;
	};
	let widgetSettingsInstanceId = trackedConfig[WIDGET_SETTINGS_INSTANCE_ID];

	if (!widgetSettingsInstanceId) {
		widgetSettingsInstanceId = createWidgetSettingsInstanceId();
		attachWidgetSettingsKey(widgetConfig, widgetSettingsInstanceId);
	}

	return widgetSettingsInstanceId;
}

export function attachWidgetSettingsKey(
	widgetConfig: CxdevDashboardWidgetConfig,
	widgetSettingsInstanceId: string,
) {
	Object.defineProperty(widgetConfig, WIDGET_SETTINGS_INSTANCE_ID, {
		value: widgetSettingsInstanceId,
		enumerable: false,
	});
}

export function copyWidgetSettingsKey(
	source: CxdevDashboardWidgetConfig,
	target: CxdevDashboardWidgetConfig,
) {
	attachWidgetSettingsKey(target, widgetSettingsKeyFor(source));
}

function createWidgetSettingsInstanceId() {
	return (
		globalThis.crypto?.randomUUID?.() ??
		`widget-${Date.now().toString(36)}-${Math.random().toString(36).slice(2)}`
	);
}
