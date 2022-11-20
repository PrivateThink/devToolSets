package com.zeros.devtool.enums;

public enum  MenuTypeEnum {

    ALL("all"),

    NETWORK("network"),
    SWITCH_HOST("host"),
    CURRENT_HOST("currentHost"),

    TEXT("text"),
    CLIPBOARD_HISTORY("clipboardHistory"),
    MEMORANDUM("MEMORANDUM"),


    FORMAT("format"),
    JSON_FORMAT("jsonFormat"),


    SYSTEM("system"),
    PORT_CHECK("portCheck")
    ;

    public String getType() {
        return type;
    }

    private String type;

    MenuTypeEnum(String type) {
        this.type = type;
    }



}
