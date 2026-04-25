package entities;

import core.Board;
import core.Move;
import java.util.List;

public abstract class Piece {
    protected PieceColor color;
    protected PieceType type;
    protected boolean hasMoved; // Rok atma ve piyon ilk hamlesi için kritik

    public Piece(PieceColor color, PieceType type) {
        this.color = color;
        this.type = type;
        this.hasMoved = false;
    }

    public PieceColor getColor() { return color; }
    public PieceType getType() { return type; }

    public boolean hasMoved() { return hasMoved; }
    public void setHasMoved(boolean hasMoved) { this.hasMoved = hasMoved; }

    /**
     * Her taş (alt sınıf) kendi hareket kurallarını bu metotta tanımlayacak.
     * @param board Tahtanın o anki durumu (diğer taşların konumunu görmek için)
     * @param currentRow Taşın şu an bulunduğu satır
     * @param currentCol Taşın şu an bulunduğu sütun
     * @return Yapılabilecek geçerli hamlelerin listesi
     */
    public abstract List<Move> calculateMoves(Board board, int currentRow, int currentCol);
}