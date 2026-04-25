package entities;

public enum PieceColor {
    WHITE, BLACK;

    // Sırayı değiştirmek için pratik metod
    public PieceColor opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
