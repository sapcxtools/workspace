import { computed, Injectable, Signal, signal } from '@angular/core';
import { CxdevWidgetSettings } from './cxdev-dashboard.model';

/**
 * Bridge between the dashboard and dynamically created widget components.
 *
 * The dashboard publishes the widget settings of the currently selected config;
 * widget components read their settings via {@link settingsFor} and push changes
 * back via {@link updateSettings}, which the dashboard persists with the config.
 *
 * Provided in root so that both widget components (created through the CMS
 * component wrapper) and the widget settings dialog (launched at root level
 * via the LaunchDialogService) can access it. The dashboard re-registers its
 * state whenever it is created.
 */
@Injectable({ providedIn: 'root' })
export class CxdevDashboardWidgetSettingsService {
	private readonly settingsByKey = signal<Record<string, CxdevWidgetSettings>>({});

	private updateHandler?: (settingsKey: string, settings: CxdevWidgetSettings) => void;

	/** Publishes the widget settings of the currently selected config (called by the dashboard). */
	publishSettings(settingsByKey: Record<string, CxdevWidgetSettings>) {
		this.settingsByKey.set(settingsByKey);
	}

	/** Registers the handler that persists setting changes (called by the dashboard). */
	registerUpdateHandler(handler: (settingsKey: string, settings: CxdevWidgetSettings) => void) {
		this.updateHandler = handler;
	}

	/** Reactive view on the settings of the widget identified by the given instance key. */
	settingsFor(settingsKey: string): Signal<CxdevWidgetSettings> {
		return computed(() => this.settingsByKey()[settingsKey] ?? {});
	}

	/** Merges the given settings into the widget's settings and persists them (called by widgets). */
	updateSettings(settingsKey: string, settings: CxdevWidgetSettings) {
		this.updateHandler?.(settingsKey, settings);
	}
}
