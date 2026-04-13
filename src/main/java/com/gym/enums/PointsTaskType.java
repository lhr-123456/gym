package com.gym.enums;

public enum PointsTaskType {

    SIGNIN("signin", "每日签到", 10, true),
    COMPLETE_COURSE("complete_course", "完成课程", 20, true),
    BOOKING("booking", "预约课程", 5, true),
    REVIEW("review", "评价课程", 10, true),
    PROFILE("profile", "完善资料", 50, false),
    INVITE("invite", "邀请好友", 30, false);

    private final String key;
    private final String name;
    private final int points;
    private final boolean repeatable;

    PointsTaskType(String key, String name, int points, boolean repeatable) {
        this.key = key;
        this.name = name;
        this.points = points;
        this.repeatable = repeatable;
    }

    public String getKey() { return key; }
    public String getName() { return name; }
    public int getPoints() { return points; }
    public boolean isRepeatable() { return repeatable; }

    public static PointsTaskType fromKey(String key) {
        for (PointsTaskType t : values()) {
            if (t.key.equals(key)) return t;
        }
        return null;
    }
}
