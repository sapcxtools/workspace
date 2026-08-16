import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from '@spartacus/core';
import {
	pdfInvoicesTranslationChunksConfig,
	pdfInvoicesTranslationsEn,
} from '@spartacus/pdf-invoices/assets';
import { PDFInvoicesRootModule, PDF_INVOICES_FEATURE } from '@spartacus/pdf-invoices/root';

@NgModule({
	declarations: [],
	imports: [PDFInvoicesRootModule],
	providers: [
		provideConfig(<CmsConfig>{
			featureModules: {
				[PDF_INVOICES_FEATURE]: {
					module: () => import('@spartacus/pdf-invoices').then((m) => m.PDFInvoicesModule),
				},
			},
		}),
		provideConfig(<I18nConfig>{
			i18n: {
				resources: { en: pdfInvoicesTranslationsEn },
				chunks: pdfInvoicesTranslationChunksConfig,
			},
		}),
	],
})
export class PdfinvoicesModuleFeatureModule {}
