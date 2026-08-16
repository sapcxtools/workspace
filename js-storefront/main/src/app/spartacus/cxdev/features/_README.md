# Features


## Komponentengenerierung
`ng g c cxdev-[COMPONENT_NAME]`

In diesem Ordner befinden sich alles Features. Das kleinste Feature ist eine einfache CMS Komponente.
Das bedeutet jede Komponente die ein CMS Gegenstück im Backend besitzt wird hier als ein Feature erstellt.
Einzelne CMS Komponenten, ohne weitere Abhängigkeiten werden als Standalone Components erstellt und benötigen kein weiteres Module
und müssen auch nicht im `_cxdev-features.module.ts` importiert werden.

Die Ordnerstruktur für eine einfache CMS Komponente sollte folgendermaßen aufgebaut sein:

```
|-- features
    ...
    |-- cxdev-cms-feature-1
        |-- cxdev-cms-feature-1.components.ts
        |-- cxdev-csm-feature-1.html.ts
        |__ cxdev-cms-feature.service.ts (falls ein separater Komponentenservice benötigt wird)
    ...     
```

## Modulegenerierung
`ng g m cxdev-[MODULE_NAME]`

Features können aber auch aus mehreren Komponenten, Services etc. bestehen. Sollte das der Fall sein, sollten die einzelnen Bestandteile
in einem Module gesammelt und anschließend im `_cxdev-features.module.ts` importiert werden.

Die Ordnerstruktur sollte bei so einem komplexeren Module folgendermaßen aufgebaut sein:

```
|-- features
    ...
    |__ cxdev-feature-1-module
        |-- components
            |-- cxdev-feature-1-part-1
                |-- cxdev-feature-1-part-1.components.ts
                |__ cxdev-feature-1-part-1.html.ts
            |__ cxdev-feature-1-part-2
                |-- cxdev-feature-1-part-2.components.ts
                |__ cxdev-feature-1-part-2.html.ts
        |-- services
            |_ cxdev-feature-1.service.ts
        ...
        |__ cxdev-feature-1.module.ts
            
```