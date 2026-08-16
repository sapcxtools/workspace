# Code Review – cxdevreporting

## Positiv aufgefallen

- Saubere Aufteilung in Such-, Service- und Generator-Komponenten.
- Backoffice-Konfiguration ist strukturiert (Explorer, Editor, Wizards, Search-Kontexte).
- Fehlerpfade in der Suchausführung werden überwiegend kontrolliert behandelt.

## Findings

### CXR-001
**Schweregrad:** Hoch  
**Bereich:** Sicherheit / Datenzugriff  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/reporting/search/FlexibleSearchGenericSearchService.java`

**Problem:**  
Wenn ein Benutzer keine lesbaren CatalogVersions hat, wird auf `getAllCatalogVersions()` eskaliert.

**Auswirkung:**  
Berichte können Daten enthalten, auf die der Benutzer eigentlich keinen Zugriff haben sollte.

**Konkrete Empfehlung:**  
Bei leerer Leseberechtigungsmenge nicht auf alle CatalogVersions zurückfallen; stattdessen kontrolliert leer liefern oder explizit fehlschlagen.

### CXR-002
**Schweregrad:** Hoch  
**Bereich:** Ressourcenmanagement / Betrieb  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/reporting/generator/ReportGeneratorJobPerformable.java`

**Problem:**  
Temporäre ZIP-Dateien werden erstellt, aber nicht zuverlässig gelöscht (`zipFile`-Tracking greift nicht durchgängig).

**Auswirkung:**  
Langfristige Aufblähung des Temp-Verzeichnisses bis hin zu Disk-Pressure.

**Konkrete Empfehlung:**  
Erzeugte ZIP-Dateien im aufrufenden Scope zuverlässig verfolgen und im `finally` immer löschen.

### CXR-003
**Schweregrad:** Mittel  
**Bereich:** Ressourcenmanagement / Backoffice-Download  
**Betroffene Datei(en):**
- `resources/backoffice/cxdevreporting_bof.jar` (`ExecuteReportAction.class`)

**Problem:**  
`FileInputStream` für Download wird ohne try-with-resources verwendet.

**Auswirkung:**  
File-Handle-Leaks und potenziell fehlschlagendes Löschen temporärer Dateien.

**Konkrete Empfehlung:**  
Download-Stream in try-with-resources kapseln.

### CXR-004
**Schweregrad:** Niedrig  
**Bereich:** Lokalisierung / Backoffice UX  
**Betroffene Datei(en):**
- `resources/backoffice/cxdevreporting_bof.jar` (`ExecuteReportAction.class`)
- `resources/backoffice/cxdevreporting_bof.jar` (`cockpitng/widgets/actions/executereport/labels/labels_en.properties`, `labels_de.properties`)

**Problem:**  
Im Code verwendeter Key `executereport.errors.generation` passt nicht zu vorhandenen Keys (`executereport.errors.create`).

**Auswirkung:**  
Im Fehlerfall wird ein nicht aufgelöster i18n-Key angezeigt.

**Konkrete Empfehlung:**  
Key in Code und Sprachdateien vereinheitlichen.

### CXR-005
**Schweregrad:** Mittel  
**Bereich:** Datenmodell-Robustheit / Parameterauflösung  
**Betroffene Datei(en):**
- `resources/cxdevreporting-items.xml`
- `src/me/cxdev/commerce/reporting/report/DefaultReportService.java`

**Problem:**  
Gleichnamige Report-Parameter sind modellseitig möglich; beim Map-Aufbau überschreibt ein Eintrag still den anderen.

**Auswirkung:**  
Reports laufen mit unerwarteten Parametern ohne frühen Validierungsfehler.

**Konkrete Empfehlung:**  
Eindeutigkeit pro Report erzwingen (Validierungsinterceptor oder Modellanpassung) und Duplikate explizit ablehnen.

### CXR-006
**Schweregrad:** Hoch  
**Bereich:** Skalierbarkeit / Speicherverbrauch  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/reporting/search/FlexibleSearchGenericSearchService.java`
- `src/me/cxdev/commerce/reporting/generator/csv/CsvReportGenerator.java`
- `src/me/cxdev/commerce/reporting/generator/excel/ExcelReportGenerator.java`

**Problem:**  
Unlimitierte Suchergebnisse werden vollständig im Speicher materialisiert, bevor geschrieben wird.

**Auswirkung:**  
Hohe Heap-Last bis OOM/GC-Pressure bei großen Ergebnismengen.

**Konkrete Empfehlung:**  
Chunk-/Streaming-Verarbeitung einführen, Result-Limits konfigurieren und technisch erzwingen.

## Offene Annahmen/Risiken

- Teile der Backoffice-Bewertung basieren auf Bytecode-Analyse aus `cxdevreporting_bof.jar`, da zugehöriger Quelltext dort nicht als `.java` vorliegt.
