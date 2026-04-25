import builder.BoardBuilder;
import core.Board;
import core.Move;
import entities.Piece;
import entities.PieceColor;
import ai.MinimaxEngine;

public class Main {
    public static void main(String[] args) {
        // Çoban Matı'na 1 hamle kala dizilimi. Beyazın sırası.
        // Beyaz Vezir (f3) ve Fil (c4), Siyahın zayıf piyonuna (f7) bakıyor.
        String scholarMateFen = "r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/5Q2/PPPP1PPP/RNB1K1NR w KQkq - 0 1";

        System.out.println("--- Bristlesinger Chess Engine İlk AI Testi ---");

        Board board = BoardBuilder.buildFromFEN(scholarMateFen);
        System.out.println("Başlangıç Durumu (Beyaz Oynar):");
        printBoard(board);

        // Yapay Zeka Motorunu Başlat
        MinimaxEngine ai = new MinimaxEngine();

        // Botun derinliğini (depth) ayarlıyoruz.
        // Depth 3: "Benim hamlem -> Rakibin cevabı -> Benim cevabım" kadar ileriyi görür.
        int searchDepth = 3;

        System.out.println("\nAI " + searchDepth + " derinliğinde düşünüyor, lütfen bekleyin...");
        long startTime = System.currentTimeMillis();

        Move bestMove = ai.findBestMove(board, searchDepth, PieceColor.WHITE);

        long timeElapsed = System.currentTimeMillis() - startTime;

        if (bestMove != null) {
            System.out.println("\nAI Kararını Verdi! (" + timeElapsed + " ms)");
            System.out.println("En İyi Hamle: " + bestMove.toString());

            // Hamleyi tahtada oynatıp yeni durumu görelim
            board.makeMove(bestMove);
            System.out.println("\nHamle Sonrası Tahta:");
            printBoard(board);
        } else {
            System.out.println("AI hamle bulamadı (Mat veya Pat olabilir).");
        }
    }

    public static void printBoard(Board board) {
        System.out.println("  a b c d e f g h");
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
        return p.getColor() == PieceColor.WHITE ? symbol : Character.toLowerCase(symbol);
    }
}