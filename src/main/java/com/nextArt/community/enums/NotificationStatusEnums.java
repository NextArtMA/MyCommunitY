package com.nextArt.community.enums;

//已读、未读枚举
public enum NotificationStatusEnums {

    UNREAD(0),
    READ(1);

    private int status;

    NotificationStatusEnums(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
