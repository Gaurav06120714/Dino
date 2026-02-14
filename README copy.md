<p align="center">
<img src="https://img.shields.io/badge/Java-^23.0.2-orange" alt="Java version"/>
</p>

<p align="center">
<img src="https://img.shields.io/github/license/NamelessProj/Chrome_Dinosaur" alt="licence"/>
<img src="https://img.shields.io/github/repo-size/NamelessProj/Chrome_Dinosaur" alt="repo size"/>
</p>

# Chrome Dinosaur Game (Java)
A simple clone of the Chrome Dinosaur offline game, written in Java using Swing.

## Features
- Classic endless runner gameplay
- Jump and duck to avoid cacti and pterodactyls
- Moving ground and clouds for a dynamic background
- Score and high score tracking

## How to Run
1. **Requirements:**
    - Java 23 or higher
    - Maven

2. **Build and Run:**
    - Clone the repository:
      ```bash
      git clone https://github.com/NamelessProj/Chrome_Dinosaur.git
      ```
    - Navigate to the project directory:
      ```bash
      cd Chrome_Dinosaur
      ```
    - Build the project using Maven:
      ```bash
        mvn clean package
        mvn compile
        mvn package
      ```
    - Run the game: 
      ```bash
        java -jar target/Chrome_Dinosaure-1.0-SNAPSHOT.jar
      ```
3. **Controls:**
    - `Key Up`: Jump
    - `Key Down`: Duck
    - `Key R`: Restart game

## Project Structure
- [`src/main/java/ChromeDinosaur.java`](./src/main/java/ChromeDinosaur.java) - Main game logic and rendering
- [`src/main/java/Block.java`](./src/main/java/Block.java) - Game object representation
- [`src/main/java/Main.java`](./src/main/java/Main.java) - Entry point
- [`src/main/resources/images/`](./src/main/resources/images) - Game images (dinosaur, cacti, ground, etc.)

## License
See [`LICENCE`](./LICENCE) for details.