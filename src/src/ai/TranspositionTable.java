package ai;

import java.util.HashMap;

public class TranspositionTable {

    // Her Zobrist Key'e karşılık o pozisyonun özelliklerini tutan obje
    public static class TTEntry {
        public int depth; // Bu pozisyon kaç derinlikte hesaplanmış?
        public int score; // Algoritmanın bulduğu en iyi skor
        // İleride buraya "Bu pozisyondaki en iyi hamle (Best Move)" da eklenecek

        public TTEntry(int depth, int score) {
            this.depth = depth;
            this.score = score;
        }
    }

    // Devasa bir HashMap oluşturuyoruz (Hafıza)
    // Profesyonel motorlar HashMap yerine özel Array'ler kullanır ama şimdilik mantığı kavramak için mükemmeldir.
    private HashMap<Long, TTEntry> table;

    public TranspositionTable() {
        table = new HashMap<>();
    }

    public void store(long zobristKey, int depth, int score) {
        // Eğer bu pozisyon daha önce kaydedildiyse ve DAHA DERİN bir hesaplamayla bulunduysa, üzerine yazma!
        TTEntry existingEntry = table.get(zobristKey);
        if (existingEntry != null && existingEntry.depth >= depth) {
            return;
        }
        table.put(zobristKey, new TTEntry(depth, score));
    }

    public TTEntry probe(long zobristKey) {
        return table.get(zobristKey);
    }

    public void clear() {
        table.clear();
    }
}