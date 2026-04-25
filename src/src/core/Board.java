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

        // Taşı yeni yerine koy
        grid[move.getToRow()][move.getToCol()] = movingPiece;
        // Eski yerini boşalt
        grid[move.getFromRow()][move.getFromCol()] = null;

        // Eğer taş ilk kez hareket ediyorsa, bunu unmake sırasında
        // geri alabilmek için Move sınıfına bir state eklemek gerekebilir.
        // Şimdilik temel taşıma işlemini yapıyoruz.
        if (movingPiece != null) {
            movingPiece.setHasMoved(true);
        }

        switchTurn(); // Sırayı rakibe geçir

        return capturedPiece;
    }

    /**
     * Oynanmış bir hamleyi geri alır. Tahtayı önceki orijinal haline döndürür.
     */
    public void unmakeMove(Move move, Piece capturedPiece) {
        Piece movingPiece = grid[move.getToRow()][move.getToCol()];

        // Taşı başladığı kareye geri koy
        grid[move.getFromRow()][move.getFromCol()] = movingPiece;
        // Yeni karesini yutulan taşla (veya boşlukla) doldur
        grid[move.getToRow()][move.getToCol()] = capturedPiece;

        // Not: hasMoved durumunu orijinal haline döndürmek için
        // daha gelişmiş bir History (Geçmiş) sistemine ihtiyaç duyacağız.

        switchTurn(); // Sırayı geri al
    }
}