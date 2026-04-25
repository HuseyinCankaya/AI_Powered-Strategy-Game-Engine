
package core;

import ai.Zobrist;
import entities.Piece;
import entities.PieceColor;
import entities.Queen;
import java.util.Stack;

public class Board {
    private Piece[][] grid;
    private PieceColor colorToMove;
    private Stack<Move> moveHistory;

    // Zobrist Hafızası
    private long currentZobristKey;
    private Stack<Long> zobristHistory;

    public Board() {
        this.grid = new Piece[8][8];
        this.colorToMove = PieceColor.WHITE;
        this.moveHistory = new Stack<>();

        this.zobristHistory = new Stack<>();
        this.currentZobristKey = 0L;
    }

    // Oyun ilk kurulduğunda çağrılır
    public void initZobrist() {
        this.currentZobristKey = Zobrist.calculateZobristKey(this);
    }

    // Taşı Zobrist şifresine ekler veya çıkarır (XOR)
    private void toggleZobristPiece(Piece piece, int row, int col) {
        if (piece != null) {
            int pieceIndex = Zobrist.getPieceIndex(piece);
            int squareIndex = row * 8 + col;
            currentZobristKey ^= Zobrist.pieceKeys[pieceIndex][squareIndex];
        }
    }

    public long getZobristKey() { return currentZobristKey; }

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
        // ZOBRIST: Hamle yapılmadan hemen önceki şifreyi geçmişe kaydet
        zobristHistory.push(currentZobristKey);

        Piece movingPiece = grid[move.getFromRow()][move.getFromCol()];

        // ZOBRIST 1: Oynayan taşı eski yerinden sil
        toggleZobristPiece(movingPiece, move.getFromRow(), move.getFromCol());

        Piece capturedPiece = move.isEnPassant()
                ? grid[move.getFromRow()][move.getToCol()]
                : grid[move.getToRow()][move.getToCol()];

        // ZOBRIST 2: Eğer taş yendiyse, o taşı şifreden sil
        if (capturedPiece != null) {
            int capRow = move.isEnPassant() ? move.getFromRow() : move.getToRow();
            toggleZobristPiece(capturedPiece, capRow, move.getToCol());
        }

        grid[move.getFromRow()][move.getFromCol()] = null;

        Piece pieceToPlace;
        if (move.isPromotion()) {
            pieceToPlace = new Queen(movingPiece.getColor());
            grid[move.getToRow()][move.getToCol()] = pieceToPlace;
        } else {
            pieceToPlace = movingPiece;
            grid[move.getToRow()][move.getToCol()] = pieceToPlace;
        }

        if (move.isEnPassant()) {
            grid[move.getFromRow()][move.getToCol()] = null;
        }

        // ZOBRIST 3: Taşı (veya terfi eden Veziri) yeni konumunda şifreye ekle
        toggleZobristPiece(pieceToPlace, move.getToRow(), move.getToCol());

        if (movingPiece != null) {
            move.setFirstMove(!movingPiece.hasMoved());
            movingPiece.setHasMoved(true);
        }

        // --- ROK YAPILDIYSA KALEYİ DE TAŞI VE ZOBRIST'İ GÜNCELLE ---
        if (move.isCastling()) {
            if (move.getToCol() == 6) { // Kısa Rok
                Piece rook = grid[move.getFromRow()][7];
                toggleZobristPiece(rook, move.getFromRow(), 7); // Kaleyi sil
                grid[move.getFromRow()][5] = rook;
                grid[move.getFromRow()][7] = null;
                if (rook != null) rook.setHasMoved(true);
                toggleZobristPiece(rook, move.getFromRow(), 5); // Kaleyi yeni yerine ekle
            } else if (move.getToCol() == 2) { // Uzun Rok
                Piece rook = grid[move.getFromRow()][0];
                toggleZobristPiece(rook, move.getFromRow(), 0); // Kaleyi sil
                grid[move.getFromRow()][3] = rook;
                grid[move.getFromRow()][0] = null;
                if (rook != null) rook.setHasMoved(true);
                toggleZobristPiece(rook, move.getFromRow(), 3); // Kaleyi yeni yerine ekle
            }
        }

        // ZOBRIST 4: Sırayı değiştir
        currentZobristKey ^= Zobrist.blackMoveKey;

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
                if (rook != null) rook.setHasMoved(false);
            } else if (move.getToCol() == 2) {
                Piece rook = grid[move.getFromRow()][3];
                grid[move.getFromRow()][0] = rook;
                grid[move.getFromRow()][3] = null;
                if (rook != null) rook.setHasMoved(false);
            }
        }

        if (movingPiece != null && move.isFirstMove()) {
            movingPiece.setHasMoved(false);
        }

        // ZOBRIST: Tüm karmaşık XOR işlemlerini geri alıp tek hamlede eski şifreyi yükle
        currentZobristKey = zobristHistory.pop();

        switchTurn();
    }
}