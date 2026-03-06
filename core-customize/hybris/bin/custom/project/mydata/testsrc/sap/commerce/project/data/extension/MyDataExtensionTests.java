package sap.commerce.project.data.extension;

import static org.assertj.core.api.Assertions.assertThat;

import me.cxdev.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;
import org.junit.jupiter.api.Test;
import sap.commerce.project.data.constants.MyDataConstants;

class MyDataExtensionTests {
	@Test
	void testConstants() {
		assertThat(MyDataConstants.EXTENSIONNAME).isEqualTo("mydata");
	}

	@Test
	void testExtensionDependencies() {
		InstalledExtensionVerifier.verifier()
				.requires("mycore")
				.verify();
	}
}
