import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideConfig, provideConfigFactory } from '@spartacus/core';
import { cxdevLayoutConfigFactory } from './cxdev-layout.config';
import { cxdevIconConfig } from './cxdev-icon.config';
import { cxdevSiteThemeConfig } from './cxdev.site-theme.config';
import { cxdevI18nConfig, cxdevI18nOverwriteConfig } from './cxdev-i18n.config';
import { cxdevRoutingConfig } from './cxdev-routing.config';
import { cxdevOccConfig } from './cxdev-occ.config';

@NgModule({
	declarations: [],
	imports: [CommonModule],
	providers: [
		provideConfigFactory(cxdevLayoutConfigFactory),
		provideConfig(cxdevIconConfig),
		provideConfig(cxdevSiteThemeConfig),
		provideConfig(cxdevI18nConfig),
		provideConfig(cxdevI18nOverwriteConfig),
		provideConfig(cxdevRoutingConfig),
		provideConfig(cxdevOccConfig),
		provideConfig(cxdevSiteThemeConfig),
	],
})
export class CxdevConfigurationsModule {}
