# Lawliet Web

Vaadin Flow 25 + Spring Boot 4 (Java 21) website/dashboard for the Lawliet Discord bot. Server-side Java UI; there is no hand-written frontend framework code. There are no tests.

## Commands
- Dev run: `./mvnw` (default goal is `spring-boot:run`, port `${PORT:8080}`, auto-launches a browser).
- Production build (what the Dockerfile does): `./mvnw clean package -DskipTests -Pproduction`. Container serves on 8443.
- Compile check: `./mvnw -q compile`. Frontend (vite/npm) build is driven by `vaadin-maven-plugin`, not by running npm manually.

## Layout
- Java root: `src/main/java/xyz/lawlietbot/spring/`. Entrypoint is `Application.java`, which also starts a stdin `Console` (`help`, `quit`, `threads`, `threads_stop`).
  - `frontend/views` holds routes, `frontend/layouts` the layouts, and `frontend/components` the reusable UI.
  - `backend` holds data, caches and util.
  - `syncserver` is the HTTP bridge to the separate Lawliet bot process (`SendEvent`, `EventOut`). The bot's source is in `/home/aninoss/Documents/Projects/Java/lawliet-bot` (see its `AGENTS.md`).
  - `CustomRequestHandler` handles non-Vaadin webhook endpoints (top.gg etc.).
- `frontend/components/dashboard/adapters` maps components from the external `lawliet-dashboard-components` dependency (JitPack, pinned by commit hash in `pom.xml`) to Vaadin components. A new dashboard component type needs an adapter, registered in `DashboardComponentConverter`.
- `bell/oauth/discord` is a vendored Discord OAuth client.
- CSS lives in `src/main/frontend/styles/`. Static assets and JS live in `src/main/webapp/`.

## Gotchas
- Don't edit or commit generated or ignored files. These are `src/main/frontend/generated/**`, `vite.generated.ts`, `webpack.generated.js`, `src/main/bundles/**`, `target/`, `node_modules/` and `types.d.ts*.bak`. `package.json`'s `vaadin` section and its `hash` are managed by Vaadin and get rewritten on build.
- Configuration comes from environment variables, read directly with `System.getenv` and with no defaults. Missing vars cause NPEs at runtime. They are:
  - `STRIPE_API_KEY`, `BOT_CLIENT_ID`, `BOT_CLIENT_SECRET`, `URL`, `CHECK_LOGIN`, `ALWAYS_INCLUDE_NSFW_REFERENCES`
  - `DASHBOARD_IMAGE_PATH`, `LOGS_DIR`, `DEVVOTES_DIR`
  - `SYNC_HOST`, `SYNC_PORT`, `SYNC_AUTH`
  - `PRINT_AUTH`, `TOPGG_AUTH`, `TOPGG_ANINOSS_AUTH`
- The actual bot lives in a separate project at `/home/aninoss/Documents/Projects/Java/lawliet-bot`, which has its own `AGENTS.md` with possibly more details. Consult it when working on anything that crosses the sync server boundary (`syncserver`, `SendEvent`/`EventOut`, `SYNC_*` vars, dashboard components, shared data).
- Many pages need the sync server (the bot) to be reachable, so expect failures locally without it.
- i18n: all user-facing text goes through `TranslationProvider` and `src/main/resources/translate_{en,de,es,fr,pt,ru,tr}.properties`. Add new keys to every locale file you can. A missing key renders `!<lang>: <key>`.
  - Locales are language-only (`new Locale("pt")` etc. in `TranslationProvider`), with no region codes. Supported: `en` (English, 🇬🇧 flag), `de` (German), `es` (Spanish, 🇪🇸 flag, so Spain by the flag), `fr` (French), `pt` (Portuguese, 🇧🇷 flag; text is Brazilian Portuguese, e.g. "você"), `ru` (Russian), `tr` (Turkish). Flags are set in `LocaleSelect`.
  - Values support `{this.other.key}` references and `[a|b]` multi-options, which are selected by passing a boolean as the first param.
  - The bundles are UTF-8, loaded via `UTF8Control`.
- Session cookies are `secure; SameSite=None; partitioned`, so login/OAuth flows need HTTPS and may not work over plain `http://localhost`.
- `.config/opencode/service.json` and `.local`/`.cache` are local tooling state. Ignore them and never copy their contents.
