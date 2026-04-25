import ai.MinimaxEngine;
import builder.BoardBuilder;
import core.Board;
import core.TurnManager;
import gui.ChessGUI;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        // Standart satranç başlangıç pozisyonu (FEN formatı)
        String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        // 1. Tahtayı taşlarla inşa et
        Board board = BoardBuilder.buildFromFEN(startingFen);

        // 2. ZOBRIST BAŞLATMASI: Oyun başlamadan hemen önce ilk şifreyi (kimlik numarasını) üret!
        board.initZobrist();

        // 3. Oyun Orkestra Şefini (TurnManager) ve Yapay Zekayı (Minimax) oluştur
        TurnManager turnManager = new TurnManager(board);
        MinimaxEngine ai = new MinimaxEngine();

        // 4. Görsel Arayüzü (Swing GUI) güvenli bir şekilde ayağa kaldır
        SwingUtilities.invokeLater(() -> {
            ChessGUI gui = new ChessGUI(turnManager, ai);
            gui.setVisible(true); // Sahne senin!
        });
    }
}