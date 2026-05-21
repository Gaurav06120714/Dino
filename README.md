# 🦕 Chrome Dino Game — Java Edition

A faithful recreation of the iconic Chrome browser offline dinosaur game, built with Java Swing.

---

## Features

- **Start screen** — press ↑ or SPACE to begin
- **Progressive difficulty** — speed and spawn rate increase every 500 points
- **Level display** — see your current difficulty level in the HUD
- **Smooth animations** — antialiased rendering for crisp visuals
- **White theme** — matches the real Chrome Dino aesthetic
- **High score tracking** — best score persists across rounds
- **Keyboard controls** — Arrow Up or SPACE to jump
- **Double jump** — press jump again mid-air for extra height
- **Night mode** — theme switches to dark after 1000 points

---

## Controls

| Key | Action |
|-----|--------|
| `↑` / `SPACE` | Jump |
| `↑` / `SPACE` (mid-air) | Double Jump |
| `↓` | Duck |
| `↑` / `SPACE` | Restart after Game Over |

---

## How to Build & Run

### Step 1 — Install Maven (one time only)

Make sure you have **Java 25** and **Maven** installed. On macOS:

```bash
brew install maven
```

Verify both are installed:

```bash
java -version    # should show openjdk 25+
mvn -version     # should show Apache Maven 3.x
```

---

### Step 2 — Build the executable JAR

Navigate to the project folder and run:

```bash
cd /path/to/dino-game
mvn package
```

This compiles the code and creates a runnable JAR at:

```
target/dino-game.jar
```

---

### Step 3 — Run the game

```bash
java -jar target/dino-game.jar
```

A window will open. Press `↑` or `SPACE` to start playing!

---

## Project Structure

```
dino-game/
├── README.md
├── pom.xml                          # Maven build config (produces dino-game.jar)
└── src/
    └── main/
        ├── java/
        │   ├── Main.java            # Entry point — creates 750×250 JFrame
        │   ├── ChromeDinosaur.java  # Game engine (rendering, physics, input)
        │   ├── Block.java           # Static obstacle (cactus)
        │   └── VeloBlock.java       # Moving obstacle with velocity
        └── resources/
            └── images/              # All game sprites (dino, cactus, bird, etc.)
```

---

## Tech Stack

- **Java 25** — language
- **Java Swing** — windowing and game loop via `javax.swing.Timer`
- **Graphics2D** — antialiased 2D rendering
- **Maven + maven-shade-plugin** — build and fat JAR packaging

---

## Push to GitHub

```bash
git init
git remote add origin https://github.com/Gaurav06120714/Dino.git
git add .
git commit -m "feat: Chrome Dino game — improved UI, start screen, speed scaling, executable JAR"
git branch -M main
git push -u origin main
```

> If you get a non-fast-forward error (repo already has commits):
> ```bash
> git push -u origin main --force
> ```

---

> Press ↑ or SPACE to start — dodge the cacti and survive as long as you can!
