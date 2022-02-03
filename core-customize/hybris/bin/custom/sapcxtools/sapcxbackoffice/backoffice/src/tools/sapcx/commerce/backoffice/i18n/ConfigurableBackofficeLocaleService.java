package tools.sapcx.commerce.backoffice.i18n;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.hybris.backoffice.i18n.BackofficeLocaleService;

import de.hybris.platform.servicelayer.user.UserService;

import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Required;

/**
 * Enhancement of the {@link BackofficeLocaleService} to enhance configuration possibilities!
 *
 * This class introduces the configuration possibilities for activating a sort order based on the ISO code,
 * such that all data locales appear in the order of their ISO code within the backoffice cockpit. To activate
 * this feature, simply set the {@code orderLanguagesByIsoCode} property to {@code true}.
 *
 * In addition, the UI locales can be limited by define a comma-separated list of ISO codes.
 */
public class ConfigurableBackofficeLocaleService extends BackofficeLocaleService {
	private UserService userService;
	private Comparator<Locale> localeComparator = Comparator.comparing(Locale::toString);

	private boolean sortDataLocalesByIsoCode = false;
	private List<String> localesForBackofficeUi = new LinkedList<>();
	private List<Locale> uiLocales;

	@Override
	public List<Locale> getAllLocales() {
		if (askForAllLocalesOnLoginScreen()) {
			return getAllUILocales();
		}

		List<Locale> dataLocales = getAllLocalesFromSuperclass();
		if (sortDataLocalesByIsoCode) {
			Collections.sort(dataLocales, localeComparator);
		}
		return dataLocales;
	}

	private boolean askForAllLocalesOnLoginScreen() {
		return userService.isAnonymousUser(userService.getCurrentUser());
	}

	@Override
	public List<Locale> getAllUILocales() {
		if (this.uiLocales == null) {
			this.initializeUILocales();
		}
		return this.uiLocales;
	}

	private synchronized void initializeUILocales() {
		if (this.uiLocales == null) {
			if (this.localesForBackofficeUi.isEmpty()) {
				this.uiLocales = getAllUILocalesFromSuperclass();
			} else {
				Map<String, Locale> allLocales = getAllLocalesFromSuperclass().stream()
						.collect(Collectors.toMap(Locale::toString, Function.identity()));
				List<Locale> tmpLocales = new ArrayList<>(localesForBackofficeUi.size());
				for (String configuredLocale : localesForBackofficeUi) {
					if (allLocales.containsKey(configuredLocale)) {
						tmpLocales.add(allLocales.get(configuredLocale));
					}
				}

				if (!tmpLocales.isEmpty()) {
					this.uiLocales = Collections.unmodifiableList(tmpLocales);
				} else {
					this.uiLocales = getAllUILocalesFromSuperclass();
				}
			}
		}
	}

	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Required
	public void setSortDataLocalesByIsoCode(boolean sortDataLocalesByIsoCode) {
		this.sortDataLocalesByIsoCode = sortDataLocalesByIsoCode;
	}

	@Required
	public void setLocalesForBackofficeUi(String localesForBackofficeUi) {
		this.localesForBackofficeUi.clear();

		if (localesForBackofficeUi != null && !localesForBackofficeUi.isEmpty()) {
			String[] isoCodes = localesForBackofficeUi.split(",");
			for (String isoCode : isoCodes) {
				this.localesForBackofficeUi.add(isoCode.trim());
			}
		}
	}

	/**
	 * This method was introduced for testing purpose. Without this method a unit test case would have to setup the whole infrastructure
	 * required by the superclass. With this method in place, we can simply mock the answer from the superclass by overriding the method
	 * within the test.
	 *
	 * @return the result of invoking {@link #getAllLocales()} on the superclass
	 */
	@VisibleForTesting
	protected List<Locale> getAllLocalesFromSuperclass() {
		return super.getAllLocales();
	}

	/**
	 * This method was introduced for testing purpose. Without this method a unit test case would have to setup the whole infrastructure
	 * required by the superclass. With this method in place, we can simply mock the answer from the superclass by overriding the method
	 * within the test.
	 *
	 * @return the result of invoking {@link #getAllUILocales()} on the superclass
	 */
	@VisibleForTesting
	protected List<Locale> getAllUILocalesFromSuperclass() {
		return super.getAllUILocales();
	}
}
