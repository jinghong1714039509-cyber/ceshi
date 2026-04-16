package com.rabbiter.association.auth;

public enum AuthRole {
    SUPER_ADMIN(0, "super-admin"),
    CLUB_ADMIN(1, "club-admin"),
    STUDENT(2, "student"),
    TEACHER(3, "teacher-admin");

    private final int legacyType;
    private final String code;

    AuthRole(int legacyType, String code) {
        this.legacyType = legacyType;
        this.code = code;
    }

    public int getLegacyType() {
        return legacyType;
    }

    public String getCode() {
        return code;
    }

    public static AuthRole fromType(Integer type) {
        if (type == null) {
            return STUDENT;
        }
        for (AuthRole role : values()) {
            if (role.legacyType == type) {
                return role;
            }
        }
        return STUDENT;
    }
}
