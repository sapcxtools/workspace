# SAP Commerce Cloud & S/4HANA B2B Demo-Dataset

## Ziel
Aufbau eines realistischen Demo-Datensets für ein SAP Commerce Cloud Kundenportal für industrielle Service-Roboter. Das Portal wird über die SAP BTP Integration Suite (CPI) asynchron mit einem simulierten SAP S/4 ERP versorgt. 

Kunden können:
- Roboter konfigurieren und kaufen
- Ersatzteile bestellen
- Später ihre installierten Maschinen (Installed Base) einsehen

## 1. Wichtige Architekturprinzipien

### Trennung von Produkt und realer Maschine
* **Produkt:** Bauplan / konfigurierbarer Robotertyp (KMAT Material)
* **Equipment:** Physische Maschine mit Seriennummer
* **Installed Base:** Maschinen eines Kunden

*Beispiel:*
* **Product:** R200_INSPECTOR
* **Equipment:** Serial SN-R200-1256373, Material R200_INSPECTOR, Customer Future Manufacturing Group

### Datenquellen-Verantwortung
* **ERP liefert (via IDoc/SOAP):** Materialnummern, technische Daten, Klassifikation, Stücklisten, Preise, Kunden.
* **Commerce/PIM pflegt (via ImpEx):** Produktbeschreibungen, Bilder, Kategorien, Marketingtexte, CMS-Strukturen.
* **Sprachen & Labels:** Redaktionelle Inhalte (Texte, HTML) werden in den ImpEx-Dateien in Englisch und Deutsch gepflegt (`[lang=en]`, `[lang=de]`). Alle statischen UI-Labels werden ausschließlich über Message Bundles in der Composable Storefront verwaltet, um die Datenbank sauber zu halten.

## 2. SAP Integrationsartefakte (Simuliert als XML via CPI)
* **Materialstamm:** MATMAS
* **Klassifikation:** CLSMAS, CLFMAS
* **Stücklisten:** BOMMAT
* **Preise:** PRICAT oder Condition IDocs
* **Kunden:** BusinessPartner SOAP API oder DEBMAS
* **Installed Base:** EQUI (Equipment)

## 3. Demo-Produktwelt & Hierarchie
Drei Robotermodelle in einer 2-Ebenen-Hierarchie:
* **Industrial Robots**
    * **Autonomous Logistics Robots:** R100 Courier
    * **Inspection Robots:** R200 Inspector
    * **Heavy Duty Robots:** R500 HeavyLift

### Ersatzteile (Modulare Baugruppen)
* **Power Systems:** Battery Packs (z.B. RBAT-15 Battery Pack 15kWh)
* **Sensor Systems:** Vision, Distance, Radar (z.B. RSEN-LID Lidar Sensor)
* **Manipulator Systems:** Robot Arms (z.B. RARM-PREC Precision Manipulator Arm)
* **Drive Systems:** Motors (z.B. RMOT-M Drive Motor Medium)
* **Control Systems:** Controllers (z.B. RCTRL-AI1 AI Control Board)

### Beispiel BOM (R200 Inspector)
1 Chassis Frame, 4 Drive Motor M, 4 Omni Wheels, 1 Battery Pack 15kWh, 1 AI Control Board, 1 Lidar Sensor, 1 Visual Camera Sensor, 1 Precision Manipulator Arm.

## 4. Preisstrategie
* **Roboter:** UVP / Listenpreise (z.B. R200 Inspector = 72.500 €).
* **Ersatzteile:** Standardlistenpreise + zwei kundenspezifische Preislisten (Price List A: 10–15% Rabatt, Price List B: alternative Rabatte).

## 5. B2B Kundenstruktur
* **Kunde 1 (Solo Robotics Services):** Ein-Mann-Betrieb. User (Owner) hat alle Rechte, Standardpreise.
* **Kunde 2 (Future Manufacturing Group):** Industriekunde mit zwei Werken. Rabatt-Preisliste (A oder B).
    * *Rollen:* Managing Director (Vollzugriff), OrgAdmin (Benutzerverwaltung), Central Buyer (Genehmiger), Production Lead (Werk 1 & 2, Ersteller von Warenkörben).

## 6. Composable Storefront & CMS Architektur

