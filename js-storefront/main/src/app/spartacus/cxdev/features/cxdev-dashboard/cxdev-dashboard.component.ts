import { CommonModule } from '@angular/common';
import {
	Component,
	computed,
	effect,
	ElementRef,
	inject,
	signal,
	ViewContainerRef,
} from '@angular/core';
import { CxdevDashboardService } from './cxdev-dashboard.service';
import { CxdevDashboardWidgetSettingsService } from './cxdev-dashboard-widget-settings.service';
import {
	CxdevDashboardConfig,
	CxdevDashboardWidget,
	CxdevDashboardWidgetConfig,
	CxdevWidgetSettings,
} from './cxdev-dashboard.model';
import { CxdevWidgetComponent } from './components/cxdev-widget/cxdev-widget.component';
import { I18nModule } from '@spartacus/core';
import { ICON_TYPE, IconModule, LaunchDialogService } from '@spartacus/storefront';
import { filter, take } from 'rxjs';
import { CxdevWidgetSelectorItem } from './components/cxdev-widget-selector/cxdev-widget-selector-modal.component';
import {
	CxdevReorderDirective,
	CxdevReorderEvent,
} from '../../directives/cxdev-reorder/cxdev-reorder.directive';
import { copyWidgetSettingsKey, widgetSettingsKeyFor } from './cxdev-dashboard-widget-settings-key';

const NEW_CONFIG_CODE = '__new__';
const WIDGET_TRACK_ID = '__cxdevWidgetTrackId';

@Component({
	selector: 'cxdev-dashboard',
	imports: [CommonModule, CxdevWidgetComponent, CxdevReorderDirective, I18nModule, IconModule],
	providers: [CxdevDashboardService],
	templateUrl: './cxdev-dashboard.component.html',
})
export class CxdevDashboardComponent {
	protected service: CxdevDashboardService = inject(CxdevDashboardService);
	protected widgetSettingsService: CxdevDashboardWidgetSettingsService = inject(
		CxdevDashboardWidgetSettingsService,
	);
	protected launchDialogService = inject(LaunchDialogService);
	protected element: ElementRef = inject(ElementRef);
	protected vcr = inject(ViewContainerRef);

	configs = signal<CxdevDashboardConfig[]>([]);
	availableWidgets = signal<CxdevDashboardWidget[]>([]);
	availableWidgetsLoaded = signal(false);

	iconType = ICON_TYPE;

	constructor() {
		this.service.getDashboardConfigs().subscribe((configs) => this.configs.set(configs));

		// Expose the selected config's widget settings to dynamically created widget components
		effect(() => {
			const widgets = this.dashboardConfig().widgetConfigs ?? [];
			this.widgetSettingsService.publishSettings(
				Object.fromEntries(widgets.map((w) => [widgetSettingsKeyFor(w), w.settings ?? {}])),
			);
		});
		this.widgetSettingsService.registerUpdateHandler((settingsKey, settings) =>
			this.onWidgetSettingsChanged(settingsKey, settings),
		);
	}

	selectedConfigCode = signal<string | undefined>(undefined);
	isNewConfig = signal(false);
	editMode = signal(false);
	dirty = signal(false);
	sorting = signal(false);
	private nextWidgetTrackId = 0;

	/** Configs shown in the nav: all in edit mode, only active ones otherwise. */
	visibleConfigs = computed(() =>
		this.editMode() ? this.configs() : this.configs().filter((c) => c.active !== false),
	);

	/** Widgets shown in the grid. */
	visibleWidgets = computed(() =>
		[...(this.dashboardConfig().widgetConfigs ?? [])].sort((a, b) => a.position - b.position),
	);

	widgetSelectorItems = computed<CxdevWidgetSelectorItem[]>(() =>
		this.availableWidgets().map((widget) => ({
			widget,
			addable: widget.multipleOccurrenceAllowed || !this.isWidgetOnDashboard(widget),
		})),
	);

	/** The currently viewed config (resolves from selectedConfigCode or first active). */
	selectedConfig = computed<CxdevDashboardConfig>(() => {
		const configs = this.configs();
		const code = this.selectedConfigCode();
		if (!configs.length) return {} as CxdevDashboardConfig;
		if (code) return configs.find((c) => c.code === code) ?? configs[0];
		return configs.find((c) => c.active !== false) ?? configs[0];
	});

