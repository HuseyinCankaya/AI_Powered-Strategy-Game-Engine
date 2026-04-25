package core;

import entities.Piece;
import entities.PieceColor;
import entities.Queen;
import java.util.Stack;

public class Board {
    private Piece[][] grid;
    private PieceColor colorToMove;
    private Stack<Move> moveHistory;

    public Board() {
        this.grid = new Piece[8][8];
        this.colorToMove = PieceColor.WHITE;
        this.moveHistory = new Stack<>();
    }

    public Move getLastMove() {
        return moveHistory.isEmpty() ? null : moveHistory.peek();
    }

    public void setPiece(int row, int col, Piece piece) {
        if (isInsideBoard(row, col)) grid[row][col] = piece;
    }

    public Piece getPiece(int row, int col) {
        if (!isInsideBoard(row, col)) return null;
        return grid[row][col];
    }

    public PieceColor getColorToMove() { return colorToMove; }
    public void switchTurn() { colorToMove = colorToMove.opposite(); }

    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public Piece makeMove(Move move) {
        Piece movingPiece = grid[move.getFromRow()][move.getFromCol()];

        Piece capturedPiece = move.isEnPassant()
                ? grid[move.getFromRow()][move.getToCol()]
                : grid[move.getToRow()][move.getToCol()];

        grid[move.getFromRow()][move.getFromCol()] = null;

        if (move.isPromotion()) {
            grid[move.getToRow()][move.getToCol()] = new Queen(movingPiece.getColor());
        } else {
            grid[move.getToRow()][move.getToCol()] = movingPiece;
        }

        if (move.isEnPassant()) {
            grid[move.getFromRow()][move.getToCol()] = null;
        }

        // --- YENİ: SİMÜLASYON SIZINTISINI ÖNLEME (KAYIT) ---
        if (movingPiece != null) {
            move.setFirstMove(!movingPiece.hasMoved()); // Hamle yapılmadan önce hiç hareket etmiş miydi?
            movingPiece.setHasMoved(true); // Artık hareket etti
        }

        if (move.isCastling()) {
            if (move.getToCol() == 6) { // Kısa Rok
                Piece rook = grid[move.getFromRow()][7];
                grid[move.getFromRow()][5] = rook;
                grid[move.getFromRow()][7] = null;
                if (rook != null) rook.setHasMoved(true);
            } else if (move.getToCol() == 2) { // Uzun Rok
                Piece rook = grid[move.getFromRow()][0];
                grid[move.getFromRow()][3] = rook;
                grid[move.getFromRow()][0] = null;
                if (rook != null) rook.setHasMoved(true);
            }
        }

        moveHistory.push(move);
        switchTurn();
        return capturedPiece;
    }

    public void unmakeMove(Move move, Piece capturedPiece) {
        moveHistory.pop();

        Piece movingPiece = grid[move.getToRow()][move.getToCol()];

        if (move.isPromotion()) {
            movingPiece = new entities.Pawn(colorToMove.opposite());
            movingPiece.setHasMoved(true);
        }

        grid[move.getFromRow()][move.getFromCol()] = movingPiece;

        if (move.isEnPassant()) {
            grid[move.getFromRow()][move.getToCol()] = capturedPiece;
            grid[move.getToRow()][move.getToCol()] = null;
        } else {
            grid[move.getToRow()][move.getToCol()] = capturedPiece;
        }

        if (move.isCastling()) {
            if (move.getToCol() == 6) {
                Piece rook = grid[move.getFromRow()][5];
                grid[move.getFromRow()][7] = rook;
                grid[move.getFromRow()][5] = null;
                if (rook != null) rook.setHasMoved(false); // Kale geri döndü, hak geri verildi
            } else if (move.getToCol() == 2) {
                Piece rook = grid[move.getFromRow()][3];
                grid[move.getFromRow()][0] = rook;
                grid[move.getFromRow()][3] = null;
                if (rook != null) rook.setHasMoved(false);
            }
        }

        // --- YENİ: SİMÜLASYON SIZINTISINI ÖNLEME (GERİ ALMA) ---
        // Eğer bu taşın ilk hareketiydiyse, geri aldığımızda tekrar "hiç oynamamış" sayılmalı!
        if (movingPiece != null && move.isFirstMove()) {
            movingPiece.setHasMoved(false);
        }

        switchTurn();
    }
}