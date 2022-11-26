package com.zeros.devtool.model.system;

import lombok.Data;

@Data
public class PortCheckInfo {

    private String port;

    private String pid;

    private String processName;

    private String protocol;

    private String status;

}
