package tools.sapcx.commerce.toolkit.testing.verifier;

import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.bootstrap.config.PlatformConfig;
import de.hybris.bootstrap.typesystem.OverridenItemsXml;
import de.hybris.bootstrap.typesystem.YTypeSystemLoader;
import de.hybris.bootstrap.typesystem.YTypeSystemSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.SetUtils.emptyIfNull;
import static org.assertj.core.api.Assertions.assertThat;

public class InstalledExtensionVerifier extends YTypeSystemSource {
    public static InstalledExtensionVerifier verifyExtensions(String platformHome) {
        return new InstalledExtensionVerifier(platformHome);
    }

    private Set<String> requiredExtensions = new HashSet<>();
    private Set<String> forbiddenExtensions = new HashSet<>();

    private InstalledExtensionVerifier(String platformHome) {
        super(
                PlatformConfig.getInstance(ConfigUtil.getSystemConfig(platformHome)),
                new YTypeSystemLoader(true),
                OverridenItemsXml.empty()
        );
    }

    public InstalledExtensionVerifier required(String... requiredExtensions) {
        if (requiredExtensions == null) {
            return this;
        }
        return required(Arrays.stream(requiredExtensions).collect(Collectors.toSet()));
    }

    public InstalledExtensionVerifier required(Set<String> requiredExtensions) {
        this.requiredExtensions.addAll(emptyIfNull(requiredExtensions));
        return this;
    }

    public InstalledExtensionVerifier forbidden(String... forbiddenExtensions) {
        if (forbiddenExtensions == null) {
            return this;
        }
        return forbidden(Arrays.stream(forbiddenExtensions).collect(Collectors.toSet()));
    }

    public InstalledExtensionVerifier forbidden(Set<String> forbiddenExtensions) {
        this.forbiddenExtensions.addAll(emptyIfNull(forbiddenExtensions));
        return this;
    }

    public void verify() {
        Set<String> configuredExtensions = this.getExtensionCfgs().stream()
                .map(ExtensionInfo::getName)
                .collect(Collectors.toSet());

        if (!this.requiredExtensions.isEmpty()) {
            assertThat(configuredExtensions)
                    .describedAs("Required extensions missing in configuration!")
                    .containsAll(this.requiredExtensions);
        }

        if (!this.forbiddenExtensions.isEmpty()) {
            assertThat(configuredExtensions)
                    .describedAs("Forbidden extensions found in configuration!")
                    .doesNotContainAnyElementsOf(this.forbiddenExtensions);
        }
    }
}