	editedDashboardConfig = signal({} as CxdevDashboardConfig);

	/** Merges live server state with any in-progress edits. */
	dashboardConfig = computed(() => ({
		...this.selectedConfig(),
		...this.editedDashboardConfig(),
	}));

	canMoveUp = computed(() => {
		const idx = this.configs().findIndex((c) => c.code === this.dashboardConfig().code);
		return !this.isNewConfig() && idx > 0;
	});

	canMoveDown = computed(() => {
		const configs = this.configs();
		const idx = configs.findIndex((c) => c.code === this.dashboardConfig().code);
		return !this.isNewConfig() && idx >= 0 && idx < configs.length - 1;
	});

	/** The last remaining config cannot be deleted. */
	canDeleteSelectedConfig = computed(
		() => !this.isNewConfig() && !!this.dashboardConfig().code && this.configs().length > 1,
	);

	// ── Config selection ────────────────────────────────────────────────────────

	setSelectedConfig(config: CxdevDashboardConfig) {
		if (this.isNewConfig()) {
			this.discardNewConfig();
		}
		this.selectedConfigCode.set(config.code);
		this.editedDashboardConfig.set({} as CxdevDashboardConfig);
		this.dirty.set(false);
		this.isNewConfig.set(false);
	}

	// ── Edit mode toggle ────────────────────────────────────────────────────────

	toggleEditMode() {
		const wasEditing = this.editMode();
		this.editMode.update((m) => !m);

		if (!wasEditing) {
			this.loadAvailableWidgets();
			return;
		}

		if (wasEditing) {
			if (this.dirty()) {
				this.saveCurrentConfig(() => this.ensureActiveSelection());
			} else {
				if (this.isNewConfig()) this.discardNewConfig();
				this.ensureActiveSelection();
			}
		}
	}

	// ── Create / delete ─────────────────────────────────────────────────────────

	createConfig() {
		const source = this.selectedConfig();
		const draft: CxdevDashboardConfig = {
			...source,
			code: NEW_CONFIG_CODE,
			name: `Copy of ${source.name ?? 'Dashboard'}`,
			active: true,
			position: this.configs().length,
			widgetConfigs: source.widgetConfigs?.map((w) => ({ ...w })) ?? [],
		};
		this.configs.update((c) => [...c, draft]);
		this.selectedConfigCode.set(NEW_CONFIG_CODE);
		this.editedDashboardConfig.set({} as CxdevDashboardConfig);
		this.editMode.set(true);
		this.loadAvailableWidgets();
		this.dirty.set(false);
		this.isNewConfig.set(true);
	}

	deleteConfig() {
		if (!this.canDeleteSelectedConfig()) return;

		const config = this.dashboardConfig();
		this.service.deleteDashboardConfig(config).subscribe(() => {
			this.configs.update((c) => c.filter((x) => x.code !== config.code));
			this.dirty.set(false);
			this.editedDashboardConfig.set({} as CxdevDashboardConfig);
			this.selectFirstConfig();
		});
	}

	// ── Position / sorting ──────────────────────────────────────────────────────

	moveConfig(direction: 'up' | 'down') {
		const allConfigs = [...this.configs()];
		const activeCode = this.dashboardConfig().code;
		const activeIndex = allConfigs.findIndex((c) => c.code === activeCode);
		const newIndex = direction === 'up' ? activeIndex - 1 : activeIndex + 1;

		if (activeIndex < 0 || newIndex < 0 || newIndex >= allConfigs.length) return;

		[allConfigs[activeIndex], allConfigs[newIndex]] = [
			allConfigs[newIndex],
			allConfigs[activeIndex],
		];

		// Reassign sequential positions
		const reindexed = allConfigs.map((c, i) => ({ ...c, position: i }));
		this.configs.set(reindexed);

		// Save the non-active config's new position immediately
		const otherConfig = reindexed[activeIndex];
		this.service.saveDashboardConfig(otherConfig).subscribe();

		// Mark dirty so the active config's new position is also saved on exit edit mode
		this.dirty.set(true);
	}

