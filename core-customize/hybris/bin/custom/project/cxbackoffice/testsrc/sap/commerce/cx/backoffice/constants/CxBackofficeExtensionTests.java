package sap.commerce.cx.backoffice.constants;

import org.junit.jupiter.api.Test;

import me.cxdev.commerce.toolkit.testing.verifier.InstalledExtensionVerifier;

class CxBackofficeExtensionTests {
	@Test
	void verify_requiredExtensions_areLoaded() {
		InstalledExtensionVerifier.verifier()
				.requires("backoffice")
				.requires("cxcore")
				.verify();
	}
}
