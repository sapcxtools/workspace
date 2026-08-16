import { NgxSliderModule } from '@angular-slider/ngx-slider';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { I18nModule } from '@spartacus/core';
import { IconModule } from '@spartacus/storefront';
import { CheckboxFacetComponent } from './checkbox-facet/checkbox-facet.component';
import { RadioFacetComponent } from './radio-facet/radio-facet.component';
import { RangeFacetComponent } from './range-facet/range-facet.component';
import { SelectFacetComponent } from './select-facet/select-facet.component';
import { SliderFacetComponent } from './slider-facet/slider-facet.component';

@NgModule({
	declarations: [
		RadioFacetComponent,
		RangeFacetComponent,
		SelectFacetComponent,
		SliderFacetComponent,
		CheckboxFacetComponent,
	],
	imports: [
		CommonModule,
		FormsModule,
		ReactiveFormsModule,
		IconModule,
		NgSelectModule,
		I18nModule,
		NgxSliderModule,
		RouterModule,
	],
	exports: [
		RadioFacetComponent,
		RangeFacetComponent,
		SelectFacetComponent,
		SliderFacetComponent,
		CheckboxFacetComponent,
	],
})
export class FacetsModule {}
