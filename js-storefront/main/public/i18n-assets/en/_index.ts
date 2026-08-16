import { address } from './address';
import { cart } from './cart';
import { common } from './common';
import { dashboard } from './dashboard';
import { facets } from './facets';
import { fake } from './fake';
import { forms } from './forms';
import { news } from './news';
import { order } from './order';
import { orders } from './orders';
import { organization } from './organization';
import { serviceRequests } from './serviceRequests';
import { siteThemeSwitcher } from './siteThemeSwitcher';

export const cxdevTranslationsEn = {
	cxdev: {
		forms,
		dashboard,
		fake,
		orders,
		serviceRequests,
		cart,
    facets,
    order,
		news,
	},
};

export const cxdevTranslationOverwritesEn = {
	common,
	siteThemeSwitcher,
	address,
	organization,
};
