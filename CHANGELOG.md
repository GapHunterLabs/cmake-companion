<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# CMake Companion Changelog

## [Unreleased]

### Changed

- The rating prompt's local counter keeps one-way fingerprints of findings
  instead of their file paths, and deletes the list that earlier versions
  kept.
- `PRIVACY.md` describes the values the plugin keeps in the IDE's local
  settings.

## [0.1.2]

### Fixed

- Review/star CTA now links to this plugin's own Marketplace
  reviews page instead of the vendor's generic plugin list.

## [0.1.1]

### Added

- Review/star CTA: after 10 distinct real unmatched-parenthesis
  findings, a one-time notification asks whether to rate the plugin on
  Marketplace, with a permanent "Don't ask again" option.

## [0.1.0]

### Added

- Syntax highlighting for CMake scripts (comments, strings, `${VAR}`
  references, parentheses) via a hand-rolled lexer.
- Known-command highlighting against the standard CMake command catalog.
- Unmatched-parenthesis inspection.
- Opt-in file detection by filename/content, no license checks, no
  upsell notifications.

[Unreleased]: https://github.com/GapHunterLabs/cmake-companion/compare/0.1.2...HEAD
[0.1.2]: https://github.com/GapHunterLabs/cmake-companion/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/GapHunterLabs/cmake-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/cmake-companion/commits/0.1.0
