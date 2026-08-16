import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NgSelectComponent } from '@ng-select/ng-select';
import {
	Country,
	GlobalMessageService,
	GlobalMessageType,
	Region,
	TranslatePipe,
	UrlPipe,
} from '@spartacus/core';
import {
	FormErrorsComponent,
	FormRequiredAsterisksComponent,
	FormRequiredLegendComponent,
	NgSelectA11yDirective,
	SpinnerComponent,
} from '@spartacus/storefront';
import { Title } from '@spartacus/user/profile/root';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { UserRegistrationFormService } from '@spartacus/organization/user-registration/components';
import { AsyncPipe, NgIf } from '@angular/common';
import { CxdevUserRegistrationFormService } from './cxdev-user-registration-form.service';

@Component({
	selector: 'cxdev-registration-form',
	templateUrl: './cxdev-registration-form.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
	standalone: true,
	providers: [
		{
			provide: UserRegistrationFormService,
			useClass: CxdevUserRegistrationFormService,
		},
	],
	imports: [
		FormsModule,
		ReactiveFormsModule,
		NgSelectComponent,
		NgSelectA11yDirective,
		FormRequiredLegendComponent,
		FormRequiredAsterisksComponent,
		FormErrorsComponent,
		RouterLink,
		SpinnerComponent,
		TranslatePipe,
		UrlPipe,
		NgIf,
		AsyncPipe,
	],
})
export class CxdevRegistrationFormComponent implements OnDestroy {
	protected userRegistrationFormService = inject(UserRegistrationFormService);
	protected globalMessageService = inject(GlobalMessageService, {
		optional: true,
	});

	titles$: Observable<Title[]> = this.userRegistrationFormService.getTitles();
	countries$: Observable<Country[]> = this.userRegistrationFormService.getCountries();
	regions$: Observable<Region[]> = this.userRegistrationFormService.getRegions();

	registerForm: FormGroup = this.userRegistrationFormService.form;
	isLoading$ = new BehaviorSubject(false);
	protected subscriptions = new Subscription();

	submit(event: Event): void {
		event.preventDefault();
		if (this.registerForm.valid) {
			this.isLoading$.next(true);
			this.subscriptions.add(
				this.userRegistrationFormService.registerUser(this.registerForm).subscribe({
					complete: () => this.isLoading$.next(false),
					error: () => {
						this.isLoading$.next(false);
						this.globalMessageService?.add(
							{ key: 'userRegistrationForm.messageToFailedToRegister' },
							GlobalMessageType.MSG_TYPE_ERROR,
						);
					},
				}),
			);
		} else {
			this.registerForm.markAllAsTouched();
		}
	}

	ngOnDestroy(): void {
		this.subscriptions.unsubscribe();
	}
}
