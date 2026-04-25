package config;

public class GameConfig {
    private int maxSearchDepth;
    private long maxThinkTimeMs;



    public GameConfig(int maxSearchDepth, long maxThinkTimeMs){
        this.maxSearchDepth = maxSearchDepth;
        this.maxThinkTimeMs = maxThinkTimeMs;
    }
    public int getMaxSearchDepth() {
        return maxSearchDepth;
    }

    public void setMaxSearchDepth(int maxSearchDepth) {
        this.maxSearchDepth = maxSearchDepth;
    }

    public long getMaxThinkTimeMs() {
        return maxThinkTimeMs;
    }

    public void setMaxThinkTimeMs(long maxThinkTimeMs) {
        this.maxThinkTimeMs = maxThinkTimeMs;
    }
}
