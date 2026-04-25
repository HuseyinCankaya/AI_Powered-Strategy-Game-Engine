package entities;

import core.Board;
import core.Move;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(PieceColor color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public List<Move> calculateMoves(Board board, int currentRow, int currentCol) {
        List<Move> legalMoves = new ArrayList<>();

        // Dizi mantığında indeks 0 en üst satırdır (Siyahın tarafı).
        // Beyaz piyonlar yukarı (satır endeksi azalır), Siyahlar aşağı (satır endeksi artar) gider.
        int moveDirection = (this.color == PieceColor.WHITE) ? -1 : 1;

        // Beyaz piyonlar 6. satırda (7. rank), siyah piyonlar 1. satırda (2. rank) başlar
        int startingRow = (this.color == PieceColor.WHITE) ? 6 : 1;
        int nextRow = currentRow + moveDirection;

        // 1. Standart İleri Tek Adım (Önü boşsa)
        if (isInsideBoard(nextRow, currentCol) && board.getPiece(nextRow, currentCol) == null) {
            legalMoves.add(new Move(currentRow, currentCol, nextRow, currentCol, this));

            // 2. İleri Çift Adım (Sadece başlangıç satırındaysa ve hemen önündeki kare de boş olduğu için buraya girdik)
            int doubleNextRow = currentRow + (moveDirection * 2);
            if (currentRow == startingRow && board.getPiece(doubleNextRow, currentCol) == null) {
                legalMoves.add(new Move(currentRow, currentCol, doubleNextRow, currentCol, this));
            }
        }

        // 3. Çapraz Taş Yeme (Sağ ve Sol Kontrolü)
        int[] captureCols = {currentCol - 1, currentCol + 1};
        for (int col : captureCols) {
            if (isInsideBoard(nextRow, col)) {
                Piece targetPiece = board.getPiece(nextRow, col);
                // Eğer hedef karede bir taş varsa ve rengi bizimkinden farklıysa (rakipse) yiyebiliriz
                if (targetPiece != null && targetPiece.getColor() != this.color) {
                    legalMoves.add(new Move(currentRow, currentCol, nextRow, col, this, targetPiece));
                }
            }
        }

        return legalMoves;
    }

    // Tahta sınırları dışına (ArrayOutOfBounds) çıkmayı önleyen güvenlik kontrolü
    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}