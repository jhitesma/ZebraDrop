package com.jthtml.zebraDrop;

public final class GameConstants {
    
    // Achievement IDs
    public static final String ACHIEVEMENT_SCORE_3000 = "CgkIx7_-lMMSEAIQAw";
    public static final String ACHIEVEMENT_SCORE_10000 = "CgkIx7_-lMMSEAIQBA";
    public static final String ACHIEVEMENT_BUCKET_BONUS = "CgkIx7_-lMMSEAIQBQ";
    
    // Score thresholds
    public static final int SCORE_THRESHOLD_3000 = 3000;
    public static final int SCORE_THRESHOLD_10000 = 10000;
    public static final int BONUS_THRESHOLD = 1000;
    
    // Movement speeds
    public static final int BASE_MOVEMENT_SPEED = 200;
    public static final int MOVEMENT_SPEED_RANGE = 250;
    public static final int BUCKET_MOVEMENT_SPEED = 260;
    
    // Bucket heights and dimensions
    public static final int BUCKET_STACK_HEIGHT = 84;
    public static final int BUCKET_SIZE = 64;
    public static final int DEFAULT_BUCKET_BOUNDS_HEIGHT = 212;
    
    // UI positioning
    public static final int TOUCH_SPOT_SIZE = 16;
    public static final int LINE_HEIGHT_OFFSET = 70;
    public static final int UI_MARGIN = 35;
    public static final int UI_BUTTON_SPACING = 75;
    public static final int UI_BUTTON_HEIGHT = 64;
    public static final int UI_BUTTON_WIDTH = 400;
    
    // Game mechanics
    public static final int MAX_BUCKETS = 3;
    public static final int DEFAULT_BUCKETS = 3;
    public static final int MAX_POINT_VALUE = 8;
    public static final int LEVEL_SPEED_INCREMENT = 50;
    public static final int DROPS_PER_LEVEL_BASE = 10;
    public static final int UFO_DROP_HEIGHT = 120;
    
    private GameConstants() {
        // Utility class - prevent instantiation
    }
}