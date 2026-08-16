package sap.commerce.cx.data.setup;

import java.util.List;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;

import me.cxdev.commerce.toolkit.setup.ReliableSystemSetupExecutor;

public class CxSystemSetup {
	private final ReliableSystemSetupExecutor reliableSystemSetupExecutor;

	public CxSystemSetup(ReliableSystemSetupExecutor reliableSystemSetupExecutor) {
		this.reliableSystemSetupExecutor = reliableSystemSetupExecutor;
	}

	@SystemSetup(process = SystemSetup.Process.ALL, type = SystemSetup.Type.ALL)
	public void reliableSetupPhases(final SystemSetupContext context) {
		reliableSystemSetupExecutor.reliableSetupPhases(context);
	}

	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getSystemSetupParameters() {
		return reliableSystemSetupExecutor.getSystemSetupParameters();
	}
}
