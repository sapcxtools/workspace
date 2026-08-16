import { NgModule } from '@angular/core';
import {
	FeaturesConfig,
	OccConfig,
	provideConfig,
	provideConfigFactory,
	SiteContextConfig,
} from '@spartacus/core';
import { defaultB2bOccConfig } from '@spartacus/setup';
import {
	defaultCmsContentProviders,
	layoutConfigFactory,
	mediaConfig,
} from '@spartacus/storefront';
import { Meta } from '@angular/platform-browser';
import { OCC_BASE_URL_META_TAG_NAME, OCC_BASE_URL_META_TAG_PLACEHOLDER } from '@spartacus/core';

@NgModule({
	declarations: [],
	imports: [],
	providers: [
		provideConfigFactory(dynamicOccConfig, [Meta]),
		provideConfigFactory(layoutConfigFactory),
		provideConfig(mediaConfig),
		...defaultCmsContentProviders,
		provideConfig({
			authentication: {
				client_id: 'storefront',
			},
		}),
		provideConfig(<SiteContextConfig>{
			context: {
				urlParameters: ['baseSite', 'language', 'currency'],
				baseSite: ['cxdev', 'electronics-spa', 'powertools-spa', 'apparel-uk-spa'],
				language: ['en', 'de'],
				currency: ['EUR', 'USD', 'GBP'],
				theme: ['cxdev-theme-light'],
			},
		}),
		provideConfig(<FeaturesConfig>{
			features: {
				level: '221121.7',
			},
		}),
		provideConfig(defaultB2bOccConfig),
	],
})
export class SpartacusConfigurationModule {}

function dynamicOccConfig(meta: Meta): OccConfig {
	return {
		backend: {
			occ: {
				baseUrl: getOccBaseUrl(meta),
			},
		},
	};
}

function getOccBaseUrl(meta: Meta): string {
	let occBaseUrl = meta
		.getTag("name='" + OCC_BASE_URL_META_TAG_NAME + "'")
		?.getAttribute('content');
	if (
		occBaseUrl !== undefined &&
		occBaseUrl != null &&
		occBaseUrl != OCC_BASE_URL_META_TAG_PLACEHOLDER
	) {
		return occBaseUrl;
	} else {
		let apiProtocol = window.location.protocol;
		let apiHost = mapApiHostname(window.location);
		let apiPort = mapApiPort(window.location);
		if (apiPort == '') {
			return apiProtocol + '//' + apiHost;
		} else {
			return apiProtocol + '//' + apiHost + ':' + apiPort;
		}
	}
}

function mapApiHostname(location: Location): string {
	let frontendHost = location.hostname;
	let firstSegment = frontendHost.split('.')[0];
	let remainingSegments = frontendHost.split('.').slice(1).join('.');

	if (remainingSegments == '') {
		return firstSegment;
	} else if (firstSegment == 'local' || remainingSegments == 'local.cxdev.me') {
		return location.hostname;
	} else {
		switch (firstSegment) {
			case 'www-d1':
				return 'api-d1' + '.' + remainingSegments;
			case 'www-s1':
				return 'api-s1' + '.' + remainingSegments;
			default:
				return 'api' + '.' + remainingSegments;
		}
	}
}

function mapApiPort(location: Location): string {
	if (location.protocol == 'http:' && location.port == '4200') {
		return '9001';
	} else if (location.protocol == 'https:' && location.port == '4200') {
		return '9002';
	}
	return location.port;
}
