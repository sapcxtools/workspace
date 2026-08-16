# Code Review – cxdevtoolkit

## Positiv aufgefallen

- Gute modulare Trennung der Spring-Kontexte (models/session/systemsetup/email/fake).
- Profile-basierte Aktivierung der Fake-Mail-Funktion ist klar und kapselt Risiken.
- Das Release-/Project-Importkonzept mit Prefix-Konvention ist grundsätzlich gut skalierbar.

## Findings

### CXT-001
**Schweregrad:** Hoch  
**Bereich:** System Setup / Import-Orchestrierung  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/toolkit/impex/executor/SynchronousImpExDataImportExecutor.java`

**Problem:**  
Importfehler führen nicht zum Abbruch. Bei `importResult.isError()`, Missing Files oder Exceptions wird nur geloggt.

**Auswirkung:**  
Init/Update kann als erfolgreich erscheinen, obwohl Daten unvollständig oder inkonsistent sind.

**Konkrete Empfehlung:**  
Fail-Fast einführen (Exception werfen), optional steuerbar über ein `continue-on-error`-Flag pro Import-Stage.

### CXT-002
**Schweregrad:** Hoch  
**Bereich:** Release-Patch-Lifecycle  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/toolkit/setup/importer/ReleasePatchesImporter.java`
- `src/me/cxdev/commerce/toolkit/setup/SystemSetupEnvironment.java`

**Problem:**  
Release-Patches werden als „processed“ markiert, ohne gesicherten Nachweis eines erfolgreichen Imports.

**Auswirkung:**  
Fehlgeschlagene Patches können dauerhaft übersprungen werden.

**Konkrete Empfehlung:**  
Nur erfolgreich importierte Patch-Keys persistieren (Success-Tracking aus dem Executor zurückgeben).

### CXT-003
**Schweregrad:** Mittel  
**Bereich:** Backoffice-Integration / Betriebsstabilität  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/toolkit/setup/ReliableSystemSetupExecutor.java`

**Problem:**  
`InvocationTargetException` wird zusammen mit „Backoffice nicht vorhanden“ behandelt; echte Laufzeitfehler werden damit maskiert.

**Auswirkung:**  
Troubleshooting wird erschwert; inkonsistente Zustände bleiben ggf. unbemerkt.

**Konkrete Empfehlung:**  
`BeansException` (Bean fehlt) separat behandeln; Invocationsfehler als echte Fehler loggen und abhängig von Kritikalität eskalieren.

### CXT-004
**Schweregrad:** Hoch  
**Bereich:** E-Mail / Security  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/toolkit/email/fake/StoreLocallyHtmlEmailServiceFake.java`

**Problem:**  
Dateiname wird u. a. aus `subject` gebildet und ohne striktes Sanitizing in den Zielpfad übernommen.

**Auswirkung:**  
Pfad-Traversal bzw. Schreiben außerhalb des Zielordners ist bei manipuliertem Betreff möglich.

**Konkrete Empfehlung:**  
Strikte Filename-Allowlist und Sanitizing (keine Separator, kein `..`, Längenlimit), optional sicherer Hash-Fallback.

### CXT-005
**Schweregrad:** Mittel  
**Bereich:** E-Mail / i18n-Vertrag  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/toolkit/email/service/impl/DatabaseEmailTemplateResolverService.java`
- `src/me/cxdev/commerce/toolkit/email/service/EmailTemplateResolverService.java`

**Problem:**  
Interface-Vertrag erwartet „never null“ bzw. Exception bei fehlender Locale-Variante; Implementierung prüft nur fehlendes Item, nicht fehlenden lokalisierten Template-Wert.

**Auswirkung:**  
`null` kann bis in spätere Verarbeitung durchrutschen und dort schwerer nachvollziehbare Fehler erzeugen.

**Konkrete Empfehlung:**  
`template.getTemplate(locale)` auf `null/blank` prüfen und gezielt `TemplateNotFoundException` mit Code+Locale werfen (ggf. definierte Fallback-Locale).

### CXT-006
**Schweregrad:** Mittel  
**Bereich:** E-Mail / i18n-Konfiguration  
**Betroffene Datei(en):**
- `resources/cxdevtoolkit/thymeleafemails-spring.xml`
- `README.md`

**Problem:**  
`ReloadableResourceBundleMessageSource` ist ohne `basename` konfiguriert; laut README soll `messages.properties` für Template-i18n genutzt werden.

**Auswirkung:**  
Erwartete Message-Keys aus dem E-Mail-Bundle werden nicht zuverlässig geladen.

**Konkrete Empfehlung:**  
`basenames` explizit setzen (z. B. classpath-Bundle für E-Mail-Templates) und per Integrationstest absichern.

### CXT-007
**Schweregrad:** Niedrig  
**Bereich:** Konfiguration / Operabilität  
**Betroffene Datei(en):**
- `README.md`
- `resources/cxdevtoolkit/thymeleafemails-fake-spring.xml`
- `project.properties`

**Problem:**  
Property-Key inkonsistent dokumentiert (`...daysToKeepEmails` in README vs. `...daysToKeep` in Konfiguration).

**Auswirkung:**  
Fehlkonfiguration wahrscheinlich; Retention bleibt unerwartet auf Default.

**Konkrete Empfehlung:**  
Doku und Property-Namen harmonisieren; optional beide Keys übergangsweise unterstützen und einen als deprecated markieren.
