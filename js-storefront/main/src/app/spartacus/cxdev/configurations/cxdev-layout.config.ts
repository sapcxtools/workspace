import { LayoutConfig } from '@spartacus/storefront';

export function cxdevLayoutConfigFactory(): LayoutConfig {
	return {
		layoutSlots: {
			DashboardPageTemplate: {
				slots: [
					'MiddleContent',
					'CenterLeftContentSlot',
					'PlaceholderContentSlot',
					'BottomContentSlot',
					'CenterRightContentSlot',
				],
			},
			NewsOverviewPageTemplate: {
				slots: [
					'MiddleContent',
					'CenterLeftContentSlot',
					'PlaceholderContentSlot',
					'NewsOverviewContent',
					'BottomContentSlot',
					'CenterRightContentSlot',
				],
			},
			ProductDetailsPageTemplate: {
				slots: ['Summary', 'Tabs', 'UpSelling', 'CrossSelling', 'PlaceholderContentSlot'],
			},
		},
	};
}
