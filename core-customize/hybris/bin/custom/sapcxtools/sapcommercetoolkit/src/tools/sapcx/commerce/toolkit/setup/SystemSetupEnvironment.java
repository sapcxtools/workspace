package tools.sapcx.commerce.toolkit.setup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.impex.ImportConfig;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Required;

/**
 * This facade handles the access to important properties for the system setup process. It also keeps track of imported
 * files and verifies the release numbers of the files accordingly.
 */
public final class SystemSetupEnvironment {
	static final Logger LOG = Logger.getLogger(SystemSetupEnvironment.class);
	@VisibleForTesting
	static final String LEGACYMODEKEY = "sapcommercetoolkit.impeximport.configuration.legacymode";
	@VisibleForTesting
	static final String ENABLECODEEXECUTIONKEY = "sapcommercetoolkit.impeximport.configuration.enablecodeexecution";
	@VisibleForTesting
	static final String VALIDATIONMODEKEY = "sapcommercetoolkit.impeximport.configuration.validationmode";
	@VisibleForTesting
	static final String DEFAULTLOCALEKEY = "sapcommercetoolkit.impeximport.configuration.defaultlocale";
	@VisibleForTesting
	static final String ISDEVELOPMENTKEY = "sapcommercetoolkit.impeximport.environment.isdevelopment";
	@VisibleForTesting
	static final String SUPPORTLOCALIZATIONKEY = "sapcommercetoolkit.impeximport.environment.supportlocalizedfiles";
	@VisibleForTesting
	static final String LASTPROCESSEDRELEASEVERSIONKEY = "sapcommercetoolkit.impeximport.configuration.lastprocessedreleaseversion";
	@VisibleForTesting
	static final String LASTPROCESSEDRELEASEITEMSKEY = "sapcommercetoolkit.impeximport.configuration.lastprocessedreleaseitems";
	@VisibleForTesting
	static final String FILE_HEADER = "This file is generated automatically by the sapcommercetoolkit extension. Do not change the file manually!";

	private Configuration persistentConfiguration = new BaseConfiguration();
	private ConfigurationService configurationService;

	public boolean useLegacyModeForImpEx() {
		return configurationService.getConfiguration().getBoolean(LEGACYMODEKEY, false);
	}

	public boolean enableCodeExecution() {
		return configurationService.getConfiguration().getBoolean(ENABLECODEEXECUTIONKEY, true);
	}

	public ImportConfig.ValidationMode getValidationMode() {
		String validationModeCode = configurationService.getConfiguration().getString(VALIDATIONMODEKEY, "strict");
		if ("relaxed".equalsIgnoreCase(validationModeCode)) {
			return ImportConfig.ValidationMode.RELAXED;
		} else {
			return ImportConfig.ValidationMode.STRICT;
		}
	}

	public Locale getDefaultLocaleForImpEx() {
		return getLocaleFromConfig(DEFAULTLOCALEKEY, Locale.ENGLISH);
	}

	public boolean isDevelopment() {
		return configurationService.getConfiguration().getBoolean(ISDEVELOPMENTKEY, false);
	}

	public boolean supportLocalizedImpExFiles() {
		return configurationService.getConfiguration().getBoolean(SUPPORTLOCALIZATIONKEY, false);
	}

	public void addProcessedItem(String version, String key) {
		setLastProcessedReleaseVersion(version);
		persistentConfiguration.addProperty(LASTPROCESSEDRELEASEITEMSKEY, key);
	}

	public void setLastProcessedReleaseVersion(String version) {
		String lastVersion = getLastProcessedReleaseVersion();
		if (version.compareToIgnoreCase(lastVersion) > 0) {
			persistentConfiguration.clearProperty(LASTPROCESSEDRELEASEITEMSKEY);
			persistentConfiguration.setProperty(LASTPROCESSEDRELEASEVERSIONKEY, version);
		}
	}

	public String getLastProcessedReleaseVersion() {
		return persistentConfiguration.getString(LASTPROCESSEDRELEASEVERSIONKEY, "");
	}

	public List<String> getLastProcessedItems() {
		ArrayList<String> keys = new ArrayList<>();
		persistentConfiguration.getList(LASTPROCESSEDRELEASEITEMSKEY).stream()
				.map(String.class::cast)
				.forEach(keys::add);
		return keys;
	}

	public List<String> getKeys(String prefix) {
		ArrayList<String> keys = new ArrayList<>();
		configurationService.getConfiguration().getKeys(prefix).forEachRemaining(keys::add);
		return keys;
	}

	public String mapKeyToFile(String key) {
		return configurationService.getConfiguration().getString(key, "");
	}

	private Locale getLocaleFromConfig(String key, Locale defaultValue) {
		String locale = configurationService.getConfiguration().getString(key);

		if (locale == null) {
			return defaultValue;
		}

		int countrySeparator = locale.indexOf('_');
		int variantSeparator = locale.indexOf('-');
		if (countrySeparator != -1 && variantSeparator != -1) {
			String language = locale.substring(0, countrySeparator);
			String country = locale.substring(countrySeparator + 1, variantSeparator);
			String variant = locale.substring(variantSeparator + 1);
			return new Locale(language, country, variant);
		}

		if (countrySeparator != -1) {
			String language = locale.substring(0, countrySeparator);
			String country = locale.substring(countrySeparator + 1);
			return new Locale(language, country);
		}

		return new Locale(locale);
	}

	@Required
	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public void setConfigurationFile(String fileName) {
		try {
			PropertiesConfiguration configuration = new PropertiesConfiguration();
			configuration.setFileName(fileName);

			File file = configuration.getFile();
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				if (!file.createNewFile()) {
					throw new IOException("Cannot create file at: " + fileName);
				}
				configuration.setHeader(FILE_HEADER);
				configuration.save();
			} else {
				configuration.load();
			}

			configuration.setAutoSave(true);
			configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
			this.persistentConfiguration = configuration;
		} catch (IOException | ConfigurationException e) {
			LOG.error("Cannot read or create persistent configuration file at: " + fileName + ". Please create the file manually and restart the server.", e);
		}
	}
}
