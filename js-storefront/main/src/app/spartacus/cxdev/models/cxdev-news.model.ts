import { Image } from '@spartacus/core';

export interface CxdevNews {
	id?: string;
	title?: string;
	shortDescription?: string;
	teaserImage?: Image;
	url?: string;
	creationTime?: string;
}

export interface CxdevNewsList {
	news?: CxdevNews[];
}
