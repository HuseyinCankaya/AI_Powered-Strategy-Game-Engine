package ai;

import core.Board;
import entities.Piece;
import entities.PieceColor;
import java.security.SecureRandom;

public class Zobrist {

    // 12 taş türü (6 Beyaz, 6 Siyah) x 64 kare için rastgele sayılar
    public static final long[][] pieceKeys = new long[12][64];

    // Sıra Siyahtayken XOR'lanacak sayı
    public static final long blackMoveKey;

    // Rok hakları için rastgele sayılar (Kısa/Uzun, Beyaz/Siyah = 4 durum)
    public static final long[] castlingKeys = new long[4];

    // Geçerken alma ihtimali olan 8 sütun için (a'dan h'ye)
    public static final long[] enPassantKeys = new long[8];

    // Sınıf yüklendiğinde rastgele sayıları bir kereye mahsus oluşturur
    static {
        // Daha kaliteli rastgele sayılar için SecureRandom kullanıyoruz
        SecureRandom random = new SecureRandom();

        for (int piece = 0; piece < 12; piece++) {
            for (int square = 0; square < 64; square++) {
                pieceKeys[piece][square] = random.nextLong();
            }
        }

        blackMoveKey = random.nextLong();

        for (int i = 0; i < 4; i++) castlingKeys[i] = random.nextLong();
        for (int i = 0; i < 8; i++) enPassantKeys[i] = random.nextLong();
    }

    /**
     * Sadece oyunun en başında veya FEN okunduğunda sıfırdan Kimlik No (Hash) üretmek için kullanılır.
     * Oyun esnasında bu fonksiyon yerine "incremental" (adım adım) güncelleme yapacağız.
     */
    public static long calculateZobristKey(Board board) {
        long finalKey = 0L;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null) {
                    int pieceIndex = getPieceIndex(piece);
                    int squareIndex = r * 8 + c; // 2D koordinatı (0-63) arası 1D indekse çevir

                    // XOR İşlemi (^=)
                    finalKey ^= pieceKeys[pieceIndex][squareIndex];
                }
            }
        }

        if (board.getColorToMove() == PieceColor.BLACK) {
            finalKey ^= blackMoveKey;
        }

        // İleride buraya başlangıçtaki Rok ve En Passant hakları da eklenecek

        return finalKey;
    }

    // Taşların tiplerini 0 ile 11 arasında bir indekse sıkıştırır
    public static int getPieceIndex(Piece piece) {
        int index = 0;
        switch (piece.getType()) {
            case PAWN: index = 0; break;
            case KNIGHT: index = 1; break;
            case BISHOP: index = 2; break;
            case ROOK: index = 3; break;
            case QUEEN: index = 4; break;
            case KING: index = 5; break;
        }
        // Siyah taşlar için indekse 6 ekle (6-11 arası)
        if (piece.getColor() == PieceColor.BLACK) {
            index += 6;
        }
        return index;
    }
}
