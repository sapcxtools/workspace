package tools.sapcx.commerce.toolkit.impex.executor;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

import tools.sapcx.commerce.toolkit.setup.SystemSetupEnvironment;

/**
 * Implementation of the {@link ImpExDataImportExecutor} interface, importing the given file with the preconfigured
 * impex import configuration.
 *
 * @see SystemSetupEnvironment
 */
public class SynchronousImpExDataImportExecutor implements ImpExDataImportExecutor, ResourceLoaderAware {
	private ImpExDataImporterLogger logger = new ImpExDataImporterLogger();
	private ResourceLoader resourceLoader;
	private SystemSetupEnvironment environment;
	private CommonI18NService commonI18NService;
	private ImportService importService;

	@Override
	public void importImpexFile(SystemSetupContext context, final String file, final String fileEncoding) {
		this.importImpexFile(context, file, fileEncoding, true);
	}

	private void importImpexFile(SystemSetupContext context, final String file, final String fileEncoding, boolean isMainImportFile) {
		if (isMainImportFile && context.getProcess().isUpdate()) {
			String extension = StringUtils.substringAfterLast(file, ".");
			String filePath = StringUtils.removeEnd(file, "." + extension);
			String cleanupFilePath = String.format("%s_%s.%s", filePath, "cleanup", extension);
			importImpexFile(context, cleanupFilePath, fileEncoding, false);
		}

		String logPrefix = ">> Import of file [" + file + "] -> ";
		try (final InputStream resourceAsStream = resourceLoader.getResource(file).getInputStream()) {
			if (resourceAsStream != null) {
				getLogger().info(context, logPrefix + "STARTED.");

				final ImportConfig importConfig = new ImportConfig();
				importConfig.setScript(new StreamBasedImpExResource(resourceAsStream, fileEncoding));
				importConfig.setSynchronous(Boolean.TRUE);

				importConfig.setLegacyMode(environment.useLegacyModeForImpEx());
				importConfig.setEnableCodeExecution(environment.enableCodeExecution());
				importConfig.setValidationMode(environment.getValidationMode());
				importConfig.setLocale(environment.getDefaultLocaleForImpEx());

				final ImportResult importResult = importService.importData(importConfig);
				if (importResult.isError()) {
					getLogger().error(context, logPrefix + "FAILED!");
				} else {
					getLogger().info(context, logPrefix + "SUCCESSFUL!");
				}

				if (isMainImportFile && environment.supportLocalizedImpExFiles()) {
					String extension = StringUtils.substringAfterLast(file, ".");
					String filePath = StringUtils.removeEnd(file, "." + extension);
					List<LanguageModel> languages = commonI18NService.getAllLanguages();
					for (LanguageModel language : CollectionUtils.emptyIfNull(languages)) {
						String languageFilePath = String.format("%s_%s.%s", filePath, language.getIsocode(), extension);
						importImpexFile(context, languageFilePath, fileEncoding, false);
					}
				}
			}
		} catch (FileNotFoundException e) {
			if (isMainImportFile) {
				getLogger().error(context, logPrefix + "ERROR (MISSING FILE)!");
			} else {
				getLogger().debug(context, logPrefix + "SKIPPED (MISSING FILE).");
			}
		} catch (Exception e) {
			getLogger().error(context, logPrefix + "FAILED!", e);
		}
	}

	@Override
	public ImpExDataImporterLogger getLogger() {
		return logger;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Required
	public void setEnvironment(SystemSetupEnvironment environment) {
		this.environment = environment;
	}

	@Required
	public void setCommonI18NService(CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}

	@Required
	public void setImportService(ImportService importService) {
		this.importService = importService;
	}
}