### Basis: Katalogstruktur
Bevor Templates, Seiten oder Produkte angelegt werden können, muss zwingend das Fundament der Kataloge initialisiert werden. Dies umfasst:
* **ContentCatalog:** Für alle CMS-Inhalte, Templates und Seiten. Redaktionelle Inhalte werden hierbei direkt zweisprachig (Englisch und Deutsch) in den ImpEx-Dateien gepflegt. Statische UI-Beschriftungen (Labels) verbleiben schlank in den Message Bundles der Storefront.
* **ProductCatalog:** Für die aus dem ERP synchronisierten Stamm- und Produktdaten.
* **ClassificationSystem:** Für die roboterspezifischen Merkmale (Klassen und Attribute).
Für alle Kataloge werden direkt die Staged- und Online-Versionen sowie die zugehörigen Synchronisationsjobs (`CatalogVersionSyncJob`) angelegt.

### Templates & Seiten-Mapping
* **Global (Alle Templates):** Konsistenter Header (Logo, Nav, Sprache, Währung, Theme, Mini-Cart, Account-Menü) & Footer (Copyright, Mini-Nav, Social Media, Impressum, Kontakt).
* **Homepage (Public):** `LandingPageTemplate`. Visual oben, darunter CMSParagraphComponent.
* **Dashboard (Logged-in):** `DashboardPageTemplate`. 6x4 Grid System über CMSFlexComponent für dynamische Kacheln.
* **Content Pages (FAQ, Impressum):** `SimpleContentTemplate` mit CMSParagraphComponent.
* **Standard Spartacus/B2B Pages:** Login, Forgot Password, Contact, Search Results, Product List/Grid, Product Detail, Cart (Empty/Filled inkl. Quick Order), Quick Order Page, One-Page Checkout, Order Confirmation, Order History, Order Details, Invoice List, My Account (Profile, Password, Address Book, B2B Unit/User Management).

## 7. Struktur des Demo-Datasets

```text
mydata/
├── commerce/                 (context.impex, basestore.impex, product-catalog-setup.impex, classification-setup.impex) 
├── erp/
│   ├── csvs/                 (Countries.csv, Currencies.csv, Languages.csv, ...)
│   ├── idocs/
│   │   ├── materials/        (MATMAS_robot_R200.xml, ...)
│   │   ├── classification/   (CLSMAS_robot_classes.xml, CLFMAS_robot_R200.xml)
│   │   ├── pricing/          (PRICAT_robot_prices.xml)
│   │   ├── customers/        (BusinessPartner_customers.xml)
│   │   ├── bom/              (BOMMAT_R200.xml)
│   │   └── equipment/        (EQUI_robot_SN1256373.xml)
└── website/                  (website.impex, content-catalog-setup.impex)
    └── templates/
        ├── common/           (shared-content.impex)
        ├── homepage/         (homepage-template.impex, homepage-content.impex)
        ├── dashboard/        (dashboard-template.impex, dashboard-content.impex)
        └── contentpage/      (contentpage-template.impex, faq-content.impex)
```

## 8. Projekt-Phasen (Iteratives Vorgehen)

* **Schritt 0: Commerce Basis-Setup.** Erstellung der ImpEx-Dateien für Context (Currencies, Languages, Countries) und BaseStore.
* **Schritt 1: Katalog-Fundament.** Anlage von `ContentCatalog`, `ProductCatalog` und `ClassificationSystem` inklusive Versionen (Staged/Online) und Catalog-Sync-Jobs.
* **Schritt 2: CMS Basis-Setup.** Erstellung der ImpEx-Dateien für Header, Footer, Homepage (`LandingPageTemplate`) und B2B Dashboard (`DashboardPageTemplate`).
* **Schritt 3: Produktdefinition.** Einen Roboter vollständig definieren (R200 Inspector).
* **Schritt 4: ERP-Rohdaten generieren.** Realistische IDoc XML Beispiele (MATMAS, CLSMAS, CLFMAS, BOMMAT, Pricing) für den R200 erstellen.
* **Schritt 5: Ersatzteile.** Hierarchie und Preise für Ersatzteile modellieren.
* **Schritt 6: Portfolio-Erweiterung.** Weitere Roboter ableiten (R100 Courier, R500 HeavyLift).
* **Schritt 7: B2B Organisation.** Kundenorganisation und User modellieren.
* **Schritt 8: Installed Base.** Equipment und Instanzen ergänzen.
* **Schritt 9: Commerce Enrichment.** Anreicherung der ERP-Daten durch Commerce-spezifische ImpEx (Marketingtexte, Bilder), die nach den CPI-Importen eingespielt werden.

## 9. Vorbedingungen: Globale ImpEx Makros

Um die ImpEx-Skripte umgebungsunabhängig (Local, Dev, Stage, Prod) zu halten, verwenden wir den `ConfigPropertyImportProcessor`. Dieser erlaubt es, IDs, Namen und Sprachen dynamisch aus den `local.properties` (oder dem Configuration Manifest) zu laden. 

