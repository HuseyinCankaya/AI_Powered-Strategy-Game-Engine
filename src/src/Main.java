import builder.BoardBuilder;
import core.Board;
import core.Move;
import core.MoveValidator;
import entities.Piece;
import entities.PieceColor;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Test senaryosu: Standart başlangıç dizilimi
        String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        // 2. Özel bir test senaryosu: Beyaz Şah'ın açmazda olduğu bir durum kurgulayalım
        // (Siyah Kale, Beyaz At'ın arkasındaki Beyaz Şah'a bakıyor olsun)
        String testFen = "r3k2r/8/8/3q4/8/8/3N4/3K4 w kq - 0 1";

        System.out.println("--- Bristlesinger Chess Engine Test Merkezi ---");

        Board board = BoardBuilder.buildFromFEN(testFen);
        printBoard(board);

        MoveValidator validator = new MoveValidator();
        List<Move> legalMoves = validator.getLegalMoves(board, PieceColor.WHITE);

        System.out.println("\nBeyaz için Yasal Hamle Sayısı: " + legalMoves.size());
        System.out.println("Yasal Hamle Listesi:");
        for (Move move : legalMoves) {
            System.out.println(move);
        }

        // Atın hareket edip edemediğini kontrol edelim (Açmaz testi)
        // Eğer MoveValidator doğru çalışıyorsa, Atın Şah'ı açıkta bırakacak hamleleri listede olmamalı.
    }

    /**
     * Tahtayı terminale okunaklı bir şekilde yazdırır.
     */
    public static void printBoard(Board board) {
        System.out.println("\n  a b c d e f g h");
        System.out.println("  ---------------");
        for (int r = 0; r < 8; r++) {
            System.out.print((8 - r) + "|");
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p == null) {
                    System.out.print(". ");
                } else {
                    char symbol = getPieceSymbol(p);
                    System.out.print(symbol + " ");
                }
            }
            System.out.println("|" + (8 - r));
        }
        System.out.println("  ---------------");
        System.out.println("  a b c d e f g h");
    }

    private static char getPieceSymbol(Piece p) {
        char symbol;
        switch (p.getType()) {
            case PAWN: symbol = 'P'; break;
            case KNIGHT: symbol = 'N'; break;
            case BISHOP: symbol = 'B'; break;
            case ROOK: symbol = 'R'; break;
            case QUEEN: symbol = 'Q'; break;
            case KING: symbol = 'K'; break;
            default: symbol = '?';
        }
        // Beyaz taşları Büyük harf, Siyah taşları küçük harf yapalım (FEN standardı)
        return p.getColor() == PieceColor.WHITE ? symbol : Character.toLowerCase(symbol);
    }
}