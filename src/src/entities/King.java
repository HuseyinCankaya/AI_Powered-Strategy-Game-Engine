package entities;

import core.Board;
import core.Move;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(PieceColor color) {
        super(color, PieceType.KING);
    }

    @Override
    public List<Move> calculateMoves(Board board, int currentRow, int currentCol) {
        List<Move> legalMoves = new ArrayList<>();

        // Şahın 8 komşu karesi
        int[][] moveOffsets = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] offset : moveOffsets) {
            int nextRow = currentRow + offset[0];
            int nextCol = currentCol + offset[1];

            if (isInsideBoard(nextRow, nextCol)) {
                Piece targetPiece = board.getPiece(nextRow, nextCol);

                if (targetPiece == null) {
                    legalMoves.add(new Move(currentRow, currentCol, nextRow, nextCol, this));
                } else if (targetPiece.getColor() != this.color) {
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