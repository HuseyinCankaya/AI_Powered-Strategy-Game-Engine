package ai;

import core.Move;
import entities.PieceType;
import java.util.List;

public class MoveOrderer {

    /**
     * AI'ın inceleyeceği hamleleri en mantıklı olandan en saça olana doğru sıralar.
     */
    public void orderMoves(List<Move> moves) {
        moves.sort((m1, m2) -> scoreMove(m2) - scoreMove(m1));
    }

    private int scoreMove(Move move) {
        int guessScore = 0;

        // 1. Taş Yeme Hamleleri (MVV-LVA)
        if (move.getCapturedPiece() != null) {
            int victimValue = getPieceValue(move.getCapturedPiece().getType());
            int attackerValue = getPieceValue(move.getMovingPiece().getType());

            // Kurbanın değeri her zaman daha baskın olmalı (O yüzden 10 ile çarpıyoruz)
            guessScore = (10 * victimValue) - attackerValue;
        }

        // İleride buraya Piyon Terfisi (Promotion) için ekstra bonuslar eklenebilir.

        return guessScore;
    }

    private int getPieceValue(PieceType type) {
        switch (type) {
            case PAWN: return 100;
            case KNIGHT: return 300;
            case BISHOP: return 300;
            case ROOK: return 500;
            case QUEEN: return 900;
            case KING: return 20000;
            default: return 0;
        }
    }
}