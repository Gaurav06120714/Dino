# Changelog

All notable changes to Chrome Dinosaur are documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [1.2.0] - 2026-05-28

### Added
- **Double Jump** — press ↑/SPACE twice in mid-air; blue indicator dots show remaining jumps
- **Night Mode** — smooth day→night colour transition every 700 points
- **Big Cacti** — 3 large cactus variants (`big-cactus1/2/3`) appear at level 3+
- **ScoreManager** — all-time high score persisted to `~/.dino-game/highscore.txt`
- **Jump indicator** — two dots under the dino show remaining air jumps

### Changed
- Board size increased from 750×250 to 900×300
- Main loop now launches on Event Dispatch Thread (thread safety)
- Collision hitboxes shrunk by 8px per side (more forgiving gameplay)
- Window title updated to "Chrome Dinosaur — Double Jump Edition"
- HUD now shows all-time best score instead of session-only best

### Fixed
- `velocityX` not resetting on game restart (was getting faster every death)
- `PLACE_CACTUS_TIMER` delay not resetting on restart

---

## [1.1.0] - 2026-05-20

### Added
- Start screen before first game
- Level indicator in HUD
- Speed scaling every 500 points
- Cactus spawn rate increases with speed

---

## [1.0.0] - 2026-05-10

### Added
- Initial release — Chrome Dino clone in Java Swing
- Dino run/jump/duck/dead animations
- Cactus + pterodactyl obstacles
- Scrolling ground and clouds
- Score display and high score tracking
- Reset button on game over
