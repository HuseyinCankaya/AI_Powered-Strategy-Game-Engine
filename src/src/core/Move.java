package core;

import entities.Piece;
import entities.PieceType;

public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;

    private Piece movingPiece;
    private Piece capturedPiece;

    // Özel Hamle Bayrakları
    private boolean isPromotion = false;
    private PieceType promotionType = PieceType.QUEEN;
    private boolean isEnPassant = false;
    private boolean isCastling = false;

    // YENİ EKLENDİ: Simülasyon sızıntısını önleyen hafıza
    private boolean isFirstMove = false;

    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece movingPiece) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.movingPiece = movingPiece;
        this.capturedPiece = null;
    }

    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece movingPiece, Piece capturedPiece) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
    }

    public int getFromRow() { return fromRow; }
    public int getFromCol() { return fromCol; }
    public int getToRow() { return toRow; }
    public int getToCol() { return toCol; }

    public Piece getMovingPiece() { return movingPiece; }
    public Piece getCapturedPiece() { return capturedPiece; }

    public boolean isPromotion() { return isPromotion; }
    public void setPromotion(boolean promotion) { isPromotion = promotion; }

    public PieceType getPromotionType() { return promotionType; }
    public void setPromotionType(PieceType promotionType) { this.promotionType = promotionType; }

    public boolean isEnPassant() { return isEnPassant; }
    public void setEnPassant(boolean enPassant) { isEnPassant = enPassant; }

    public boolean isCastling() { return isCastling; }
    public void setCastling(boolean castling) { isCastling = castling; }

    // YENİ EKLENDİ
    public boolean isFirstMove() { return isFirstMove; }
    public void setFirstMove(boolean firstMove) { isFirstMove = firstMove; }

    @Override
    public String toString() {
        return String.format("Hamle: [%d,%d] -> [%d,%d]", fromRow, fromCol, toRow, toCol);
    }
}