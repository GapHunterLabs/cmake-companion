# CMake Companion

Syntax highlighting, known-command highlighting, and an unmatched-paren
inspection for `CMakeLists.txt`/`*.cmake` files.

## Why it exists

**CMake Plus** (JetBrains Marketplace id 12869), 57,780 downloads, paid
subscription, vendor Artsiom Chapialiou. The same vendor's free sibling
plugin ("CMake simple highlighter", 254k downloads) had its command-argument
highlighting moved behind the paid plugin, which drew complaints about the
business model on top of real bugs. Verbatim reviewer complaints:

- *"The new version makes the IDE unresponsive and is really slow."*
  (Jacob Hatami, 2023-11)
- Two separate reviews (2021-12, 2022-10) from the same user reporting
  **"License not found"** after repeated reactivation.
- A real GitHub issue against the vendor's own repo: the plugin crashes on
  startup after purchasing a license, "Access Denied" --
  *"Plugin 'artsiomch.cmake.plus' failed to initialize and will be
  disabled"*.
- An upsell notification on every IDE boot, "with the intent to annoy
  you/influence you psychologically."

## Why built this way

- **No licensing code at all.** No `LicensingFacade` check, no upsell
  notification, no background licensing calls -- there is nothing here
  that can fail with "License not found" or "Access Denied" because there
  is no license gate to fail.
- **Hand-rolled lexer** (`CMakeLexer`), same pattern already proven twice
  in this workspace (`nginx-companion`, `graphql-companion`): CMake's
  syntax is small and stable enough that a manual scanner is simpler than
  wiring up a full grammar for it, and a small, well-tested lexer is much
  less likely to cause the "unresponsive and slow" complaint above than
  a heavier general-purpose parser running on every keystroke.
- **Opt-in by filename/content, not by hijacking every text file.**
  `CMakeLists.txt` and `*.cmake` are matched directly; anything else needs
  two independent content hints (`project(`, `add_executable(`, etc.)
  before this plugin claims it.
- **Proactive `FileTypeIdentifiableByVirtualFile` + `order="first"`.**
  `nginx-companion` needed a real 5-round investigation to discover that a
  bundled `FileTypeIdentifiableByVirtualFile` implementation (e.g.
  TextMate) always wins a naive `FileTypeDetector`-only registration,
  regardless of specificity (see `SDK_GOTCHAS.md` SS10). Applied here from
  the first version instead of rediscovering the same bug.
- **The "unmatched parenthesis" inspection is a pure function over the
  lexer's token stream** (`CMakeParenChecker`), not a PSI walk -- directly
  unit-testable, and the same off-EDT-friendly, allocation-light approach
  as the lexer itself.

## Usage

Open or create a `CMakeLists.txt`/`*.cmake` file. Known commands
(`add_executable`, `target_link_libraries`, `find_package`, etc.) are
highlighted when followed by `(`; comments, strings, and `${VAR}`
references get their own colors; an unmatched `(` or `)` is flagged as a
real error, not just a bracket-matching hint.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**kennyj.diazm@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
