# 🎮 Java BreakOut Game

A classic **BreakOut (Brick Breaker)** arcade game built with **Java Swing**. This project demonstrates basic 2D game mechanics, collision detection, animation using timers, and keyboard input handling.

## 🧱 Game Features

- Move paddle with **← Left** and **→ Right** arrow keys
- **Pause / Resume** with `Spacebar`
- **Win** after destroying all bricks
- **Game Over** if ball falls below paddle
- Restart or Exit from Win/Game Over screens
- Scoring system (10 points per brick)

---

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- A Java IDE (e.g. IntelliJ IDEA, Eclipse) or terminal

### Run the Game
1. Clone or download this repository
2. Compile and run the project:


🎮 Controls
| Key        | Action                   |
| ---------- | ------------------------ |
| `←` / `→`  | Move paddle              |
| `Spacebar` | Pause/Unpause            |
| `R`        | Restart on Win/Game Over |
| `E`        | Exit Game                |


🧠 How It Works:

>Java Swing is used for GUI components and drawing.

>Game loop is powered by javax.swing.Timer.

>Collision detection is handled with Rectangle intersection checks.

>Bricks are represented with a 2D array (map) where 0 means active and 1 means destroyed.

>Game states (running, paused, won, over) are handled with conditionals and dialogs.


Notes:
This game was inspired by the classic BreakOut arcade game and created as a personal learning project to practice Java GUI development.
