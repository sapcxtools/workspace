# CMS Impex Konfiguration

## Überblick

Die CMS-Konfiguration des Projekts ist nach Templates und den dazugehörigen Seiteninhalten strukturiert.

Jedes Template besteht grundsätzlich aus zwei Ebenen:

1. **Template-Definition**

    * Definition des `PageTemplate`
    * Definition der verfügbaren `ContentSlotNames`
    * Zuordnung der Standard-ContentSlots über `ContentSlotForTemplate`

2. **Page Content**

    * Anlegen der eigentlichen `ContentPage`
    * Zuordnung von ContentSlots zur Seite
    * Definition der verwendeten CMS-Komponenten
    * Sprachspezifische Übersetzungen

---

# Verzeichnisstruktur

```text
cms/
└── templates/
    ├── _global/
    │   ├── 52-contentSlots.impex
    │   ├── 53-restrictions.impex
    │   ├── 54-anonymous-nav.impex
    │   ├── 54-logged-in-nav.impex
    │   ├── 55-nav-category.impex
    │   ├── 56-nav-myaccount.impex
    │   ├── 58-nav-content.impex
    │   ├── 59-nav-footer.impex
    │   └── 60-components.impex
    │
    ├── checkout/
    ├── content/
    ├── customer/
    ├── dashboard/
    ├── product/
    └── search/
```

---

# Globale Konfiguration (_global)

Die Dateien im Verzeichnis `_global` enthalten CMS-Konfigurationen, die templateübergreifend verwendet werden.

## contentSlots.impex

Definiert globale ContentSlots.

Beispiele:

* Header
* Footer
* Navigation
* Search
* Logo

---

## restrictions.impex

Definiert CMS-Restrictions, beispielsweise:

* Login erforderlich
* Benutzerrollen
* Katalogeinschränkungen

---

## Navigation

### anonymous-nav.impex

Navigation für nicht angemeldete Benutzer.

### logged-in-nav.impex

Navigation für angemeldete Benutzer.

### nav-category.impex

Kategorienavigation.

### nav-myaccount.impex

Navigation im Bereich „Mein Konto“.

### nav-content.impex

Content-Navigation.

### nav-footer.impex

Footer-Navigation.

---

## components.impex

Globale CMS-Komponenten.

Beispiele:

* Banner
* Navigation Components
* Footer Components
* Shared Components

---

# Template-Struktur

Jede Seite besitzt typischerweise folgende Dateien:

```text
<PageTemplate>/
├── _<PageTemplate>.impex
├── <page>-content.impex
├── <page>-content_de.impex
└── <page>-content_en.impex
```

---

## _PageTemplate.impex

Definiert die technische Struktur der Seite.

### Aufgaben

#### PageTemplate anlegen

```impex
INSERT_UPDATE PageTemplate
```

Erzeugt das CMS-Template.

---

#### ContentSlotNames definieren

```impex
INSERT_UPDATE ContentSlotName
```

Definiert die verfügbaren Bereiche innerhalb des Templates.

Beispiele:

* Header
* Footer
* NavigationBar
* CenterContent
* BottomContent

---

#### Standard-Slots zuordnen

```impex
INSERT_UPDATE ContentSlotForTemplate
```

Verknüpft die definierten Positionen mit konkreten ContentSlots.

---

# Dashboard Beispiel

## Verzeichnis

```text
dashboard/
├── _DashboardPageTemplate.impex
├── dashboard-content.impex
├── dashboard-content_de.impex
└── dashboard-content_en.impex
```

---

# Dashboard Template

Datei:

```text
_DashboardPageTemplate.impex
```

## PageTemplate

Definiert das Template:

```impex
$templateId = DashboardPageTemplate
$templateName = Dashboard Page Template
```

Erzeugt:

```impex
INSERT_UPDATE PageTemplate
```

---

## Verfügbare Slots

Folgende Bereiche werden definiert:

| Slot                   | Gruppe           |
| ---------------------- | ---------------- |
| SiteLogo               | logo             |
| HeaderLinks            | headerlinks      |
| SearchBox              | searchbox        |
| MiniCart               | minicart         |
| NavigationBar          | navigation       |
| TopContent             | wide             |
| Footer                 | footer           |
| TopHeaderSlot          | wide             |
| BottomHeaderSlot       | wide             |
| CenterLeftContentSlot  | narrow           |
| CenterRightContentSlot | narrow           |
| BottomContentSlot      | wide             |
| PlaceholderContentSlot | -                |
| SiteContext            | CMS Site Context |
| SiteLinks              | CMSLinkComponent |

Definition erfolgt über:

```impex
INSERT_UPDATE ContentSlotName
```

---

## Template-Slot-Zuordnungen

Die Slots werden anschließend mit den globalen ContentSlots verbunden:

