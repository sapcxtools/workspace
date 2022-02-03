package tools.sapcx.commerce.backoffice.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import tools.sapcx.commerce.toolkit.testing.testdoubles.user.UserServiceFake;

public class ConfigurableBackofficeLocaleServiceTests {
	private List<Locale> allLocalesFromSuperclass = new ArrayList<>();
	private ConfigurableBackofficeLocaleService service;
	private UserServiceFake userService;

	@Before
	public void setUp() throws Exception {
		service = new ConfigurableBackofficeLocaleService() {
			@Override
			public List<Locale> getAllLocalesFromSuperclass() {
				return allLocalesFromSuperclass;
			}

			@Override
			public List<Locale> getAllUILocalesFromSuperclass() {
				return allLocalesFromSuperclass;
			}
		};
		userService = UserServiceFake.fake();
		service.setUserService(userService);
	}

	@Test
	public void withoutConfiguration_ReturnsUnmodifiedResultsFromSuperclass() throws Exception {
		allLocalesFromSuperclass.addAll(List.of(Locale.ENGLISH, Locale.GERMAN, Locale.US, Locale.UK, Locale.GERMANY));

		service.setSortDataLocalesByIsoCode(false);
		service.setLocalesForBackofficeUi("");

		assertThat(service.getAllUILocales())
				.describedAs("Expected all UI locales to be identical with list from superclass!")
				.containsExactlyElementsOf(allLocalesFromSuperclass);
		assertThat(service.getAllLocales())
				.describedAs("Expected all locales to be identical with list from superclass!")
				.containsExactlyElementsOf(allLocalesFromSuperclass);
	}

	@Test
	public void withConfiguration_ReturnsReducedSetOfUiLocalesInTheCorrectOrder() throws Exception {
		allLocalesFromSuperclass.addAll(List.of(Locale.ENGLISH, Locale.GERMAN, Locale.US, Locale.UK, Locale.GERMANY));

		service.setSortDataLocalesByIsoCode(true);
		service.setLocalesForBackofficeUi("en_US,de_DE");

		assertThat(service.getAllUILocales())
				.describedAs("Expected all UI locales to be limited to en_US and de_DE!")
				.containsExactly(Locale.US, Locale.GERMANY);
		assertThat(service.getAllLocales())
				.describedAs("Expected all locales to be sorted by ISO code!")
				.containsExactly(Locale.GERMAN, Locale.GERMANY, Locale.ENGLISH, Locale.UK, Locale.US);
	}

	@Test
	public void withInvalidConfiguration_ReturnsUnmodifiedResultsFromSuperclass() throws Exception {
		allLocalesFromSuperclass.addAll(List.of(Locale.ENGLISH, Locale.GERMAN, Locale.US, Locale.UK, Locale.GERMANY));

		service.setSortDataLocalesByIsoCode(true);
		service.setLocalesForBackofficeUi("unknown");

		assertThat(service.getAllUILocales())
				.describedAs("Expected all UI locales to be identical with list from superclass!")
				.containsExactlyElementsOf(allLocalesFromSuperclass);
		assertThat(service.getAllLocales())
				.describedAs("Expected all locales to be sorted by ISO code!")
				.containsExactly(Locale.GERMAN, Locale.GERMANY, Locale.ENGLISH, Locale.UK, Locale.US);
	}

	@Test
	public void forLoginPageAsAnonymousUser_returnsAllUILocalesForBoth() throws Exception {
		allLocalesFromSuperclass.addAll(List.of(Locale.ENGLISH, Locale.GERMAN, Locale.US, Locale.UK, Locale.GERMANY));

		service.setSortDataLocalesByIsoCode(true);
		service.setLocalesForBackofficeUi("en_US,de_DE");

		userService.setCurrentUser(userService.getAnonymousUser());

		assertThat(service.getAllUILocales())
				.describedAs("Expected all UI locales to be limited to en_US and de_DE!")
				.containsExactly(Locale.US, Locale.GERMANY);
		assertThat(service.getAllLocales())
				.describedAs("Expected all locales to be limited to en_US and de_DE for anonymous users (on Login Page)!")
				.containsExactly(Locale.US, Locale.GERMANY);
	}
}