Jedes ImpEx-Skript in diesem Projekt muss zwingend mit folgendem Header beginnen (nicht verwendete Makros oder Importe dürfen weggelassen werden):

```impex
# -----------------------------------------------------------------------
# BeanShell imports & Locale setup
# -----------------------------------------------------------------------
"#% impex.setLocale(Locale.ENGLISH);"
"#% import org.apache.commons.lang3.StringUtils;"
"#% import org.apache.commons.io.FilenameUtils;"
"#% import me.cxdev.commerce.toolkit.impex.ImpExLineFilters;"
"#% import sap.commerce.project.data.constants.MyDataConstants;"

# -----------------------------------------------------------------------
# DEFAULT MACROS
# -----------------------------------------------------------------------
UPDATE GenericItem[processor = de.hybris.platform.commerceservices.impex.impl.ConfigPropertyImportProcessor]; pk[unique = true]
$primaryCurrency = $config-project.mydata.import.defaults.currency.primary
$primaryLang = $config-project.mydata.import.defaults.languages.primary
$optionalLang1 = $config-project.mydata.import.defaults.languages.optional1
$optionalLang2 = $config-project.mydata.import.defaults.languages.optional2
$optionalLang3 = $config-project.mydata.import.defaults.languages.optional3
$optionalLang4 = $config-project.mydata.import.defaults.languages.optional4
$optionalLang5 = $config-project.mydata.import.defaults.languages.optional5
$optionalLang6 = $config-project.mydata.import.defaults.languages.optional6
$optionalLang7 = $config-project.mydata.import.defaults.languages.optional7
$optionalLang8 = $config-project.mydata.import.defaults.languages.optional8
$optionalLang9 = $config-project.mydata.import.defaults.languages.optional9
$siteId = $config-project.mydata.import.defaults.site.id
$siteName = $config-project.mydata.import.defaults.site.name
$storeId = $config-project.mydata.import.defaults.store.id
$storeName = $config-project.mydata.import.defaults.store.name
$vendorId = $config-project.mydata.import.defaults.vendor.id
$vendorName = $config-project.mydata.import.defaults.vendor.name
$warehouseId = $config-project.mydata.import.defaults.warehouse.id
$warehouseName = $config-project.mydata.import.defaults.warehouse.name
$requiresAuthentication = $config-project.mydata.import.defaults.requiresauthentication
$enableRegistration = $config-project.mydata.import.defaults.enableregistration
$useNetValues = $config-project.mydata.import.defaults.usenetvalues
$productCatalogId = $config-project.mydata.import.defaults.catalog.product.id
$productCatalogName = $config-project.mydata.import.defaults.catalog.product.name
$productCatalogEditVersion = $config-project.mydata.import.defaults.catalog.product.versions.edit
$productCatalogLiveVersion = $config-project.mydata.import.defaults.catalog.product.versions.live
$productCV = catalogVersion(CatalogVersion.catalog(Catalog.id[default = $productCatalogId]), CatalogVersion.version[default = $productCatalogEditVersion])[unique = true, default = $productCatalogId:$productCatalogEditVersion]
$classificationCatalogId = $config-project.mydata.import.defaults.catalog.classification.id
$classificationCatalogName = $config-project.mydata.import.defaults.catalog.classification.name
$classificationCatalogLiveVersion = $config-project.mydata.import.defaults.catalog.classification.versions.live
$classCatalogVersion = catalogversion(catalog(id[default = '$classificationCatalog']), version[default = '$classificationCatalogLiveVersion'])[unique = true, default = '$classificationCatalog:$classificationCatalogLiveVersion']
$classSystemVersion = systemVersion(catalog(id[default = '$classificationCatalog']), version[default = '$classificationCatalogLiveVersion'])[unique = true, default = '$classificationCatalog:$classificationCatalogLiveVersion']
$class = classificationClass(ClassificationClass.code, $classCatalogVersion)[unique = true]
$contentCatalogId = $config-project.mydata.import.defaults.catalog.content.id
$contentCatalogName = $config-project.mydata.import.defaults.catalog.content.name
$contentCatalogEditVersion = $config-project.mydata.import.defaults.catalog.content.versions.edit
$contentCatalogLiveVersion = $config-project.mydata.import.defaults.catalog.content.versions.live
$contentCV = catalogVersion(CatalogVersion.catalog(Catalog.id[default = $contentCatalogId]), CatalogVersion.version[default = $contentCatalogEditVersion])[unique = true, default = $contentCatalogId:$contentCatalogEditVersion]
$mediaResourcePath = jar:sap.commerce.project.data.constants.MyDataConstants&/mydata/assets/
```