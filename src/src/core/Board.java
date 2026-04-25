package core;

import entities.Piece;
import entities.PieceColor;

public class Board {
    private Piece[][] grid;
    private PieceColor colorToMove;

    public Board() {
        // Standart 8x8 satranç tahtası
        this.grid = new Piece[8][8];
        this.colorToMove = PieceColor.WHITE; // Her zaman Beyaz başlar
    }

    // --- Tahta Üzerinde Okuma/Yazma İşlemleri ---

    public void setPiece(int row, int col, Piece piece) {
        if (isInsideBoard(row, col)) {
            grid[row][col] = piece;
        }
    }

    public Piece getPiece(int row, int col) {
        if (!isInsideBoard(row, col)) {
            return null; // Tahta dışına çıkılırsa null döndür
        }
        return grid[row][col];
    }

    public PieceColor getColorToMove() { return colorToMove; }
    public void switchTurn() { colorToMove = colorToMove.opposite(); }

    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    // --- Yapay Zeka ve Motor İçin Kritik Metodlar (Make / Unmake) ---

    /**
     * Hamleyi tahtada oynar ve eğer bir taş yendiyse onu geri döndürür.
     * Yapay zekanın derinlik taraması (depth search) için kullanılır.
     */
    public Piece makeMove(Move move) {
        Piece movingPiece = grid[move.getFromRow()][move.getFromCol()];
        Piece capturedPiece = grid[move.getToRow()][move.getToCol()];

        // Şahı (veya taşı) yeni yerine koy
        grid[move.getToRow()][move.getToCol()] = movingPiece;
        grid[move.getFromRow()][move.getFromCol()] = null;

        if (movingPiece != null) {
            movingPiece.setHasMoved(true);
        }

        // --- ROK YAPILDIYSA KALEYİ DE TAŞI ---
        if (move.isCastling()) {
            if (move.getToCol() == 6) { // Kısa Rok
                Piece rook = grid[move.getFromRow()][7]; // h kalesini al
                grid[move.getFromRow()][5] = rook;       // f karesine koy
                grid[move.getFromRow()][7] = null;       // h karesini boşalt
                if (rook != null) rook.setHasMoved(true);
            } else if (move.getToCol() == 2) { // Uzun Rok
                Piece rook = grid[move.getFromRow()][0]; // a kalesini al
                grid[move.getFromRow()][3] = rook;       // d karesine koy
                grid[move.getFromRow()][0] = null;       // a karesini boşalt
                if (rook != null) rook.setHasMoved(true);
            }
        }

        switchTurn();
        return capturedPiece;
    }

    public void unmakeMove(Move move, Piece capturedPiece) {
        Piece movingPiece = grid[move.getToRow()][move.getToCol()];

        // Taşı eski yerine koy
        grid[move.getFromRow()][move.getFromCol()] = movingPiece;
        grid[move.getToRow()][move.getToCol()] = capturedPiece;

        // --- ROK İPTAL EDİLDİYSE KALEYİ DE ESKİ YERİNE KOY ---
        if (move.isCastling()) {
            if (move.getToCol() == 6) { // Kısa Rok Geri Alma
                Piece rook = grid[move.getFromRow()][5];
                grid[move.getFromRow()][7] = rook;
                grid[move.getFromRow()][5] = null;
                // Rok durumunda her iki taşın da daha önce hiç oynamadığından eminiz
                if (rook != null) rook.setHasMoved(false);
                if (movingPiece != null) movingPiece.setHasMoved(false);
            } else if (move.getToCol() == 2) { // Uzun Rok Geri Alma
                Piece rook = grid[move.getFromRow()][3];
                grid[move.getFromRow()][0] = rook;
                grid[move.getFromRow()][3] = null;
                if (rook != null) rook.setHasMoved(false);
                if (movingPiece != null) movingPiece.setHasMoved(false);
            }
        }

        switchTurn();
    }
}