# DELTA-Analyse (korrigiert): `custom/project` vs. Referenz `custom/dxp`

## Scope (explizit)
- Vergleich **nur** zwischen:
  - lokal: `core-customize/hybris/bin/custom/project` (`my*`)
  - referenz: `core-customize/hybris/bin/custom/dxp` (`dxp*`)
- `cxdevtools` ist **ausgeschlossen** und bleibt unverändert.

## Kurzfazit
- Dein `my*`-Stack ist aktuell überwiegend Template-/Starter-Stand.
- Der Referenz-`dxp*`-Stack ist funktional deutlich umfangreicher (Backend, OCC, CMS, CPI, Storefront-Features).
- Für dein Ziel (Open-Source Demo, Roboterwelten, ohne DXP-Branding) ist eine strukturierte Übernahme auf `cxdev*` notwendig.

## Strukturdelta auf hoher Ebene
- Lokal vorhanden: `mybackoffice`, `mycore`, `mycpi`, `mydata`, `myfacades`, `myocc`
- Referenz vorhanden: `dxpbackoffice`, `dxpcore`, `dxpcpi`, `dxpdata`, `dxpfacades`, `dxpocc`, `dxpfake`
- Bedeutend: `dxpfake` hat lokal kein Pendant.

## Funktionales Delta (konzeptionell) + Umsetzungs-Prompts

### 1) Extension-Migration auf `cxdev*` (Basis)
**Delta:** `my*` ist kein funktionaler Match zu `dxp*`; zusätzlich fehlt `fake`-Extension.

**Prompt:**
```text
Migriere den Business-Stack aus custom/project von my* auf cxdev* und stelle ihn funktional auf das Niveau von custom/dxp.
Scope:
- Ziel-Extensions: cxdevbackoffice, cxdevcore, cxdevcpi, cxdevdata, cxdevfacades, cxdevocc, cxdevfake
- package names, extension names, spring beans, constants, dto mappings, tests konsistent umbenennen
- cxdevtools unverändert lassen und nicht anfassen
Validierung:
- Alle cxdev*-Extensions builden und booten im bestehenden Setup
```

### 2) CMS-Inhalte vollständig nachziehen
**Delta:** Lokal nur Basis-CMS (Homepage/Dashboard/FAQ/Imprint) vorhanden; Referenz hat vollständige Template-Landschaft (global, checkout, customer, product, search, dashboard, news).

**Prompt:**
```text
Übernehme die vollständige CMS-Template- und Content-Struktur aus dxpdata in cxdevdata.
Wichtig:
- Inhalte auf Roboterwelten und neutrales Wording anpassen
- de/en Mehrsprachigkeit beibehalten
- cxdevtools nicht berühren
Nicht übernehmen:
- keine fremden produktspezifischen Texte unverändert
Validierung:
- Content Pages, Slots, Komponenten und Navigation sind vollständig verfügbar
```

### 3) Dashboard (Backend + OCC + Frontend) ausbauen
**Delta:** Referenz hat komplettes Dashboard-Feature (Config/Widgets/API/UI), lokal nur rudimentäre CMS-Seite.

**Prompt:**
```text
Implementiere das vollständige Dashboard-Feature aus der Referenz im cxdev-Namespace.
Backend:
- Dashboard-Modelle, Services, Facades, Converter/Populator
- OCC-Endpunkte für Config und Widgets
Frontend:
- Widget-Komponenten und Settings/Selector Dialoge
- CMS-Mappings für Dashboard-Komponenten
Validierung:
- Dashboard für User ladbar, editierbar und persistierbar
```

### 4) Service Tickets als „Dynamic Forms“ umsetzen
**Delta:** Referenz enthält vollständige Service-Ticket-/Form-Implementierung; lokal fehlt sie.

**Prompt:**
```text
Übernehme das Service-Ticket-Feature aus der Referenz und benenne es fachlich in "Dynamic Forms" um.
Scope:
- Items/Modelle, Services, Facades, OCC Controller/DTOs, CMS-Komponenten, Frontend
- Funktionen: Formdefinitionen laden, Requests erstellen, Übersicht/Detail/Count, Mail-Versand
- Solr-Suche für Requests integrieren
Migrationsregel:
- konsistentes Wording "Dynamic Forms", technische Rückwärtskompatibilität nur falls zwingend
```

### 5) Solr-Erweiterungen übernehmen
**Delta:** Referenz hat erweiterte Facetten-/Range-/Stats-/RemoveQuery-Logik in Core/Facades; lokal fehlt das.

