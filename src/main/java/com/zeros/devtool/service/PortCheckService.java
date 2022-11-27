package com.zeros.devtool.service;

import cn.hutool.core.util.RuntimeUtil;
import com.zeros.devtool.constants.CmdConstants;
import com.zeros.devtool.model.system.PortCheckInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PortCheckService {

    public List<PortCheckInfo> getPortCheckInfos() {

        List<PortCheckInfo> infos = new ArrayList<>();
        List<String> portInfos = RuntimeUtil.execForLines(CmdConstants.PORT_LIST);
        if (CollectionUtils.isEmpty(portInfos)) {
            return infos;
        }
        Map<String, String> process = getProcess();
        for (String info : portInfos) {
            info = info.trim();
            if (info.toLowerCase().startsWith("tcp") || info.startsWith("udp")) {
                String[] infoSplit = info.split("\\s+");
                PortCheckInfo portCheckInfo = new PortCheckInfo();
                portCheckInfo.setProtocol(infoSplit[0]);
                portCheckInfo.setPort(infoSplit[1].split(":")[1]);
                portCheckInfo.setStatus(infoSplit[3]);
                portCheckInfo.setPid(infoSplit[4]);
                portCheckInfo.setProcessName(process.getOrDefault(portCheckInfo.getPid(), "-"));
                infos.add(portCheckInfo);
            }
        }
        infos = infos.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                -> new TreeSet<>(Comparator.comparing(PortCheckInfo::getPid))), ArrayList::new));
        return infos;
    }

    private Map<String, String> getProcess() {
        Map<String, String> processMap = new HashMap<>();
        List<String> taskList = RuntimeUtil.execForLines(CmdConstants.TASK_LIST);
        if (CollectionUtils.isEmpty(taskList)) {
            return processMap;
        }
        for (String task : taskList) {
            if (StringUtils.isBlank(task)) {
                continue;
            }
            String[] taskSpilt = task.split("\\s+");
            if (taskList == null || taskList.size() < 2) {
                continue;
            }

            processMap.put(taskSpilt[1], taskSpilt[0]);

        }
        return processMap;
    }

    public void kill(String pid) {
        RuntimeUtil.execForStr(CmdConstants.KILL_OPERATOR + pid);
    }

}
