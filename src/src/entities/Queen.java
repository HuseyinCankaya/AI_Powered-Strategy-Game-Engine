package entities;

import core.Board;
import core.Move;
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(PieceColor color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    public List<Move> calculateMoves(Board board, int currentRow, int currentCol) {
        List<Move> legalMoves = new ArrayList<>();

        // Vezirin 8 yönü (Kale ve Filin birleşimi)
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}, // Düz
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Çapraz
        };

        for (int[] dir : directions) {
            int nextRow = currentRow + dir[0];
            int nextCol = currentCol + dir[1];

            while (isInsideBoard(nextRow, nextCol)) {
                Piece targetPiece = board.getPiece(nextRow, nextCol);

                if (targetPiece == null) {
                    legalMoves.add(new Move(currentRow, currentCol, nextRow, nextCol, this));
                } else {
                    if (targetPiece.getColor() != this.color) {
                        legalMoves.add(new Move(currentRow, currentCol, nextRow, nextCol, this, targetPiece));
                    }
                    break;
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