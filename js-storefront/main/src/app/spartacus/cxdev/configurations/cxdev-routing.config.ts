import { RoutingConfig } from '@spartacus/core';

export const cxdevRoutingConfig: RoutingConfig = {
	routing: {
		protected: true,
		routes: {
			homepage: {
				paths: [''],
				protected: true,
			},
			contact: {
				paths: ['contact'],
				protected: false,
			},
			faq: {
				paths: ['faq'],
				protected: false,
			},
			imprint: {
				paths: ['imprint'],
				protected: false,
			},
			serviceRequestOverview: {
				paths: ['service-requests'],
				protected: true,
			},
			serviceRequestDetail: {
				paths: ['service-request/:id'],
				paramsMapping: {
					id: 'id',
				},
				protected: true,
			},
			product: {
				paths: ['product/:productCode'],
				paramsMapping: { productCode: 'code' },
			},
		},
	},
};
