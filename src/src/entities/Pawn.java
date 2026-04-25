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

        int moveDirection = (this.color == PieceColor.WHITE) ? -1 : 1;
        int startingRow = (this.color == PieceColor.WHITE) ? 6 : 1;
        int promotionRow = (this.color == PieceColor.WHITE) ? 0 : 7;

        int nextRow = currentRow + moveDirection;

        // 1. İleri Adımlar
        if (isInsideBoard(nextRow, currentCol) && board.getPiece(nextRow, currentCol) == null) {
            Move forwardMove = new Move(currentRow, currentCol, nextRow, currentCol, this);
            if (nextRow == promotionRow) forwardMove.setPromotion(true);
            legalMoves.add(forwardMove);

            int doubleNextRow = currentRow + (moveDirection * 2);
            if (currentRow == startingRow && board.getPiece(doubleNextRow, currentCol) == null) {
                legalMoves.add(new Move(currentRow, currentCol, doubleNextRow, currentCol, this));
            }
        }

        // 2. Çapraz Yemeler
        int[] captureCols = {currentCol - 1, currentCol + 1};
        for (int col : captureCols) {
            if (isInsideBoard(nextRow, col)) {
                Piece targetPiece = board.getPiece(nextRow, col);
                if (targetPiece != null && targetPiece.getColor() != this.color) {
                    Move captureMove = new Move(currentRow, currentCol, nextRow, col, this, targetPiece);
                    if (nextRow == promotionRow) captureMove.setPromotion(true);
                    legalMoves.add(captureMove);
                }
            }
        }

        // 3. GEÇERKEN ALMA (EN PASSANT)
        Move lastMove = board.getLastMove();
        if (lastMove != null && lastMove.getMovingPiece().getType() == PieceType.PAWN) {
            if (Math.abs(lastMove.getFromRow() - lastMove.getToRow()) == 2) {
                if (lastMove.getToRow() == currentRow && Math.abs(lastMove.getToCol() - currentCol) == 1) {
                    int epCol = lastMove.getToCol();
                    Move epMove = new Move(currentRow, currentCol, nextRow, epCol, this, lastMove.getMovingPiece());
                    epMove.setEnPassant(true);
                    legalMoves.add(epMove);
                }
            }
        }

        return legalMoves;
    }

    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}