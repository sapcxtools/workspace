import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { FacetModule } from '@spartacus/storefront';
import { FacetDisplayComponent } from './facet-display.component';
import { FacetsModule } from '../facets/facets.module';

@NgModule({
	declarations: [FacetDisplayComponent],
	imports: [CommonModule, FacetsModule, FacetModule, I18nModule],
	exports: [FacetDisplayComponent],
})
export class FacetDisplayModule {}
