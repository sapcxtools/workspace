package tools.sapcx.commerce.template.config;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import tools.sapcx.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;

@UnitTest
public class ExtensionConfigurationTests {
    @Test
    public void extensionConfiguration() {
        InstalledExtensionVerifier.verifier()
                .requires("sapcxtemplate")
                .requires("sapcommercetoolkit")
                .verify();
    }
}
