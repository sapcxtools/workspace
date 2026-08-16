import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { I18nModule } from '@spartacus/core';
import { CmsComponentData } from '@spartacus/storefront';
import { CxdevDashboardWidgetSettingsService } from '../../cxdev-dashboard-widget-settings.service';

const SETTING_TEXT = 'text';

@Component({
	selector: 'cxdev-dashboard-notepad',
	imports: [I18nModule],
	templateUrl: './cxdev-dashboard-notepad.component.html',
})
export class CxdevDashboardNotepadComponent {
	protected componentData: CmsComponentData<any> = inject(CmsComponentData);
	protected widgetSettingsService = inject(CxdevDashboardWidgetSettingsService);

	protected data = toSignal(this.componentData.data$, { initialValue: {} as any });
	protected settingsKey = computed(() => this.data().widgetSettingsKey ?? this.componentData.uid);
	protected settings = computed(() => this.widgetSettingsService.settingsFor(this.settingsKey())());

	text = computed(() => this.settings()[SETTING_TEXT] ?? '');

	onBlur(value: string) {
		if (value !== this.text()) {
			this.widgetSettingsService.updateSettings(this.settingsKey(), { [SETTING_TEXT]: value });
		}
	}
}
