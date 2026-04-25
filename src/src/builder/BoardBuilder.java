package builder;

import core.Board;
import entities.*;

public class BoardBuilder {

    /**
     * Verilen FEN string'inin sadece dizilim kısmını okuyarak Board nesnesini doldurur.
     */
    public static Board buildFromFEN(String fen) {
        Board board = new Board();

        // FEN formatı boşluklarla ayrılır. İlk kısım "rnbqkbnr/pppppppp/..." tahta dizilimidir.
        String[] fenParts = fen.split(" ");
        String boardPlacement = fenParts[0];

        int row = 0;
        int col = 0;

        for (char c : boardPlacement.toCharArray()) {
            if (c == '/') {
                // '/' karakteri alt satıra geç anlamına gelir
                row++;
                col = 0;
            } else if (Character.isDigit(c)) {
                // Rakamlar, o kadar karenin boş olduğu anlamına gelir
                col += Character.getNumericValue(c);
            } else {
                // Harf büyükse Beyaz, küçükse Siyah taştır
                PieceColor color = Character.isUpperCase(c) ? PieceColor.WHITE : PieceColor.BLACK;
                Piece piece = createPieceFromChar(Character.toLowerCase(c), color);

                if (piece != null) {
                    board.setPiece(row, col, piece);
                }
                col++;
            }
        }
        return board;
    }

    // Harfe karşılık gelen taş nesnesini üreten fabrika (Factory) metodu
    private static Piece createPieceFromChar(char c, PieceColor color) {
        switch (c) {
            case 'p': return new Pawn(color);
            case 'n': return new Knight(color);
            case 'b': return new Bishop(color);
            case 'r': return new Rook(color);
            case 'q': return new Queen(color);
            case 'k': return new King(color);
            default: return null; // Şimdilik tanımlı olmayan taşlar için boşluk bırak
        }
    }
}
