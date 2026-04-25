package ai;

import core.Board;
import entities.Piece;
import entities.PieceColor;

public class BoardEvaluator {

    private static final int PAWN_VAL = 100;
    private static final int KNIGHT_VAL = 300;
    private static final int BISHOP_VAL = 300;
    private static final int ROOK_VAL = 500;
    private static final int QUEEN_VAL = 900;

    public int evaluate(Board board) {
        int score = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null) {
                    // 1. Taşın ham (materyal) değeri
                    int materialValue = getPieceValue(piece);

                    // 2. Taşın tahtadaki konumuna göre aldığı bonus
                    int positionalBonus = EvaluationConstants.getPositionalBonus(piece.getType(), piece.getColor(), r, c);

                    int totalPieceValue = materialValue + positionalBonus;

                    // Beyaz için pozitif, Siyah için negatif skora ekle
                    score += (piece.getColor() == PieceColor.WHITE) ? totalPieceValue : -totalPieceValue;
                }
            }
        }
        return score;
    }

    private int getPieceValue(Piece piece) {
        switch (piece.getType()) {
            case PAWN: return PAWN_VAL;
            case KNIGHT: return KNIGHT_VAL;
            case BISHOP: return BISHOP_VAL;
            case ROOK: return ROOK_VAL;
            case QUEEN: return QUEEN_VAL;
            case KING: return 20000;
            default: return 0;
        }
    }
}