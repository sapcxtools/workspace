package tools.sapcx.commerce.toolkit.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.validation.services.ValidationService;

import org.springframework.beans.factory.annotation.Required;

import tools.sapcx.commerce.toolkit.constants.ToolkitConstants;

/**
 * Composed {@link SystemSetup} runner that combines several {@link ImpExDataImporter} that follow different semantics.
 *
 * Those are:
 * - elementary data: will be imported first and only once during system initialization
 * - release patch data: will be imported second during essential data processing
 * - essential data: will be imported third during essential data processing
 * - project data: will be imported last during project data processing
 *
 * Multiple project data importers can be defined and attached. Those data importers will be executed in the order
 * they are defined. Typically, there are multiple project data imports such as "initial data", "sample data", and
 * "test data".
 */
@SystemSetup(extension = ToolkitConstants.EXTENSIONNAME)
public class ToolkitSystemSetup {
	private ValidationService validationService;
	private ImpExDataImporter elementaryDataImporter;
	private ImpExDataImporter releasePatchesImporter;
	private ImpExDataImporter essentialDataImporter;
	private List<ImpExDataImporter> projectDataImporters = new ArrayList<>();

	@SystemSetup(process = SystemSetup.Process.ALL, type = SystemSetup.Type.ALL)
	public void reliableSetupPhases(final SystemSetupContext context) {
		Consumer<ImpExDataImporter> importData = importer -> importer.importData(context);

		if (context.getType().isEssential()) {
			if (isSystemInitialization(context)) {
				Optional.ofNullable(elementaryDataImporter).ifPresent(importData);
			}

			Optional.ofNullable(releasePatchesImporter).ifPresent(importData);
			Optional.ofNullable(essentialDataImporter).ifPresent(importData);

			validationService.reloadValidationEngine();
		}

		if (context.getType().isProject()) {
			projectDataImporters.forEach(importData);
		}
	}

	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getSystemSetupParameters() {
		final List<SystemSetupParameter> params = new ArrayList<>();
		for (ImpExDataImporter projectDataImporter : projectDataImporters) {
			params.addAll(projectDataImporter.getSystemSetupParameters());
		}
		return params;
	}

	private boolean isSystemInitialization(SystemSetupContext context) {
		return context.getProcess().isInit() && !context.getProcess().isAll();
	}

	@Required
	public void setValidationService(ValidationService validationService) {
		this.validationService = validationService;
	}

	public void setElementaryDataImporter(ImpExDataImporter elementaryDataImporter) {
		this.elementaryDataImporter = elementaryDataImporter;
	}

	public void setReleasePatchesImporter(ImpExDataImporter releasePatchesImporter) {
		this.releasePatchesImporter = releasePatchesImporter;
	}

	public void setEssentialDataImporter(ImpExDataImporter essentialDataImporter) {
		this.essentialDataImporter = essentialDataImporter;
	}

	public void setProjectDataImporters(List<ImpExDataImporter> projectDataImporters) {
		this.projectDataImporters.clear();
		this.projectDataImporters.addAll(projectDataImporters);
	}
}
