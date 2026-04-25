package core;

import entities.*;
import java.util.ArrayList;
import java.util.List;

public class MoveValidator {

    /**
     * Verilen renk için tahtadaki %100 Yasal (Strict-Legal) hamlelerin listesini döndürür.
     */
    public List<Move> getLegalMoves(Board board, PieceColor color) {
        List<Move> pseudoLegalMoves = new ArrayList<>();
        List<Move> strictLegalMoves = new ArrayList<>();

        // 1. O renkteki tüm taşların sözde yasal hamlelerini topla
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.getColor() == color) {
                    pseudoLegalMoves.addAll(piece.calculateMoves(board, r, c));
                }
            }
        }

        // 2. Her hamleyi simüle et ve Şah'ın güvende kalıp kalmadığını kontrol et
        for (Move move : pseudoLegalMoves) {
            // --- ROK İÇİN ÖZEL GÜVENLİK KURALLARI ---
            if (move.isCastling()) {
                int[] kingPos = findKingPosition(board, color);

                // 1. Kural: Şah halihazırda tehdit altında (Check) olamaz
                if (isSquareAttacked(board, kingPos[0], kingPos[1], color.opposite())) {
                    continue; // Rok yapılamaz, bu dalı atla
                }

                // 2. Kural: Şahın üzerinden atladığı ara kare tehdit altında olamaz
                // Kısa rok ise f sütunu (5), uzun rok ise d sütunu (3)
                int passCol = (move.getToCol() == 6) ? 5 : 3;
                if (isSquareAttacked(board, move.getFromRow(), passCol, color.opposite())) {
                    continue; // Rok yapılamaz, bu dalı atla
                }
            }
            // Hamleyi tahtada sanal olarak oyna
            Piece captured = board.makeMove(move);

            // Hamleden sonra Şahımızın nerede olduğunu bul
            int[] kingPos = findKingPosition(board, color);

            // Eğer şah tahtadaysa ve YENİ konumda rakip tarafından tehdit edilmiyorsa, hamle yasaldır.
            // (Not: makeMove sırayı rakibe geçirdiği için 'board.getColorToMove()' artık rakibin rengidir)
            if (kingPos != null && !isSquareAttacked(board, kingPos[0], kingPos[1], color.opposite())) {
                strictLegalMoves.add(move);
            }

            // Hamleyi geri al ve tahtayı orijinal haline döndür
            board.unmakeMove(move, captured);
        }

        return strictLegalMoves;
    }

    /**
     * Tahtadaki Şah'ın [satır, sütun] koordinatlarını bulur.
     */
    private int[] findKingPosition(Board board, PieceColor color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.getType() == PieceType.KING && piece.getColor() == color) {
                    return new int[]{r, c};
                }
            }
        }
        return null; // Hata durumu
    }

    /**
     * Belirtilen hedefin (hedef karenin), saldıran renk tarafından tehdit edilip edilmediğini kontrol eder.
     */
    public boolean isSquareAttacked(Board board, int targetRow, int targetCol, PieceColor attackerColor) {

        // 1. Piyon Tehditleri (Saldıranın rengine göre piyonun gelme yönü değişir)
        int pawnDirection = (attackerColor == PieceColor.WHITE) ? 1 : -1;
        int[][] pawnAttacks = {{pawnDirection, -1}, {pawnDirection, 1}};
        for (int[] offset : pawnAttacks) {
            int r = targetRow + offset[0];
            int c = targetCol + offset[1];
            if (isInside(r, c)) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.PAWN) return true;
            }
        }

        // 2. At Tehditleri
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] offset : knightMoves) {
            int r = targetRow + offset[0];
            int c = targetCol + offset[1];
            if (isInside(r, c)) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.KNIGHT) return true;
            }
        }

        // 3. Düz Tehditler (Kale veya Vezir)
        int[][] straightDirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        if (checkRayAttacks(board, targetRow, targetCol, attackerColor, straightDirs, PieceType.ROOK, PieceType.QUEEN)) return true;

        // 4. Çapraz Tehditler (Fil veya Vezir)
        int[][] diagDirs = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        if (checkRayAttacks(board, targetRow, targetCol, attackerColor, diagDirs, PieceType.BISHOP, PieceType.QUEEN)) return true;

        // 5. Şah Tehditleri (Şahlar birbirine yaklaşamaz)
        int[][] kingMoves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] offset : kingMoves) {
            int r = targetRow + offset[0];
            int c = targetCol + offset[1];
            if (isInside(r, c)) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.KING) return true;
            }
        }

        return false;
    }

    // Işın Gönderme (Ray-Casting) yardımcı fonksiyonu
    private boolean checkRayAttacks(Board board, int startRow, int startCol, PieceColor attackerColor, int[][] directions, PieceType type1, PieceType type2) {
        for (int[] dir : directions) {
            int r = startRow + dir[0];
            int c = startCol + dir[1];
            while (isInside(r, c)) {
                Piece p = board.getPiece(r, c);
                if (p != null) {
                    if (p.getColor() == attackerColor && (p.getType() == type1 || p.getType() == type2)) {
                        return true;
                    }
                    break; // Bir taşa çarptık, arkasına bakmaya gerek yok
                }
                r += dir[0];
                c += dir[1];
            }
        }
        return false;
    }

    private boolean isInside(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}