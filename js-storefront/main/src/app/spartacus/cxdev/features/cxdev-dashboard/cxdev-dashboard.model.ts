import { CmsLinkComponent, CmsComponent } from '@spartacus/core';
import { Media } from '@spartacus/storefront';

export interface CxdevDashboardConfig {
	code?: string;
	name?: string;
	active?: boolean;
	position?: number;
	widgetConfigs: CxdevDashboardWidgetConfig[];
}

export interface CxdevDashboardWidgetConfig {
	position: number;
	columnSpan: number;
	rowSpan: number;
	widget: CxdevDashboardWidget;
	settings?: CxdevWidgetSettings;
}

export interface CxdevDashboardWidget {
	code: string;
	contentComponentUid: string;
	contentComponentType: string;
	minColumnSpan: number;
	minRowSpan: number;
	multipleOccurrenceAllowed: boolean;
	displayName: string;
}

/** Generic widget-specific settings as key-value pairs, e.g. notepad text. */
export type CxdevWidgetSettings = Record<string, string>;

export interface CxdevServiceRequestCount {
	openRequests: number;
	closedRequests: number;
}

// cms:
export interface CxdevDashboardTeaserCmsComponent extends CmsLinkComponent {
	headline: string;
	description: string;
	icon: Media;
}

export interface CxdevDashboardProductListCmsComponent extends CmsComponent {
	productCount: number;
	category: string;
	linkText: string;
}
