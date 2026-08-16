import { inject, Injectable } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { Observable, tap } from 'rxjs';
import { UserRegistrationFormService } from '@spartacus/organization/user-registration/components';
import { OrganizationUserRegistration } from '@spartacus/organization/user-registration/root';
import { Router } from '@angular/router';

@Injectable()
export class CxdevUserRegistrationFormService extends UserRegistrationFormService {
	protected router = inject(Router);

	protected override buildForm(): FormGroup {
		const form = super.buildForm();
		if (!form.get('ustId')) {
			form.addControl('ustId', this.formBuilder.control('', Validators.required));
		}
		if (!form.get('terms')) {
			form.addControl('terms', this.formBuilder.control(false, Validators.requiredTrue));
		}
		return form;
	}

	override registerUser(form: FormGroup): Observable<OrganizationUserRegistration> {
		return this.organizationUserRegistrationFacade
			.registerUser({
				titleCode: form.get('titleCode')?.value,
				firstName: form.get('firstName')?.value,
				lastName: form.get('lastName')?.value,
				email: form.get('email')?.value,

				// Die neuen API-Keys:
				telephone: form.get('phoneNumber')?.value,
				companyName: form.get('companyName')?.value,
				companyAddressStreet: form.get('line1')?.value,
				companyAddressStreetLine2: form.get('line2')?.value,
				companyAddressCity: form.get('town')?.value,
				companyAddressRegion:
					form.get('region.isocode')?.value ?? form.get('region')?.get('isocode')?.value,
				companyAddressPostalCode: form.get('postalCode')?.value,
				companyAddressCountryIso:
					form.get('country.isocode')?.value ?? form.get('country')?.get('isocode')?.value,
				ustId: form.get('ustId')?.value,
			})
			.pipe(
				tap(() => {
					this.displayGlobalMessage();
					this.redirectToLogin();
					form.reset();
				}),
			);
	}
}
