package com.zeros.devtool.enums;

public enum  MenuTypeEnum {

    ALL("all"),

    NETWORK("network"),
    SWITCH_HOST("host"),
    CURRENT_HOST("currentHost"),

    TEXT("text"),
    CLIPBOARD_HISTORY("clipboardHistory"),

    ;

    public String getType() {
        return type;
    }

    private String type;

    MenuTypeEnum(String type) {
        this.type = type;
    }



}
