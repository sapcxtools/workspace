package sap.commerce.project.backoffice.extension;

import static org.assertj.core.api.Assertions.assertThat;

import me.cxdev.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;
import org.junit.jupiter.api.Test;
import sap.commerce.project.backoffice.constants.MyBackofficeConstants;

class MyBackofficeExtensionTests {
	@Test
	void testConstants() {
		assertThat(MyBackofficeConstants.EXTENSIONNAME).isEqualTo("mybackoffice");
	}

	@Test
	void testExtensionDependencies() {
		InstalledExtensionVerifier.verifier()
				.requires("backoffice")
				.requires("mycore")
				.verify();
	}
}
