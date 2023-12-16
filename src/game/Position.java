package game;

import java.util.Random;

// Пара координат = позиция
public class Position {
    public int x;
    public int y;

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void add(Position move) {
        x += move.x;
        y += move.y;
    }

    public boolean isOutOfBorder(Position move) {
        return x + move.x >= 1 &&
                x + move.x <= 100 &&
                y + move.y >= 1 &&
                y + move.y <= 100;
    }

    public boolean isEqualTo(Position position) {
        return x == position.x && y == position.y;
    }

    // Координата от 1 до 100
    private static int randomCoordinate() {
        int min = 1;
        int max = 100;
        int diff = max - min;
        return new Random().nextInt(diff + 1) + min;
    }

    static Position randomPosition() {
        return new Position(randomCoordinate(), randomCoordinate());
    }

    public String toStringForOutput() {
        return String.format("(%d, %d)", x, y);
    }

    public String toStringForSend() {
        return String.format("%d,%d", x, y);
    }
}