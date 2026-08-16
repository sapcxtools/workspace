package sap.commerce.cx.facades.constants;

import org.junit.jupiter.api.Test;

import me.cxdev.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;

class CxFacadesExtensionTests {
	@Test
	void verify_requiredExtensions_areLoaded() {
		InstalledExtensionVerifier.verifier()
				.requires("cxcore")
				.verify();
	}
}
