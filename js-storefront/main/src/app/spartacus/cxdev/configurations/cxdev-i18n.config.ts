import { translationChunksConfig, translationsDe, translationsEn } from '@spartacus/assets';
import { I18nConfig } from '@spartacus/core';
import { cxdevTranslationChunks } from '../../../../../public/i18n-assets/cxdev-translations';
import {
	cxdevTranslationOverwritesDe,
	cxdevTranslationsDe,
} from '../../../../../public/i18n-assets/de/_index';
import {
	cxdevTranslationOverwritesEn,
	cxdevTranslationsEn,
} from '../../../../../public/i18n-assets/en/_index';
import {
	cartBaseTranslationChunksConfig,
	cartBaseTranslationsDe,
	cartBaseTranslationsEn,
} from '@spartacus/cart/base/assets';
import {
	importExportTranslationChunksConfig,
	importExportTranslationsDe,
	importExportTranslationsEn,
} from '@spartacus/cart/import-export/assets';
import {
	quickOrderTranslationChunksConfig,
	quickOrderTranslationsDe,
	quickOrderTranslationsEn,
} from '@spartacus/cart/quick-order/assets';
import {
	savedCartTranslationChunksConfig,
	savedCartTranslationsDe,
	savedCartTranslationsEn,
} from '@spartacus/cart/saved-cart/assets';
import {
	checkoutTranslationChunksConfig,
	checkoutTranslationsDe,
	checkoutTranslationsEn,
} from '@spartacus/checkout/base/assets';
import {
	orderTranslationChunksConfig,
	orderTranslationsDe,
	orderTranslationsEn,
} from '@spartacus/order/assets';
import {
	organizationTranslationChunksConfig,
	organizationTranslationsDe,
	organizationTranslationsEn,
} from '@spartacus/organization/administration/assets';
import {
	userAccountTranslationChunksConfig,
	userAccountTranslationsDe,
	userAccountTranslationsEn,
} from '@spartacus/user/account/assets';
import {
	userProfileTranslationChunksConfig,
	userProfileTranslationsDe,
	userProfileTranslationsEn,
} from '@spartacus/user/profile/assets';
import {
	checkoutB2BTranslationChunksConfig,
	checkoutB2BTranslationsDe,
	checkoutB2BTranslationsEn,
} from '@spartacus/checkout/b2b/assets';
import {
	pdfInvoicesTranslationChunksConfig,
	pdfInvoicesTranslationsDe,
	pdfInvoicesTranslationsEn,
} from '@spartacus/pdf-invoices/assets';
import { newsOverviewTranslationsEn, newsOverviewTranslationsDe } from '../features/cxdev-news/cxdev-news-overview/cxdev-news-overview.component';

export const cxdevI18nConfig: I18nConfig = {
	i18n: {
		resources: {
			en: {
				...translationsEn,
				...cartBaseTranslationsEn,
				...importExportTranslationsEn,
				...quickOrderTranslationsEn,
				...savedCartTranslationsEn,
				...checkoutTranslationsEn,
				...checkoutB2BTranslationsEn,
				...orderTranslationsEn,
				...organizationTranslationsEn,
				...userAccountTranslationsEn,
				...userProfileTranslationsEn,
				...pdfInvoicesTranslationsEn,
				...cxdevTranslationsEn,
				...newsOverviewTranslationsEn,
			},
			de: {
				...translationsDe,
				...cartBaseTranslationsDe,
				...importExportTranslationsDe,
				...quickOrderTranslationsDe,
				...savedCartTranslationsDe,
				...checkoutTranslationsDe,
				...checkoutB2BTranslationsDe,
				...orderTranslationsDe,
				...organizationTranslationsDe,
				...userAccountTranslationsDe,
				...userProfileTranslationsDe,
				...pdfInvoicesTranslationsDe,
				...cxdevTranslationsDe,
				...newsOverviewTranslationsDe,
			},
		},
		chunks: {
			...translationChunksConfig,
			...cartBaseTranslationChunksConfig,
			...importExportTranslationChunksConfig,
			...quickOrderTranslationChunksConfig,
			...savedCartTranslationChunksConfig,
			...checkoutTranslationChunksConfig,
			...checkoutB2BTranslationChunksConfig,
			...orderTranslationChunksConfig,
			...organizationTranslationChunksConfig,
			...userAccountTranslationChunksConfig,
			...userProfileTranslationChunksConfig,
			...pdfInvoicesTranslationChunksConfig,
			...cxdevTranslationChunks,
		},
		fallbackLang: 'en',
	},
};

export const cxdevI18nOverwriteConfig: I18nConfig = {
	i18n: {
		resources: {
			en: {
				...cxdevTranslationOverwritesEn,
			},
			de: {
				...cxdevTranslationOverwritesDe,
			},
		},
	},
};
