package core;

import entities.PieceColor;
import entities.PieceType;
import entities.Piece;
import java.util.List;

public class TurnManager {
    private Board board;
    private MoveValidator validator;
    private GameState currentState;

    public TurnManager(Board board) {
        this.board = board;
        this.validator = new MoveValidator();
        this.currentState = GameState.ACTIVE;
    }

    /**
     * Bir hamleyi kurallara uygunsa gerçekleştirir ve oyun durumunu günceller.
     */
    public boolean makeTurn(Move move) {
        if (currentState == GameState.CHECKMATE || currentState == GameState.STALEMATE) {
            return false; // Oyun bittiği için hamle yapılamaz
        }

        // Hamleyi gerçekleştir
        board.makeMove(move);

        // Hamleden sonra oyun durumunu (Mat/Pat) kontrol et
        updateGameState();

        return true;
    }

    private void updateGameState() {
        PieceColor nextPlayer = board.getColorToMove();
        List<Move> legalMoves = validator.getLegalMoves(board, nextPlayer);

        // Eğer bir sonraki oyuncunun yapabileceği hiçbir yasal hamle kalmadıysa
        if (legalMoves.isEmpty()) {
            // Şah'ın nerede olduğunu bul
            int[] kingPos = findKingPos(board, nextPlayer);

            // Eğer Şah şu an saldırı altındaysa ve hamle yoksa: MAT
            if (validator.isSquareAttacked(board, kingPos[0], kingPos[1], nextPlayer.opposite())) {
                currentState = GameState.CHECKMATE;
            } else {
                // Şah saldırı altında değil ama hamle yoksa: PAT
                currentState = GameState.STALEMATE;
            }
        } else {
            // Oyun devam ediyor, sadece Şah durumunu kontrol et (UI için)
            int[] kingPos = findKingPos(board, nextPlayer);
            if (validator.isSquareAttacked(board, kingPos[0], kingPos[1], nextPlayer.opposite())) {
                currentState = GameState.CHECK;
            } else {
                currentState = GameState.ACTIVE;
            }
        }
    }

    private int[] findKingPos(Board board, PieceColor color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getType() == PieceType.KING && p.getColor() == color) {
                    return new int[]{r, c};
                }
            }
        }
        return new int[]{0,0}; // Teorik olarak buraya asla düşmemeli
    }

    public GameState getCurrentState() { return currentState; }
    public Board getBoard() { return board; }

    public MoveValidator getValidator() {
        return validator;
    }

    public void setValidator(MoveValidator validator) {
        this.validator = validator;
    }
}
