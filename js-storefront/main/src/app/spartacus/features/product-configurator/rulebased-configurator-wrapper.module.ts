import { NgModule } from '@angular/core';
import { RulebasedConfiguratorModule } from '@spartacus/product-configurator/rulebased';
import { RulebasedCpqConfiguratorModule } from '@spartacus/product-configurator/rulebased/cpq';

@NgModule({
	declarations: [],
	imports: [RulebasedConfiguratorModule, RulebasedCpqConfiguratorModule],
})
export class RulebasedConfiguratorWrapperModule {}
