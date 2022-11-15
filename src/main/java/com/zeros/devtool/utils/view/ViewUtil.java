package com.zeros.devtool.utils.view;

import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.enums.MenuTypeEnum;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import org.apache.commons.collections4.CollectionUtils;
import org.controlsfx.control.ToggleSwitch;

public class ViewUtil {


    public static HBox getTreeItem(String id, String text) {
        HBox hBox = new HBox();
        Label label = new Label();
        label.setTextAlignment(TextAlignment.CENTER);
        label.setId(id);
        label.setText(text);
        hBox.getChildren().add(label);
        return hBox;
    }


    public static HBox getTreeItemWithSwitch(String id, String text) {
        HBox hBox = new HBox();
        Label label = new Label();
        label.setMaxWidth(120);
        label.setPrefWidth(120);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setId(id);
        label.setText(text);
        hBox.getChildren().add(label);
        ToggleSwitch toggleSwitch = new ToggleSwitch();
        hBox.getChildren().add(toggleSwitch);

        return hBox;
    }

    public static Label getLabel(TreeItem<Node> treeItem) {
        Label label = null;
        HBox hBox = (HBox) treeItem.getValue();
        ObservableList<Node> hBoxChildren = hBox.getChildren();
        if (CollectionUtils.isNotEmpty(hBoxChildren)) {
            label = (Label) hBoxChildren.get(0);
        }
        return label;
    }

    public static ToggleSwitch getToggleSwitch(TreeItem<Node> treeItem) {
        ToggleSwitch toggleSwitch = null;
        HBox hBox = (HBox) treeItem.getValue();
        ObservableList<Node> hBoxChildren = hBox.getChildren();
        if (CollectionUtils.isNotEmpty(hBoxChildren)) {
            if (hBoxChildren.size() >= 2) {
                toggleSwitch = (ToggleSwitch) hBoxChildren.get(1);
            }
        }
        return toggleSwitch;
    }


    public static TreeItem<Node> getRootTreeItem() {
        TreeItem<Node> text = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.ALL.getType(), Constants.ALL));
        return text;
    }

    public static TreeItem<Node> getNetworkTreeItem() {
        //网络
        TreeItem<Node> network = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.NETWORK.getType(), Constants.NETWORK));
        network.setExpanded(true);
        return network;
    }

    public static TreeItem<Node> getFormatTreeItem() {
        TreeItem<Node> format = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.FORMAT.getType(), Constants.FORMAT));
        format.setExpanded(true);
        return format;
    }


    private TreeItem<Node> getTextRootTreeItem() {
        TreeItem<Node> text = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.TEXT.getType(), Constants.TEXT));
        text.setExpanded(true);
        TreeItem<Node> clipboardHistory = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.CLIPBOARD_HISTORY.getType(), Constants.CLIPBOARD_HISTORY));
        TreeItem<Node> memorandum = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.MEMORANDUM.getType(), Constants.MEMORANDUM));
        text.getChildren().add(clipboardHistory);
        text.getChildren().add(memorandum);
        return text;
    }

}
