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
    private MoveOrderer orderer; // YENİ EKLENDİ

    public MinimaxEngine() {
        this.evaluator = new BoardEvaluator();
        this.validator = new MoveValidator();
        this.orderer = new MoveOrderer(); // YENİ EKLENDİ
    }

    public Move findBestMove(Board board, int depth, PieceColor aiColor) {
        List<Move> legalMoves = validator.getLegalMoves(board, aiColor);
        if (legalMoves.isEmpty()) return null;

        // YENİ EKLENDİ: Hamleleri incelemeden önce sırala!
        orderer.orderMoves(legalMoves);

        Move bestMove = null;
        boolean isMaximizing = (aiColor == PieceColor.WHITE);
        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : legalMoves) {
            Piece captured = board.makeMove(move);
            int currentScore = alphaBeta(board, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, !isMaximizing);
            board.unmakeMove(move, captured);

            if (isMaximizing) {
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestMove = move;
                }
            } else {
                if (currentScore < bestScore) {
                    bestScore = currentScore;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    private int alphaBeta(Board board, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0) return evaluator.evaluate(board);

        PieceColor currentColor = isMaximizing ? PieceColor.WHITE : PieceColor.BLACK;
        List<Move> legalMoves = validator.getLegalMoves(board, currentColor);

        if (legalMoves.isEmpty()) return isMaximizing ? -20000 : 20000;

        // YENİ EKLENDİ: Ağacın alt dallarını da sırala ki budama (pruning) verimli çalışsın!
        orderer.orderMoves(legalMoves);

        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (Move move : legalMoves) {
                Piece captured = board.makeMove(move);
                int score = alphaBeta(board, depth - 1, alpha, beta, false);
                board.unmakeMove(move, captured);

                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break;
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (Move move : legalMoves) {
                Piece captured = board.makeMove(move);
                int score = alphaBeta(board, depth - 1, alpha, beta, true);
                board.unmakeMove(move, captured);

                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) break;
            }
            return minScore;
        }
    }
}