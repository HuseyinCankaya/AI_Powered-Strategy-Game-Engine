package entities;

import core.Board;
import core.Move;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(PieceColor color) {
        super(color, PieceType.ROOK);
    }

    @Override
    public List<Move> calculateMoves(Board board, int currentRow, int currentCol) {
        List<Move> legalMoves = new ArrayList<>();

        // Kalenin gidebileceği 4 yön: {satır_değişimi, sütun_değişimi}
        // (Aşağı, Yukarı, Sağ, Sol)
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            int nextRow = currentRow + dir[0];
            int nextCol = currentCol + dir[1];

            // Tahta sınırları içinde kaldığımız sürece bu yönde kaymaya devam et
            while (isInsideBoard(nextRow, nextCol)) {
                Piece targetPiece = board.getPiece(nextRow, nextCol);

                if (targetPiece == null) {
                    legalMoves.add(new Move(currentRow, currentCol, nextRow, nextCol, this));
                } else {
                    if (targetPiece.getColor() != this.color) {
                        legalMoves.add(new Move(currentRow, currentCol, nextRow, nextCol, this, targetPiece));
                    }
                    break; // Kendi taşımız da olsa rakip de olsa, taşa çarptıktan sonra kaymayı durdur.
                }
                nextRow += dir[0];
                nextCol += dir[1];
            }
        }
        return legalMoves;
    }

    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
