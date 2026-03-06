package sap.commerce.project.facades.extension;

import static org.assertj.core.api.Assertions.assertThat;

import me.cxdev.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;
import org.junit.jupiter.api.Test;
import sap.commerce.project.facades.constants.MyFacadesConstants;

class MyFacadesExtensionTests {
	@Test
	void testConstants() {
		assertThat(MyFacadesConstants.EXTENSIONNAME).isEqualTo("myfacades");
	}

	@Test
	void testExtensionDependencies() {
		InstalledExtensionVerifier.verifier()
				.requires("mycore")
				.verify();
	}
}
