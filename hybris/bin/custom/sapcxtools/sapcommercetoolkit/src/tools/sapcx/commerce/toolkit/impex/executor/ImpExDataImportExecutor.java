package tools.sapcx.commerce.toolkit.impex.executor;

import de.hybris.platform.core.initialization.SystemSetupContext;

import org.apache.log4j.Logger;

@FunctionalInterface
public interface ImpExDataImportExecutor {
	void importImpexFile(SystemSetupContext context, String file, String fileEncoding);

	default void importImpexFile(SystemSetupContext context, String file) {
		importImpexFile(context, file, "UTF-8");
	}

	default ImpExDataImporterLogger getLogger() {
		return new ImpExDataImporterLogger(Logger.getLogger(getClass()));
	}
}
