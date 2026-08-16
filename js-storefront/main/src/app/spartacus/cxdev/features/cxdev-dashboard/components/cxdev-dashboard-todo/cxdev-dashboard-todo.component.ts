import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { I18nModule } from '@spartacus/core';
import { CmsComponentData } from '@spartacus/storefront';
import { CxdevDashboardWidgetSettingsService } from '../../cxdev-dashboard-widget-settings.service';

const SETTING_ITEMS = 'items';

export interface CxdevTodoItem {
	text: string;
	done: boolean;
}

@Component({
	selector: 'cxdev-dashboard-todo',
	imports: [I18nModule],
	templateUrl: './cxdev-dashboard-todo.component.html',
})
export class CxdevDashboardTodoComponent {
	protected componentData: CmsComponentData<any> = inject(CmsComponentData);
	protected widgetSettingsService = inject(CxdevDashboardWidgetSettingsService);

	protected data = toSignal(this.componentData.data$, { initialValue: {} as any });
	protected settingsKey = computed(() => this.data().widgetSettingsKey ?? this.componentData.uid);
	protected settings = computed(() => this.widgetSettingsService.settingsFor(this.settingsKey())());

	items = computed<CxdevTodoItem[]>(() => {
		try {
			const parsed = JSON.parse(this.settings()[SETTING_ITEMS] ?? '[]');
			return Array.isArray(parsed) ? parsed : [];
		} catch {
			return [];
		}
	});

	addItem(input: HTMLInputElement) {
		const text = input.value.trim();
		if (!text) return;
		this.saveItems([...this.items(), { text, done: false }]);
		input.value = '';
	}

	toggleItem(index: number) {
		this.saveItems(
			this.items().map((item, i) => (i === index ? { ...item, done: !item.done } : item)),
		);
	}

	removeItem(index: number) {
		this.saveItems(this.items().filter((_, i) => i !== index));
	}

	private saveItems(items: CxdevTodoItem[]) {
		this.widgetSettingsService.updateSettings(this.settingsKey(), {
			[SETTING_ITEMS]: JSON.stringify(items),
		});
	}
}
