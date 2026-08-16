import { OccConfig, OccEndpoint } from '@spartacus/core';

export const cxdevOccConfig: OccConfig = {
	backend: {
		occ: {
			endpoints: {
				dashboardConfig: 'users/${userId}/dashboard',
				dashboardWidgets: 'users/${userId}/dashboard/widgets',
				serviceRequestsCount: '/service-requests/count',
				serviceRequestOverview: '/service-requests',
				serviceRequestDetails: '/service-requests/${requestId}',
				saveCart: 'users/${userId}/carts/${cartId}/savedCart',
				createServiceTicket: '/service-requests/create',
				getAllServiceRequestForms: '/forms',
				serviceRequestForm: '/forms/${formId}',
				cxdevNews: 'cxdev/news/latest',
				cxdevAllNews: 'cxdev/news/all',
			},
		},
	},
};

declare module '@spartacus/core' {
	interface OccEndpoints {
		dashboardConfig: string | OccEndpoint;
		dashboardWidgets: string | OccEndpoint;
		serviceRequestsCount: string | OccEndpoint;
		serviceRequestOverview: string | OccEndpoint;
		serviceRequestDetails: string | OccEndpoint;
		createServiceTicket: string | OccEndpoint;
		getAllServiceRequestForms: string | OccEndpoint;
		serviceRequestForm: string | OccEndpoint;
		cxdevNews: string | OccEndpoint;
		cxdevAllNews: String | OccEndpoint;
	}
}
