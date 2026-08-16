# CxdevFormComponent

Die `CxdevFormComponent` dient als Abstrakte Komponente um Formulare darzustellen.
Anhand einer ID wird eine Formstruktur geladen, welche über den `CXDEV_FORM_DATA` InjectionToken provided werden kann.
Zukünftig könnten die Formstrukturen auch über eine API aus dem Backend oder anderen System geladen werden.

Als Anschauungsbeispiel für die Umsetzung kann die `CxdevContactFormComponent` dienen.

---

**2026-04-01**
Die CxdevFormComponent wurde initial für die CxdevContactComponent angelegt und erstmal nur für die dafür benötigten
Felder (Text, Textarea) und Funktionen getestet. Andere Felder und Funktionen sollten bei gegebener Zeit weiter ausgebaut und getestet werden. Ein Todo wäre auf jeden Fall noch beim submit nicht intern zu submitten, sondern den FormValue zu emitten, damit die ParentComponent damit weiterarbeiten kann. Dadurch können komplexere Abläufe abgebildet werden und die CxdevFormComponent ist wirklich nur für das sammeln von Informationen da. Die Verarbeitung findet dann außerhalb statt. Eventuell sollte das auch zur generellen Funktionsweise der Komponente umgebaut werden.

---
