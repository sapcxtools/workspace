# Code Review – cxdevenvconfig

## Positiv aufgefallen

- Saubere Trennung zwischen Controller und Service.
- Unit-Tests für den verschachtelten JSON-Aufbau sind vorhanden.
- OCC-Spring-Wiring ist minimal und nachvollziehbar.

## Findings

### CEC-001
**Schweregrad:** Hoch  
**Bereich:** API-Security / Datenexposition  
**Betroffene Datei(en):**
- `README.md` (Hinweis auf unauthentifizierten Endpoint)
- `src/me/cxdev/commerce/config/frontend/EnvironmentSpecificFrontendConfigurationService.java`

**Problem:**  
Der Endpoint ist unauthentifiziert und liefert alle `cxdevenvconfig.frontend.*`-Properties ungefiltert aus; es gibt keine Schutzlogik für sensitive Keys.

**Auswirkung:**  
Fehlkonfiguration kann sensible Werte unbeabsichtigt öffentlich machen.

**Konkrete Empfehlung:**  
Allowlist für explizit freizugebende Keys plus harte Blockliste für sensible Muster (`secret`, `token`, `password` etc.) und Fail-Fast bei Verstößen.

### CEC-002
**Schweregrad:** Hoch  
**Bereich:** Konfigurationsaggregation / Robustheit  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/config/frontend/EnvironmentSpecificFrontendConfigurationService.java`

**Problem:**  
Wenn `cxdevenvconfig.frontend` ohne Suffix existiert, führt `substring(...)` zu `StringIndexOutOfBoundsException`.

**Auswirkung:**  
Der öffentliche Konfigurations-Endpoint kann durch eine einzelne fehlerhafte Property auf 500 laufen.

**Konkrete Empfehlung:**  
Nur Keys mit `cxdevenvconfig.frontend.` verarbeiten und vor `substring` strikt validieren (`startsWith(prefix + ".")`).

### CEC-003
**Schweregrad:** Mittel  
**Bereich:** Caching-Korrektheit  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/config/frontend/EnvironmentSpecificFrontendConfigurationService.java`

**Problem:**  
Cache-Invalidierung basiert auf `keys.hashCode()` und ignoriert Value-Änderungen bei gleichem Key-Set.

**Auswirkung:**  
Frontend erhält veraltete Konfiguration trotz geänderter Property-Werte.

**Konkrete Empfehlung:**  
Cache-Key aus sortierten Key+Value-Paaren ableiten oder Caching vereinfachen/entfernen.

### CEC-004
**Schweregrad:** Mittel  
**Bereich:** API-Design / Multi-Site-Korrektheit  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/config/controller/FrontendConfigurationController.java`
- `README.md`

**Problem:**  
Die URL ist baseSite-spezifisch (`/{baseSiteId}/configuration`), aber `baseSiteId` wird nicht genutzt; faktisch globaler Zustand.

**Auswirkung:**  
Irreführender API-Vertrag und potenzielle Fehlannahmen in Multi-Site-Kontexten.

**Konkrete Empfehlung:**  
Entweder Endpoint explizit global gestalten (ohne `baseSiteId`) oder echte site-spezifische Auflösung implementieren.

## Offene Annahmen/Risiken

- CEC-003 ist besonders relevant bei Runtime-Änderungen von Properties. Wenn Änderungen nur per Restart erfolgen, sinkt die operative Relevanz, der Logikfehler bleibt.
