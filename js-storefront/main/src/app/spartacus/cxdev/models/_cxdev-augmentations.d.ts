import { CmsComponent, ContentSlotComponentData } from '@spartacus/core';

declare module '@spartacus/core' {
	export interface ContentSlotComponentData {
		content?: any;
	}
}
declare module '@spartacus/organization/user-registration/root' {
	interface OrganizationUserRegistration {
		telephone?: string;
		companyName?: string;
		companyAddressStreet?: string;
		companyAddressStreetLine2?: string;
		companyAddressCity?: string;
		companyAddressRegion?: string;
		companyAddressPostalCode?: string;
		companyAddressCountryIso?: string;
		ustId?: string;
	}
}

declare module '@spartacus/order/root' {
	export interface Order {
		trackingUrl?: string;
	}
}
