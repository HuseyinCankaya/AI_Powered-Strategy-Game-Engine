package ai;

import entities.PieceColor;

public class EvaluationConstants {

    // Piyonlar merkeze ilerledikçe (satır endeksi küçüldükçe) çok değerlenir.
    // İlk satır ve son satır 0'dır çünkü piyon oralarda olamaz/terfi eder.
    private static final int[][] PAWN_PST = {
            {  0,  0,  0,  0,  0,  0,  0,  0},
            { 50, 50, 50, 50, 50, 50, 50, 50},
            { 10, 10, 20, 30, 30, 20, 10, 10},
            {  5,  5, 10, 25, 25, 10,  5,  5},
            {  0,  0,  0, 20, 20,  0,  0,  0},
            {  5, -5,-10,  0,  0,-10, -5,  5},
            {  5, 10, 10,-20,-20, 10, 10,  5},
            {  0,  0,  0,  0,  0,  0,  0,  0}
    };

    // Atlar merkezde devleşir, köşelerde ise "Köşedeki At, Kötü Attır" kuralı işler.
    private static final int[][] KNIGHT_PST = {
            {-50,-40,-30,-30,-30,-30,-40,-50},
            {-40,-20,  0,  0,  0,  0,-20,-40},
            {-30,  0, 10, 15, 15, 10,  0,-30},
            {-30,  5, 15, 20, 20, 15,  5,-30},
            {-30,  0, 15, 20, 20, 15,  0,-30},
            {-30,  5, 10, 15, 15, 10,  5,-30},
            {-40,-20,  0,  5,  5,  0,-20,-40},
            {-50,-40,-30,-30,-30,-30,-40,-50}
    };

    // İhtiyaca göre BISHOP_PST, ROOK_PST vb. eklenebilir. Şimdilik At ve Piyonu kullanacağız.

    /**
     * İlgili taş ve renk için o koordinattaki bonus puanı döndürür.
     */
    public static int getPositionalBonus(entities.PieceType type, PieceColor color, int row, int col) {
        int[][] pst;
        switch (type) {
            case PAWN: pst = PAWN_PST; break;
            case KNIGHT: pst = KNIGHT_PST; break;
            // Diğer taşlar eklendikçe burası genişletilecek (Fil, Kale, Vezir...)
            // default olarak konumsal bonus sıfır dönüyoruz.
            default: return 0;
        }

        // Matrisler BEYAZ taşa göre tasarlandığı için, SİYAH taşı değerlendirirken
        // tahtayı simetrik olarak ters çevirmemiz (aynalamamız) gerekir.
        int effectiveRow = (color == PieceColor.WHITE) ? row : (7 - row);

        return pst[effectiveRow][col];
    }
}