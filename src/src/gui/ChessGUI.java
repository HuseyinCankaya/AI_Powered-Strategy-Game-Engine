package gui;

import ai.MinimaxEngine;
import core.Board;
import core.GameState;
import core.Move;
import core.TurnManager;
import entities.Piece;
import entities.PieceColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ChessGUI extends JFrame {
    private JPanel boardPanel;
    private JButton[][] squares = new JButton[8][8];

    private TurnManager turnManager;
    private MinimaxEngine ai;

    // Tıklama durumunu (State) takip etmek için
    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<Move> currentLegalMoves = null;

    private final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private final Color DARK_SQUARE = new Color(181, 136, 99);
    private final Color HIGHLIGHT_COLOR = new Color(130, 151, 105); // Hamle yapılabilecek yerler

    public ChessGUI(TurnManager turnManager, MinimaxEngine ai) {
        this.turnManager = turnManager;
        this.ai = ai;

        setTitle("Bristlesinger Chess Engine");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ekranın ortasında açılır

        initializeBoard();
        updateBoardUI();
    }

    private void initializeBoard() {
        boardPanel = new JPanel(new GridLayout(8, 8));

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton button = new JButton();
                // 1. Yazı tipini işletim sisteminin sembolleri daha iyi tanıdığı "Dialog" veya "Segoe UI Symbol" yapıp, boyutu 40'a düşürüyoruz.
                button.setFont(new Font("Dialog", Font.PLAIN, 40));
                // 2. Butonun iç boşluklarını (margin) sıfırlayarak karakterin sığması için yer açıyoruz.
                button.setMargin(new java.awt.Insets(0, 0, 0, 0));
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setOpaque(true);

                // Arka plan rengini (Açık/Koyu) ayarla
                if ((r + c) % 2 == 0) {
                    button.setBackground(LIGHT_SQUARE);
                } else {
                    button.setBackground(DARK_SQUARE);
                }

                // Butonlara tıklama olayı (Event Listener) ekle
                final int finalR = r;
                final int finalC = c;
                button.addActionListener(e -> handleSquareClick(finalR, finalC));

                squares[r][c] = button;
                boardPanel.add(button);
            }
        }
        add(boardPanel);
    }

    private void handleSquareClick(int r, int c) {
        // Oyun bittiyse veya sıra AI'daysa (Siyah) tıklamaları yoksay
        GameState state = turnManager.getCurrentState();
        if ((state != GameState.ACTIVE && state != GameState.CHECK) || turnManager.getBoard().getColorToMove() == PieceColor.BLACK) {
            return;
        }

        Board board = turnManager.getBoard();

        // 1. Aşama: Henüz bir taş seçilmediyse
        if (selectedRow == -1) {
            Piece clickedPiece = board.getPiece(r, c);
            if (clickedPiece != null && clickedPiece.getColor() == PieceColor.WHITE) {
                // Taşı seç ve yasal hamlelerini al
                selectedRow = r;
                selectedCol = c;

                // Tüm yasal hamleler arasından sadece bu taşa ait olanları filtrele
                List<Move> allLegalMoves = turnManager.getValidator().getLegalMoves(board, PieceColor.WHITE);
                currentLegalMoves = allLegalMoves.stream()
                        .filter(m -> m.getFromRow() == r && m.getFromCol() == c)
                        .toList();

                updateBoardUI(); // Seçili taşı ve gidilebilecek yerleri boya
            }
        }
        // 2. Aşama: Bir taş zaten seçiliyse ve hedef kareye tıklandıysa
        else {
            Move madeMove = null;
            // Tıklanan kare yasal hamlelerimiz arasında mı?
            for (Move move : currentLegalMoves) {
                if (move.getToRow() == r && move.getToCol() == c) {
                    madeMove = move;
                    break;
                }
            }

            if (madeMove != null) {
                // Hamleyi yap!
                turnManager.makeTurn(madeMove);
                resetSelection();
                updateBoardUI();

                // Hamleden sonra oyun durumunu kontrol et
                checkGameState();

                // Oyun devam ediyorsa AI'ı tetikle
                if (turnManager.getCurrentState() == GameState.ACTIVE) {
                    triggerAIMove();
                }
            } else {
                // Geçersiz bir yere tıklandıysa seçimi iptal et
                resetSelection();
                updateBoardUI();
            }
        }
    }

    private void triggerAIMove() {
        // AI hesaplaması arayüzü (UI) dondurmasın diye ayrı bir Thread'de çalıştırılır
        new Thread(() -> {
            Move bestMove = ai.findBestMove(turnManager.getBoard(), 4, PieceColor.BLACK); // Derinlik 4

            if (bestMove != null) {
                // Swing arayüzünü güvenli bir şekilde güncelle
                SwingUtilities.invokeLater(() -> {
                    turnManager.makeTurn(bestMove);
                    updateBoardUI();
                    checkGameState();
                });
            }
        }).start();
    }

    private void checkGameState() {
        GameState state = turnManager.getCurrentState();
        if (state == GameState.CHECKMATE) {
            String winner = (turnManager.getBoard().getColorToMove() == PieceColor.WHITE) ? "Siyah" : "Beyaz";
            JOptionPane.showMessageDialog(this, "ŞAH MAT! Kazanan: " + winner);
        } else if (state == GameState.STALEMATE) {
            JOptionPane.showMessageDialog(this, "PAT! Oyun Berabere.");
        }
    }

    private void resetSelection() {
        selectedRow = -1;
        selectedCol = -1;
        currentLegalMoves = null;
    }

    /**
     * Tahtadaki taşları ve arkaplan renklerini günceller.
     */
    private void updateBoardUI() {
        Board board = turnManager.getBoard();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton button = squares[r][c];

                // Standart arkaplan renklerini ayarla
                if ((r + c) % 2 == 0) button.setBackground(LIGHT_SQUARE);
                else button.setBackground(DARK_SQUARE);

                // Eğer bu kare, seçili taşın gidebileceği bir yerse rengini değiştir
                if (currentLegalMoves != null) {
                    for (Move move : currentLegalMoves) {
                        if (move.getToRow() == r && move.getToCol() == c) {
                            button.setBackground(HIGHLIGHT_COLOR);
                        }
                    }
                }

                // Taşı çiz (Unicode)
                Piece p = board.getPiece(r, c);
                if (p != null) {
                    button.setText(getUnicodePiece(p));
                    // Beyaz taşlar için siyah, siyah taşlar için farklı bir renk tonu
                    button.setForeground(p.getColor() == PieceColor.WHITE ? Color.WHITE : Color.BLACK);
                } else {
                    button.setText("");
                }
            }
        }
    }

    // Taşları estetik Unicode karakterlerine çevirir
    private String getUnicodePiece(Piece piece) {
        switch (piece.getType()) {
            case KING: return piece.getColor() == PieceColor.WHITE ? "♔" : "♚";
            case QUEEN: return piece.getColor() == PieceColor.WHITE ? "♕" : "♛";
            case ROOK: return piece.getColor() == PieceColor.WHITE ? "♖" : "♜";
            case BISHOP: return piece.getColor() == PieceColor.WHITE ? "♗" : "♝";
            case KNIGHT: return piece.getColor() == PieceColor.WHITE ? "♘" : "♞";
            case PAWN: return piece.getColor() == PieceColor.WHITE ? "♙" : "♟";
            default: return "";
        }
    }
}
