package core;

public enum GameState {
    ACTIVE,      // Oyun devam ediyor
    CHECKMATE,   // Şah Mat
    STALEMATE,   // Pat (Beraberlik)
    DRAW,        // Diğer beraberlik durumları
    CHECK        // Şah çekildi (Opsiyonel, UI bildirimi için)
}