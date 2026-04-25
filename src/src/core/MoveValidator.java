package core;

import entities.*;
import java.util.ArrayList;
import java.util.List;

public class MoveValidator {

    public List<Move> getLegalMoves(Board board, PieceColor color) {
        List<Move> pseudoLegalMoves = new ArrayList<>();
        List<Move> strictLegalMoves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.getColor() == color) {
                    pseudoLegalMoves.addAll(piece.calculateMoves(board, r, c));
                }
            }
        }

        for (Move move : pseudoLegalMoves) {
            // --- ROK İÇİN ÖZEL GÜVENLİK KURALLARI ---
            if (move.isCastling()) {
                int[] kingPos = findKingPosition(board, color);

                // 1. Şah halihazırda tehdit altında olamaz
                if (kingPos != null && isSquareAttacked(board, kingPos[0], kingPos[1], color.opposite())) {
                    continue;
                }

                // 2. Şahın atlayacağı ara kare tehdit altında olamaz
                int passCol = (move.getToCol() == 6) ? 5 : 3;
                if (isSquareAttacked(board, move.getFromRow(), passCol, color.opposite())) {
                    continue;
                }
            }

            Piece captured = board.makeMove(move);
            int[] kingPos = findKingPosition(board, color);

            if (kingPos != null && !isSquareAttacked(board, kingPos[0], kingPos[1], color.opposite())) {
                strictLegalMoves.add(move);
            }

            board.unmakeMove(move, captured);
        }

        return strictLegalMoves;
    }

    private int[] findKingPosition(Board board, PieceColor color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getType() == PieceType.KING && p.getColor() == color) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    public boolean isSquareAttacked(Board board, int targetRow, int targetCol, PieceColor attackerColor) {
        int pawnDir = (attackerColor == PieceColor.WHITE) ? 1 : -1;
        int[][] pawnAttacks = {{pawnDir, -1}, {pawnDir, 1}};
        for (int[] offset : pawnAttacks) {
            int r = targetRow + offset[0];
            int c = targetCol + offset[1];
            if (isInside(r, c)) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.PAWN) return true;
            }
        }

        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] offset : knightMoves) {
            int r = targetRow + offset[0];
            int c = targetCol + offset[1];
            if (isInside(r, c)) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.KNIGHT) return true;
            }
        }

        int[][] straightDirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        if (checkRayAttacks(board, targetRow, targetCol, attackerColor, straightDirs, PieceType.ROOK, PieceType.QUEEN)) return true;

        int[][] diagDirs = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        if (checkRayAttacks(board, targetRow, targetCol, attackerColor, diagDirs, PieceType.BISHOP, PieceType.QUEEN)) return true;

        int[][] kingMoves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] offset : kingMoves) {
            int r = targetRow + offset[0];
            int c = targetCol + offset[1];
            if (isInside(r, c)) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.KING) return true;
            }
        }

        return false;
    }

    private boolean checkRayAttacks(Board board, int startRow, int startCol, PieceColor attackerColor, int[][] directions, PieceType type1, PieceType type2) {
        for (int[] dir : directions) {
            int r = startRow + dir[0];
            int c = startCol + dir[1];
            while (isInside(r, c)) {
                Piece p = board.getPiece(r, c);
                if (p != null) {
                    if (p.getColor() == attackerColor && (p.getType() == type1 || p.getType() == type2)) return true;
                    break;
                }
                r += dir[0];
                c += dir[1];
            }
        }
        return false;
    }

    private boolean isInside(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}