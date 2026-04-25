package ai;

import core.Board;
import core.Move;
import core.MoveValidator;
import entities.Piece;
import entities.PieceColor;
import java.util.List;

public class MinimaxEngine {
    private BoardEvaluator evaluator;
    private MoveValidator validator;
    private MoveOrderer orderer;
    private TranspositionTable transpositionTable;

    // Zaman yönetimi için değişkenler
    private long startTime;
    private long timeLimit;
    private boolean isTimeUp;

    public MinimaxEngine() {
        this.evaluator = new BoardEvaluator();
        this.validator = new MoveValidator();
        this.orderer = new MoveOrderer();
        this.transpositionTable = new TranspositionTable();
    }

    /**
     * AI'ın belirli bir süre (ms) içinde bulabildiği en derin hamleyi döndürür.
     */
    public Move findBestMove(Board board, int maxDepth, long timeLimitMs, PieceColor aiColor) {
        this.startTime = System.currentTimeMillis();
        this.timeLimit = timeLimitMs;
        this.isTimeUp = false;

        Move bestMoveFoundSoFar = null;
        List<Move> legalMoves = validator.getLegalMoves(board, aiColor);
        if (legalMoves.isEmpty()) return null;

        // --- ITERATIVE DEEPENING DÖNGÜSÜ ---
        // 1. derinlikten başla, süre bitene kadar derinliği artır
        for (int currentDepth = 1; currentDepth <= maxDepth; currentDepth++) {

            if (isTimeUp()) break;

            Move bestMoveAtThisDepth = null;
            int bestScore = (aiColor == PieceColor.WHITE) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            orderer.orderMoves(legalMoves);

            for (Move move : legalMoves) {
                Piece captured = board.makeMove(move);
                int score = alphaBeta(board, currentDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, aiColor == PieceColor.BLACK);
                board.unmakeMove(move, captured);

                if (isTimeUp()) break; // Süre bittiyse bu derinliği tamamlama

                if (aiColor == PieceColor.WHITE) {
                    if (score > bestScore) {
                        bestScore = score;
                        bestMoveAtThisDepth = move;
                    }
                } else {
                    if (score < bestScore) {
                        bestScore = score;
                        bestMoveAtThisDepth = move;
                    }
                }
            }

            // Eğer süremiz yetip bu derinliği bitirebildiysek, sonucu güncelle
            if (!isTimeUp() && bestMoveAtThisDepth != null) {
                bestMoveFoundSoFar = bestMoveAtThisDepth;
                // Bir sonraki derinlik için hamleleri sıralarken bu derinlikteki en iyiyi başa al
                legalMoves.remove(bestMoveAtThisDepth);
                legalMoves.add(0, bestMoveAtThisDepth);
            }
        }

        return bestMoveFoundSoFar;
    }

    private int alphaBeta(Board board, int depth, int alpha, int beta, boolean isMaximizing) {
        // Her 2048 düğümde bir saati kontrol et (Sürekli sistem saati sormak motoru yavaşlatır)
        if (isTimeUp()) return isMaximizing ? -20000 : 20000;

        long boardKey = board.getZobristKey();
        TranspositionTable.TTEntry entry = transpositionTable.probe(boardKey);
        if (entry != null && entry.depth >= depth) return entry.score;

        if (depth == 0) return evaluator.evaluate(board);

        PieceColor currentColor = isMaximizing ? PieceColor.WHITE : PieceColor.BLACK;
        List<Move> legalMoves = validator.getLegalMoves(board, currentColor);
        if (legalMoves.isEmpty()) return isMaximizing ? -20000 : 20000;

        orderer.orderMoves(legalMoves);

        int bestScore;
        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (Move move : legalMoves) {
                Piece captured = board.makeMove(move);
                int score = alphaBeta(board, depth - 1, alpha, beta, false);
                board.unmakeMove(move, captured);
                if (isTimeUp()) break;
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break;
            }
            bestScore = maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (Move move : legalMoves) {
                Piece captured = board.makeMove(move);
                int score = alphaBeta(board, depth - 1, alpha, beta, true);
                board.unmakeMove(move, captured);
                if (isTimeUp()) break;
                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) break;
            }
            bestScore = minScore;
        }

        if (!isTimeUp()) transpositionTable.store(boardKey, depth, bestScore);
        return bestScore;
    }

    private boolean isTimeUp() {
        if (isTimeUp) return true;
        if (System.currentTimeMillis() - startTime >= timeLimit) {
            isTimeUp = true;
            return true;
        }
        return false;
    }
}