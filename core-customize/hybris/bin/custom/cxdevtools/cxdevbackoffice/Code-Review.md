# Code Review – cxdevbackoffice

## Positiv aufgefallen

- Klare Feature-Kapselung über Properties (`cxdevbackoffice.i18n.*`, `cxdevbackoffice.sync.relateditems.*`).
- Sinnvolle Erweiterungspunkte über Spring-Bean-Austausch und Map-Merge.
- Unit-Tests für Kernfunktionalität der Hauptkomponenten sind vorhanden.

## Findings

### CXB-001
**Schweregrad:** Mittel  
**Bereich:** Backoffice i18n / Architektur-Robustheit  
**Betroffene Datei(en):**
- `backoffice/src/me/cxdev/commerce/backoffice/i18n/ConfigurableBackofficeLocaleService.java`

**Problem:**  
Die Anonymous-Erkennung basiert auf hart codiertem String (`"anonymous"`) statt auf dem tatsächlich konfigurierten Anonymous-User.

**Auswirkung:**  
Bei abweichender Anonymous-UID greift die Login-Locale-Logik nicht korrekt.

**Konkrete Empfehlung:**  
Vergleich über `userService.getAnonymousUser()` bzw. dessen UID statt Literal.

### CXB-002
**Schweregrad:** Hoch  
**Bereich:** Sync-Visitor / Laufzeitstabilität  
**Betroffene Datei(en):**
- `backoffice/src/me/cxdev/commerce/backoffice/sync/GenericItemSyncRelatedItemsVisitor.java`

**Problem:**  
Konfigurierte Attributnamen werden ungeprüft gelesen; ungültige Qualifier können `AttributeNotSupportedException` auslösen und werden nicht abgefangen.

**Auswirkung:**  
Fehlerhafte Property kann Related-Items-Visit und Folgeprozesse zur Laufzeit abbrechen.

**Konkrete Empfehlung:**  
Qualifier vorab validieren (Typmetadaten) und `AttributeNotSupportedException` gezielt abfangen, loggen, überspringen.

### CXB-003
**Schweregrad:** Mittel  
**Bereich:** Konfigurationsqualität / Robustheit  
**Betroffene Datei(en):**
- `backoffice/src/me/cxdev/commerce/backoffice/sync/GenericItemSyncRelatedItemsVisitor.java`

**Problem:**  
`split(",")` erfolgt ohne `trim()` und ohne Filter leerer Tokens.

**Auswirkung:**  
Whitespace/Trailing-Commas erzeugen ungültige Qualifier und erhöhen Laufzeitfehler-Risiko.

**Konkrete Empfehlung:**  
Tokens trimmen, leere Einträge verwerfen.

### CXB-004
**Schweregrad:** Mittel  
**Bereich:** Testbarkeit / Build-Pipeline  
**Betroffene Datei(en):**
- `resources/cxdevbackoffice/cxdevbackoffice-webtestclasses.xml`
- `backoffice/testsrc/me/cxdev/commerce/backoffice/i18n/ConfigurableBackofficeLocaleServiceTests.java`
- `backoffice/testsrc/me/cxdev/commerce/backoffice/sync/GenericItemSyncRelatedItemsVisitorTests.java`

**Problem:**  
Die Webtest-Klassenliste referenziert FQCNs unter `tools.sapcx...`, während tatsächliche Testklassen unter `me.cxdev...` liegen.

**Auswirkung:**  
Testausführung kann fehlschlagen oder relevante Tests werden nicht ausgeführt.

**Konkrete Empfehlung:**  
FQCNs in der XML auf reale Klassen korrigieren oder Liste entfernen, falls nicht benötigt.

### CXB-005
**Schweregrad:** Niedrig  
**Bereich:** Betriebsdokumentation / Konfigurationssicherheit  
**Betroffene Datei(en):**
- `README.md`

**Problem:**  
Widersprüchliche Property-Konvention (`cxdevbackoffice.sync.relateditems...` vs. `sync.relateditems...`).

**Auswirkung:**  
Hohe Wahrscheinlichkeit für Fehlkonfiguration; Feature bleibt scheinbar inaktiv.

**Konkrete Empfehlung:**  
README auf eine einheitliche Konvention mit Prefix `cxdevbackoffice.` korrigieren und ein korrektes Beispiel ergänzen.