```impex
INSERT_UPDATE ContentSlotForTemplate
```

Beispiele:

| Position      | Slot              |
| ------------- | ----------------- |
| SiteLogo      | SiteLogoSlot      |
| NavigationBar | NavigationBarSlot |
| Footer        | FooterSlot        |
| SearchBox     | SearchBoxSlot     |
| MiniCart      | MiniCartSlot      |

---

# Dashboard Seite

Datei:

```text
dashboard-content.impex
```

---

## ContentPage

Anlegen der eigentlichen CMS-Seite:

```impex
INSERT_UPDATE ContentPage
```

Parameter:

| Feld           | Wert                  |
| -------------- | --------------------- |
| uid            | dashboardPage         |
| name           | Dashboard Page        |
| label          | /                     |
| masterTemplate | DashboardPageTemplate |
| homepage       | true                  |

---

## Seiten-Slots

Zuordnung eines ContentSlots zur Seite:

```impex
INSERT_UPDATE ContentSlotForPage
```

Beispiel:

| Position      | ContentSlot                 |
| ------------- | --------------------------- |
| MiddleContent | MiddleContent-dashboardPage |

---

# Medien

Dashboard verwendet eigene Icons.

Definition über:

```impex
INSERT_UPDATE Media
```

Importierte Medien:

| Code                           |
| ------------------------------ |
| /media/cx_icon_my_users.png   |
| /media/cx_icon_my_account.png |
| /media/cx_icon_my_company.png |

---

# Dashboard Komponenten

## Dashboard Teaser

```impex
INSERT_UPDATE CxDashboardTeaserComponent
```

Komponenten:

| UID                               | Ziel                       |
| --------------------------------- | -------------------------- |
| MyUsersDashboardTeaserComponent   | /organization/users        |
| MyAccountDashboardTeaserComponent | /my-account/update-profile |
| MyCompanyDashboardTeaserComponent | /organization              |

---

## Dashboard Hauptkomponente

```impex
INSERT_UPDATE CMSFlexComponent
```

```text
DashboardComponent
```

---

## Service Requests

```impex
INSERT_UPDATE CMSFlexComponent
```

```text
CxDashboardServiceRequestsComponent
```

---

## Produktliste

```impex
INSERT_UPDATE CxDashboardProductListComponent
```

Konfiguration:

| Eigenschaft  | Wert                                    |
| ------------ | --------------------------------------- |
| productCount | 4                                       |
| category     | 99999999                                |
| actions      | ListAddToCartAction,ListOrderFormAction |

---

# Slot-Belegung

Die Komponenten werden dem Dashboard-Slot zugeordnet:

```impex
INSERT_UPDATE ContentSlot
```

Beispiel:

```text
MiddleContent-dashboardPage
    └── DashboardComponent
```

---

# Sprachdateien

## dashboard-content_de.impex

Enthält deutsche Übersetzungen.

### Seitentitel

```impex
UPDATE ContentPage
```

```text
Dashboard
```

---

### Dashboard Teaser Texte

```impex
UPDATE CxDashboardTeaserComponent
```

Beispiele:

| Komponente                        | Linkname         |
| --------------------------------- | ---------------- |
| MyUsersDashboardTeaserComponent   | Meine Benutzer   |
| MyAccountDashboardTeaserComponent | Mein Account     |
| MyCompanyDashboardTeaserComponent | Mein Unternehmen |

Zusätzlich werden Headlines und Beschreibungen gepflegt.

---

### Produktliste

```impex
UPDATE CxDashboardProductListComponent
```

```text
Meine Geräte anzeigen
```

---

# Namenskonventionen

## Template-Dateien

```text
_<TemplateName>.impex
```

Beispiel:

```text
_DashboardPageTemplate.impex
```

---

## Content-Dateien

```text
<page>-content.impex
```

Beispiel:

```text
dashboard-content.impex
```

---

## Sprachdateien

```text
<page>-content_de.impex
<page>-content_en.impex
```

---

## Komponenten

Empfohlene Benennung:

```text
<Page><Typ>Component
```

Beispiele:

```text
DashboardComponent
MyUsersDashboardTeaserComponent
MyEquipmentDashboardComponent
```

---

# Erweiterung einer neuen CMS-Seite

Für eine neue Seite sollten mindestens folgende Dateien angelegt werden:

```text
NewPageTemplate/
├── _NewPageTemplate.impex
├── newpage-content.impex
├── newpage-content_de.impex
└── newpage-content_en.impex
```

Vorgehensweise:

1. Neues `PageTemplate` anlegen.
2. Benötigte `ContentSlotNames` definieren.
3. `ContentSlotForTemplate` konfigurieren.
4. `ContentPage` anlegen.
5. Komponenten definieren.
6. Komponenten einem ContentSlot zuordnen.
7. Sprachdateien pflegen.