	// ── Config metadata editing ─────────────────────────────────────────────────

	updateConfigName(name: string) {
		this.editedDashboardConfig.update((c) => ({ ...c, name }));
		this.dirty.set(true);
	}

	updateConfigActive(active: boolean) {
		this.editedDashboardConfig.update((c) => ({ ...c, active }));
		this.dirty.set(true);
	}

	// ── Widget editing ──────────────────────────────────────────────────────────

	openWidgetSelector() {
		if (!this.availableWidgetsLoaded()) {
			this.service
				.getAvailableWidgets()
				.pipe(take(1))
				.subscribe((widgets) => {
					this.availableWidgets.set(widgets);
					this.availableWidgetsLoaded.set(true);
					this.openWidgetSelectorDialog();
				});
			return;
		}

		this.openWidgetSelectorDialog();
	}

	addWidget(widget: CxdevDashboardWidget) {
		if (!widget.multipleOccurrenceAllowed && this.isWidgetOnDashboard(widget)) return;

		const widgets = this.dashboardConfig().widgetConfigs ?? [];
		const nextPosition = widgets.reduce((position, w) => Math.max(position, w.position), -1) + 1;
		const widgetConfig: CxdevDashboardWidgetConfig = {
			position: nextPosition,
			columnSpan: widget.minColumnSpan,
			rowSpan: widget.minRowSpan,
			widget,
		};

		this.updateWidgetConfigs([...widgets, widgetConfig]);
	}

	removeWidget(widget: CxdevDashboardWidgetConfig) {
		const widgets = this.visibleWidgets()
			.filter((w) => w !== widget)
			.map((w, position) => this.cloneWidgetConfig(w, { position }));

		this.updateWidgetConfigs(widgets);
	}

	onWidgetResized(
		widget: CxdevDashboardWidgetConfig,
		{ columnSpan, rowSpan }: { columnSpan: number; rowSpan: number },
	) {
		const updatedWidgets = this.dashboardConfig().widgetConfigs.map((w) =>
			w === widget ? this.cloneWidgetConfig(w, { columnSpan, rowSpan }) : w,
		);
		this.updateWidgetConfigs(updatedWidgets);
	}

	/**
	 * Merges changed settings into the widget. In edit mode (settings overlay)
	 * and for unsaved drafts the change is saved together with the config on
	 * exiting edit mode; outside of edit mode (e.g. notepad blur) it is
	 * persisted right away.
	 */
	private onWidgetSettingsChanged(settingsKey: string, settings: CxdevWidgetSettings) {
		const widgets = this.dashboardConfig().widgetConfigs?.map((w) =>
			widgetSettingsKeyFor(w) === settingsKey
				? this.cloneWidgetConfig(w, {
						settings: this.mergeWidgetSettings(w.settings, settings),
					})
				: w,
		);
		this.editedDashboardConfig.update((c) => ({ ...c, widgetConfigs: widgets ?? [] }));

		if (this.isNewConfig() || this.editMode()) {
			this.dirty.set(true);
			return;
		}
		this.saveCurrentConfig();
	}

	private mergeWidgetSettings(
		currentSettings: CxdevWidgetSettings | undefined,
		changedSettings: CxdevWidgetSettings,
	): CxdevWidgetSettings | undefined {
		const mergedSettings = { ...currentSettings, ...changedSettings };
		Object.entries(changedSettings).forEach(([key, value]) => {
			if (value === '') delete mergedSettings[key];
		});

		return Object.keys(mergedSettings).length ? mergedSettings : undefined;
	}

	onWidgetsReordered({ from, to }: CxdevReorderEvent) {
		const widgets = [...this.visibleWidgets()];
		const [movedWidget] = widgets.splice(from, 1);
		if (!movedWidget) return;

		widgets.splice(to, 0, movedWidget);
		this.updateWidgetConfigs(this.reindexWidgets(widgets));
	}

