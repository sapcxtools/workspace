import { NgIf, NgSwitch, NgSwitchCase, AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormControl } from '@angular/forms';
import { SavedCartFormDialogComponent } from '@spartacus/cart/saved-cart/components';
import { SavedCartFormType } from '@spartacus/cart/saved-cart/root';
import { TranslatePipe } from '@spartacus/core';
import {
	FocusDirective,
	IconComponent,
	FormRequiredLegendComponent,
	FormRequiredAsterisksComponent,
	FormErrorsComponent,
} from '@spartacus/storefront';
import { take } from 'rxjs';
import { CxdevSavedCartService } from './cxdev-saved-cart.service';
import { ActiveCartFacade } from '@spartacus/cart/base/root';

@Component({
	selector: 'cx-saved-cart-form-dialog',
	templateUrl: './cxdev-saved-cart.component.html',
	standalone: true,
	imports: [
		FocusDirective,
		FormsModule,
		ReactiveFormsModule,
		IconComponent,
		FormRequiredLegendComponent,
		FormRequiredAsterisksComponent,
		FormErrorsComponent,
		AsyncPipe,
		TranslatePipe,
	],
})
export class DpSavedCartFormDialogComponent extends SavedCartFormDialogComponent {
	protected dpSavedCartService = inject(CxdevSavedCartService);
	private activeCartService = inject(ActiveCartFacade);

	protected override build(cart?: any): void {
		super.build(cart);

		this.form.addControl('cxdevKeepCartCheckbox', new UntypedFormControl(false));
	}

	override saveOrEditCart(cartId: string): void {
		if (this.form.invalid) {
			this.form.markAllAsTouched();
			return;
		}
		const name = this.form.get('name')?.value;
		const description = this.form.get('description')?.value || '-';
		const cxdevKeepCartCheckbox = this.form.get('cxdevKeepCartCheckbox')?.value;

		if (this.layoutOption === SavedCartFormType.SAVE) {
			this.dpSavedCartService
				.saveCartWithCustomParam(cartId, name, description, cxdevKeepCartCheckbox)
				.pipe(take(1))
				.subscribe({
					next: () => {
						this.savedCartService.loadSavedCarts();
						if (!cxdevKeepCartCheckbox) {
							this.routingService.go({ cxRoute: 'savedCarts' });
							this.activeCartService.reloadActiveCart();
						}
						this.onComplete(true);
					},
					error: () => this.onComplete(false),
				});
		} else {
			super.saveOrEditCart(cartId);
		}
	}
}
