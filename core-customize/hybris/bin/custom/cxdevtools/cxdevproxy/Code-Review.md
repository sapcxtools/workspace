# Code Review – cxdevproxy

## Positiv aufgefallen

- Gute Trennung zwischen Conditions und Interceptors (DSL-Ansatz ist grundsätzlich sauber).
- Korrekte Entlastung des Undertow-IO-Threads via `exchange.dispatch(this)`.
- JWT-Signing nutzt platformnahe Key-Quelle (`jwkSource`) statt separater Schlüsselverwaltung.

## Findings

### CXP-001
**Schweregrad:** Kritisch  
**Bereich:** Dynamic Rule Engine / Hot-Reload  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/proxy/livecycle/UndertowProxyManager.java` (u. a. um `start()`, `applyRules(...)`)

**Problem:**  
Hot-Reload der Groovy-Regeln wirkt zur Laufzeit nicht auf den aktiven Request-Flow. Die finalen Handler-Ketten werden beim Start mit einem Listen-Snapshot aufgebaut; spätere Änderungen in den `AtomicReference`-Listen greifen dadurch nicht.

**Auswirkung:**  
Die beworbene Zero-Downtime-Regelaktualisierung ist funktional gebrochen.

**Konkrete Empfehlung:**  
`applyRules(...)` so umbauen, dass pro Request die aktuelle Handler-Liste gelesen wird, oder Handler bei Reload atomar neu verdrahten.

### CXP-002
**Schweregrad:** Hoch  
**Bereich:** Rule Reload Correctness / Operations  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/proxy/livecycle/UndertowProxyManager.java`
- `resources/cxdevproxy/rulesets/cxdevproxy-frontend-rules.groovy`
- `resources/cxdevproxy/rulesets/cxdevproxy-backend-rules.groovy`

**Problem:**  
`lastModified...` wird nur gesetzt, wenn neu geladene Handler nicht leer sind. Leere Regelsets (`return []`) werden dadurch nie als erfolgreich geladen markiert.

**Auswirkung:**  
Regeln mit leerer Liste werden dauerhaft neu evaluiert (unnötige Last), und das bewusste Deaktivieren aller Interceptors ist nicht zuverlässig.

**Konkrete Empfehlung:**  
`lastModified...` auch bei leerem, aber gültigem Ergebnis aktualisieren und leere Listen als legitimen Zustand behandeln.

### CXP-003
**Schweregrad:** Hoch  
**Bereich:** Security (Transport Trust)  
**Betroffene Datei(en):**
- `src/me/cxdev/commerce/proxy/livecycle/UndertowProxyManager.java`
- `src/me/cxdev/commerce/proxy/ssl/AcceptAllTrustManager.java`

**Problem:**  
HTTPS-Upstreams verwenden Trust-All-SSL (`AcceptAllTrustManager`) ohne Zertifikatsprüfung.

**Auswirkung:**  
Bei nicht rein lokaler Nutzung ist MITM möglich; Header/Tokens können kompromittiert werden.

**Konkrete Empfehlung:**  
Trust-All nur explizit via Dev-Flag aktivieren, ansonsten Default auf Truststore-Validierung und optional Host-Allowlist für „insecure SSL“.

### CXP-004
**Schweregrad:** Mittel  
**Bereich:** Spring Wiring / Proxy-Verhalten  
**Betroffene Datei(en):**
- `resources/cxdevproxy/config/cxdevproxy-interceptor-spring.xml`
- `resources/cxdevproxy/rulesets/cxdevproxy-frontend-rules.groovy`
- `resources/cxdevproxy/rulesets/cxdevproxy-backend-rules.groovy`
- `src/me/cxdev/commerce/proxy/interceptor/ForwardedHeadersInterceptor.java`

**Problem:**  
Kein Standard-Bean für `ForwardedHeadersInterceptor` verdrahtet; Default-Regeln sind leer.

**Auswirkung:**  
Wichtige Header-/Proxy-Interception fehlt out-of-the-box; dokumentiertes Baseline-Verhalten ist inkonsistent.

**Konkrete Empfehlung:**  
`ForwardedHeadersInterceptor` als Standard-Bean verdrahten und in Basisregeln aktivieren, alternativ Doku auf „nicht standardmäßig aktiv“ korrigieren.

### CXP-005
**Schweregrad:** Niedrig  
**Bereich:** i18n / UI-Konsistenz  
**Betroffene Datei(en):**
- `resources/cxdevproxy/ui/proxy/login.html`
- `resources/cxdevproxy/i18n/messages_*.properties`

**Problem:**  
Mehrere in `login.html` verwendete Keys fehlen in den Sprachdateien (u. a. `heading.links`, `msg.loggedin`, `tab.employee`, `user.employee.admin`, `user.employee.cms`, `user.employee.pim`).

**Auswirkung:**  
Unvollständige Lokalisierung, Fallback-Texte und inkonsistente UX.

**Konkrete Empfehlung:**  
Fehlende Keys in allen unterstützten `messages_*.properties` ergänzen und Key-Abdeckung per automatisiertem Check absichern.

## Offene Annahmen/Risiken

- Die Sicherheitsbewertung setzt voraus, dass die Extension nicht strikt localhost-only isoliert betrieben wird. Bei rein lokaler Nutzung sinkt das reale Risiko, die strukturelle Schwachstelle bleibt.
