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
        // --- ROK (CASTLING) KONTROLÜ ---
        if (!this.hasMoved) {
            // Kısa Rok (Kingside - h kalesi tarafı)
            if (board.getPiece(currentRow, 5) == null && board.getPiece(currentRow, 6) == null) {
                Piece rook = board.getPiece(currentRow, 7);
                // Kale orada mı ve daha önce hiç oynamamış mı?
                if (rook != null && rook.getType() == PieceType.ROOK && !rook.hasMoved()) {
                    Move castlingMove = new Move(currentRow, currentCol, currentRow, 6, this);
                    castlingMove.setCastling(true); // Bu hamlenin rok olduğunu işaretliyoruz
                    legalMoves.add(castlingMove);
                }
            }

            // Uzun Rok (Queenside - a kalesi tarafı)
            // Uzun rok için b, c ve d sütunları (1, 2, 3 endeksleri) boş olmalıdır.
            if (board.getPiece(currentRow, 3) == null && board.getPiece(currentRow, 2) == null && board.getPiece(currentRow, 1) == null) {
                Piece rook = board.getPiece(currentRow, 0);
                if (rook != null && rook.getType() == PieceType.ROOK && !rook.hasMoved()) {
                    Move castlingMove = new Move(currentRow, currentCol, currentRow, 2, this);
                    castlingMove.setCastling(true);
                    legalMoves.add(castlingMove);
                }
            }
        }
        return legalMoves;
    }

    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}