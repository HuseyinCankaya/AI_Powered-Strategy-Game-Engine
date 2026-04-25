import ai.MinimaxEngine;
import builder.BoardBuilder;
import core.Board;
import core.TurnManager;
import gui.ChessGUI;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        // Standart başlangıç pozisyonu
        String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        Board board = BoardBuilder.buildFromFEN(startingFen);
        TurnManager turnManager = new TurnManager(board);
        MinimaxEngine ai = new MinimaxEngine();

        // Swing arayüzlerinin "Event Dispatch Thread" üzerinde çalışması gerekir.
        SwingUtilities.invokeLater(() -> {
            ChessGUI gui = new ChessGUI(turnManager, ai);
            gui.setVisible(true); // Pencereyi göster!
        });
    }
}