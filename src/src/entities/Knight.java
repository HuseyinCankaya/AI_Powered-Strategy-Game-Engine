package entities;

import core.Board;
import core.Move;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(PieceColor color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public List<Move> calculateMoves(Board board, int currentRow, int currentCol) {
        List<Move> legalMoves = new ArrayList<>();

        // Atın gidebileceği 8 olası L rotası (Satır ve Sütun değişimleri)
        int[][] moveOffsets = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] offset : moveOffsets) {
            int nextRow = currentRow + offset[0];
            int nextCol = currentCol + offset[1];

            if (isInsideBoard(nextRow, nextCol)) {
                Piece targetPiece = board.getPiece(nextRow, nextCol);

                if (targetPiece == null) {
                    // Kare boş, normal hamle
                    legalMoves.add(new Move(currentRow, currentCol, nextRow, nextCol, this));
                } else if (targetPiece.getColor() != this.color) {
                    // Rakip taş var, yeme hamlesi
                    legalMoves.add(new Move(currentRow, currentCol, nextRow, nextCol, this, targetPiece));
                }
            }
        }
        return legalMoves;
    }

    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}