	trackWidgetConfig(widgetConfig: CxdevDashboardWidgetConfig) {
		const trackedConfig = widgetConfig as CxdevDashboardWidgetConfig & { [WIDGET_TRACK_ID]?: string };

		if (!trackedConfig[WIDGET_TRACK_ID]) {
			Object.defineProperty(trackedConfig, WIDGET_TRACK_ID, {
				value: `${widgetConfig.widget.contentComponentUid}-${this.nextWidgetTrackId++}`,
				enumerable: false,
			});
		}

		return trackedConfig[WIDGET_TRACK_ID];
	}

	// ── Private helpers ─────────────────────────────────────────────────────────

	private loadAvailableWidgets() {
		if (this.availableWidgetsLoaded()) return;

		this.service
			.getAvailableWidgets()
			.pipe(take(1))
			.subscribe((widgets) => {
				this.availableWidgets.set(widgets);
				this.availableWidgetsLoaded.set(true);
			});
	}

	private openWidgetSelectorDialog() {
		this.launchDialogService
			.openDialog('CXDEV_WIDGET_SELECTOR', this.element, this.vcr, {
				items: this.widgetSelectorItems(),
			})
			?.pipe(take(1))
			.subscribe();

		this.launchDialogService.dialogClose
			.pipe(
				filter((result) => result !== undefined),
				take(1),
			)
			.subscribe((result) => {
				if (this.isDashboardWidget(result)) {
					this.addWidget(result);
				}
			});
	}

	private updateWidgetConfigs(widgetConfigs: CxdevDashboardWidgetConfig[]) {
		this.editedDashboardConfig.update((c) => ({ ...c, widgetConfigs }));
		this.dirty.set(true);
	}

	private reindexWidgets(widgetConfigs: CxdevDashboardWidgetConfig[]) {
		return widgetConfigs.map((widget, position) => this.cloneWidgetConfig(widget, { position }));
	}

	private cloneWidgetConfig(
		widgetConfig: CxdevDashboardWidgetConfig,
		updates: Partial<CxdevDashboardWidgetConfig>,
	) {
		const clone = { ...widgetConfig, ...updates };
		const trackId = this.trackWidgetConfig(widgetConfig);

		Object.defineProperty(clone, WIDGET_TRACK_ID, {
			value: trackId,
			enumerable: false,
		});
		copyWidgetSettingsKey(widgetConfig, clone);

		return clone;
	}

	private isWidgetOnDashboard(widget: CxdevDashboardWidget) {
		return (this.dashboardConfig().widgetConfigs ?? []).some(
			(w) =>
				w.widget.code === widget.code ||
				w.widget.contentComponentUid === widget.contentComponentUid,
		);
	}

	private isDashboardWidget(value: unknown): value is CxdevDashboardWidget {
		return !!value && typeof value === 'object' && 'contentComponentUid' in value;
	}

	private saveCurrentConfig(onComplete?: () => void) {
		const config = {
			...this.dashboardConfig(),
			widgetConfigs: this.reindexWidgets(this.visibleWidgets()),
		};
		const configToSave = this.isNewConfig() ? { ...config, code: undefined } : config;

		this.service.saveDashboardConfig(configToSave).subscribe((saved) => {
			if (this.isNewConfig()) {
				this.configs.update((c) => [...c.filter((x) => x.code !== NEW_CONFIG_CODE), saved]);
				this.selectedConfigCode.set(saved.code);
				this.isNewConfig.set(false);
			} else {
				this.configs.update((c) =>
					c.map((x) => (x.code === config.code ? { ...x, ...config } : x)),
				);
			}
			this.dirty.set(false);
			onComplete?.();
		});
	}

	private discardNewConfig() {
		this.configs.update((c) => c.filter((x) => x.code !== NEW_CONFIG_CODE));
		this.isNewConfig.set(false);
		this.selectFirstConfig();
	}

	/** After leaving edit mode, if the selected config is now hidden (active=false), switch to first visible. */
	private ensureActiveSelection() {
		if (this.dashboardConfig().active === false) {
			this.selectFirstConfig();
		}
	}

	private selectFirstConfig() {
		const first = this.configs().find((c) => c.active !== false) ?? this.configs()[0];
		this.selectedConfigCode.set(first?.code);
		this.editedDashboardConfig.set({} as CxdevDashboardConfig);
	}
}