**Prompt:**
```text
Übernehme die Solr-Erweiterungen aus dxpcore/dxpfacades nach cxdevcore/cxdevfacades.
Scope:
- zusätzliche Query-/Response-Populatoren (DisplayType, Stats, Range, RemoveQuery, Type)
- SearchState/Codec-Erweiterungen
- benötigte Spring- und Impex-Konfigurationen
Validierung:
- Produktsuche bleibt stabil
- Dynamic-Forms-Suche mit Facetten funktioniert
```

### 6) CPI-Inbound-Schnittstellen vollständig nachziehen
**Delta:** Referenz hat Integrationsobjekte + Hook-Executor + Inbound-Konfiguration; lokal `mycpi` ist nur Basis.

**Prompt:**
```text
Übernehme die CPI-Inbound-Struktur aus dxpcpi in cxdevcpi.
Scope:
- IntegrationUsers und Inbound-Definitionen für Product/Variant/Feature/Price/Discount/Stock/B2BCustomer/Order
- PersistenceHookExecutor inkl. konfigurierbarer pre/post-persist hooks
- cloud/property wiring für inbound monitoring und hook chains
Nicht tun:
- keine Produkt- oder Klassifikationsdaten importieren
Validierung:
- Inbound-Struktur technisch vorhanden und hook execution aktiv
```

### 7) Composable Storefront-Funktionalität nachziehen
**Delta:** Referenz hat umfassende Storefront-Features; lokal fehlt praktisch die zugehörige App-Funktionalität.

**Prompt:**
```text
Baue die cxdev-Storefront-Funktionsmodule analog zur Referenz auf.
Scope:
- OCC endpoint config (dashboard/forms/news/search facets etc.)
- Feature-Module für Dashboard, Dynamic Forms, Facets, News, Saved Cart/Order-bezogene Erweiterungen
- CMS-Komponenten-Mappings
Validierung:
- Build erfolgreich
- Seiten aus CMS rendern korrekt mit cxdevocc APIs
```

### 7a) Physische Frontend-Überführung: `js-storefront/dxp` -> `js-storefront/main`
**Delta:** Lokal existiert unter `js-storefront` noch kein fachlicher Frontend-Bereich; Referenz liegt als App in `js-storefront/dxp`.

**Prompt:**
```text
Überführe die Referenz-Storefront aus js-storefront/dxp in das lokale js-storefront/main.
Vorgehen:
- Struktur und Quellcode aus Referenz übernehmen
- DXP-Bezeichner konsistent auf cxdev umstellen (App-Name, Pfade, Module, i18n keys, Branding-Texte)
- Endpoint-Konfiguration von dxpocc auf cxdevocc anpassen
- Build-/Tooling-Dateien im Zielordner main konsistent halten (angular.json, package.json, tsconfig, manifest-Einträge)
- cxdevtools nicht verändern
Validierung:
- npm install + Build laufen in js-storefront/main
- CMS-Komponentenzuordnung funktioniert gegen die cxdev OCC-Endpunkte
Hinweis:
- Nicht blind 1:1 kopieren; Bezeichner und Konfigurationspfade müssen sauber auf main/cxdev angepasst werden.
```

### 8) Backoffice-Integration für neue Typen/Komponenten
**Delta:** Referenz hat zusätzliche Typ-/Label-/Config-Integration für die Features; lokal ist das nur Basis.

**Prompt:**
```text
Übernehme Backoffice-relevante Erweiterungen für Dashboard und Dynamic Forms in cxdevbackoffice/cxdevcore.
Scope:
- Labels, Typen, Editor-Konfigurationen, Validierungsregeln
- CMS-Komponenten administrierbar machen
Validierung:
- Redakteure/Fachanwender können neue Komponenten und Formdefinitionen pflegen
```

### 9) Produkt- und Klassifikationsdaten: nur Struktur-Gap, keine Datenübernahme
**Delta:** Referenz enthält umfangreiche Produkt-/Klassifikationsdatensätze; diese sollen nicht übernommen werden.

**Prompt:**
```text
Erstelle eine reine Strukturanalyse der Produkt-/Klassifikationsabhängigkeiten aus custom/dxp.
Liefere:
- notwendige Klassifikationsklassen/-attribute/-units für Features (Suche/Facetten/Dynamic Forms)
- Mapping-Vorschlag auf Roboterwelten-Domäne
Nicht tun:
- keine Übernahme von Referenz-Datensätzen (Produkt oder Klassifikation)
```

## Hinweise für die Umsetzung
- Bestehende Roboterwelten-Daten in `mydata` als Domänenbasis erhalten.
- Übernahmen primär auf Feature-Logik, APIs, CMS-Struktur und Konfiguration fokussieren.
- `cxdevtools` explizit unangetastet lassen.
