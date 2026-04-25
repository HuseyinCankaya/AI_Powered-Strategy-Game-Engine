package core;

import entities.Piece;

public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;

    private Piece movingPiece;   // Hamleyi yapan taş
    private Piece capturedPiece; // Varsa yutulan taş (yoksa null)

    // Yapay Zeka (AI) için kritik olan özel hamle bayrakları
    private boolean isPromotion = false;
    private boolean isEnPassant = false;
    private boolean isCastling = false;

    // Normal veya sadece yer değiştirme hamlesi için Constructor
    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece movingPiece) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.movingPiece = movingPiece;
        this.capturedPiece = null;
    }

    // Taş yeme (Capture) hamlesi için Constructor
    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece movingPiece, Piece capturedPiece) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
    }

    // --- Getter ve Setter'lar ---
    public int getFromRow() { return fromRow; }
    public int getFromCol() { return fromCol; }
    public int getToRow() { return toRow; }
    public int getToCol() { return toCol; }

    public Piece getMovingPiece() { return movingPiece; }
    public Piece getCapturedPiece() { return capturedPiece; }

    public boolean isPromotion() { return isPromotion; }
    public void setPromotion(boolean promotion) { isPromotion = promotion; }

    public boolean isEnPassant() { return isEnPassant; }
    public void setEnPassant(boolean enPassant) { isEnPassant = enPassant; }

    public boolean isCastling() { return isCastling; }
    public void setCastling(boolean castling) { isCastling = castling; }

    // Hata ayıklama (Debug) işini kolaylaştırmak için
    @Override
    public String toString() {
        return String.format("Hamle: [%d,%d] -> [%d,%d]", fromRow, fromCol, toRow, toCol);
    }
}