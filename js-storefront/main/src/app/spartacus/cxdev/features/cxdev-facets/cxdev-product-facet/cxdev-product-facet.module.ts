import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { I18nModule } from '@spartacus/core';
import { CxdevProductFacetComponent } from './cxdev-product-facet.component';
import { FacetDisplayModule } from '../facet-display/facet-display.module';
import { FacetsModule } from '../facets/facets.module';

@NgModule({
	declarations: [CxdevProductFacetComponent],
	imports: [CommonModule, FacetDisplayModule, I18nModule, RouterModule, FacetsModule],
})
export class CxdevProductFacetModule {}
