package entities;

import core.Board;
import core.Move;
import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(PieceColor color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public List<Move> calculateMoves(Board board, int currentRow, int currentCol) {
        List<Move> legalMoves = new ArrayList<>();

        // Filin gidebileceği 4 çapraz yön: (Sağ-Alt, Sol-Alt, Sağ-Üst, Sol-Üst)
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

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
                    break; // Engelle karşılaşınca dur
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