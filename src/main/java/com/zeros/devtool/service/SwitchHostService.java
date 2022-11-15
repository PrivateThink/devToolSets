package com.zeros.devtool.service;

import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.constants.CssConstants;
import com.zeros.devtool.constants.FileConstants;
import com.zeros.devtool.constants.FxmlConstant;
import com.zeros.devtool.controller.index.IndexController;
import com.zeros.devtool.controller.network.SwitchHostController;
import com.zeros.devtool.enums.MenuTypeEnum;
import com.zeros.devtool.utils.*;
import com.zeros.devtool.utils.view.ViewUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.ToggleSwitch;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;


public class SwitchHostService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    //保存host的tab页面
    private static final ConcurrentHashMap<String, Tab> hostTab = new ConcurrentHashMap<>();

    //CodeArea 与 fileName
    private static final HashMap<CodeArea, String> codeAreaFile = new HashMap<>();

    private static final List<String> hostFileName = new ArrayList<>();


    //获取host的树形菜单
    public TreeItem<Node> getHostRootTreeItem() {

        //切换host
        TreeItem<Node> switchHost = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.SWITCH_HOST.getType(), Constants.SWITCH_HOST));
        //系统当前的host
        TreeItem<Node> currentHostItem = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.CURRENT_HOST.getType(), Constants.CURRENT_HOST));


        currentHostItem.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                //加载系统当前的host
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    String hostFile = "";
                    if (SystemUtils.isWindows()) {
                        hostFile = FileConstants.WIN_HOST;
                    } else {
                        hostFile = FileConstants.MAC_HOST;
                    }
                    //设置为tabPane
                    SwitchHostController switchHostController = ControllerMangerUtil.getSwitchHostController();
                    IndexController indexController = ControllerMangerUtil.getIndexController();
                    indexController.getIndexPane().setCenter(switchHostController.getTabPaneMain());


                    //添加host tab
                    CodeArea codeArea = addHostTab(Constants.CURRENT_HOST, switchHostController.getTabPaneMain());
                    //读取host
                    loadSystemHost(codeArea, hostFile);
                    //选择系统当前的host
                    switchHostTab(Constants.CURRENT_HOST);
                }
            }
        });

        switchHost.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    ObservableList<TreeItem<Node>> treeItems = switchHost.getChildren();
                    if (!treeItems.contains(currentHostItem)) {
                        switchHost.getChildren().add(currentHostItem);
                    }
                    loadHost(switchHost);
                }
            }
        });


        return switchHost;
    }

    public void loadHost(TreeItem<Node> switchHost) {
        //设置为tabPane
        SwitchHostController switchHostController = ControllerMangerUtil.getSwitchHostController();
        IndexController indexController = ControllerMangerUtil.getIndexController();
        indexController.getIndexPane().setCenter(switchHostController.getTabPaneMain());

        //添加host tab
        CodeArea codeArea = addHostTab(Constants.CURRENT_HOST, switchHostController.getTabPaneMain());
        //读取host
        loadSystemHost(codeArea, FileConstants.WIN_HOST);

        //选择系统当前的host
        switchHostTab(Constants.CURRENT_HOST);

        //加载保存的host文件
        File hostPath = new File(FileConstants.HOST_PATH);
        if (!hostPath.exists()) {
            hostPath.mkdirs();
        }
        File[] hostFiles = hostPath.listFiles();
        if (hostFiles == null || hostFiles.length == 0) {
            return;
        }

        //加载host文件
        for (File hostFile : hostFiles) {
            if (hostFile.isFile() && hostFile.getName().endsWith(FileConstants.HOST_SUFFIX)) {
                String name = hostFile.getName().substring(0, hostFile.getName().indexOf(FileConstants.HOST_SUFFIX));
                //添加host tab
                CodeArea area = addHostTab(name, switchHostController.getTabPaneMain());
                //读取host
                loadSystemHost(area, hostFile.getAbsolutePath());
                //保存当前系统host的codeArea和fileName
                codeAreaFile.put(area, hostFile.getAbsolutePath());
                String hostType = MenuTypeEnum.SWITCH_HOST.getType() + "_" + hostFile.getName();
                if (!existItem(switchHost, name)) {
                    TreeItem<Node> newHostItem = new TreeItem<>(ViewUtil.getTreeItemWithSwitch(hostType, name));
                    switchHost.getChildren().add(newHostItem);
                    newHostItem.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if (event.getButton() == MouseButton.PRIMARY) {
                                //点击切换host tab
                                switchHostTab(name);
                            }
                        }
                    });
                    //添加菜单
                    Label hostLabel = ViewUtil.getLabel(newHostItem);
                    setHostItemMenu(hostLabel);

                    //设置切换按钮事件
                    ToggleSwitch toggleSwitch = ViewUtil.getToggleSwitch(newHostItem);

                    toggleSwitch.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldSelect, Boolean newSelect) {
                            setToggleSwitchEvent(newSelect, switchHost, toggleSwitch);
                        }
                    });
                }
            }
        }

        //设置添加菜单
        setHostFileMenu(switchHost);
    }

    private void setToggleSwitchEvent(Boolean newSelect, TreeItem<Node> switchHost, ToggleSwitch toggleSwitch) {
        ObservableList<TreeItem<Node>> items = switchHost.getChildren();
        if (CollectionUtils.isNotEmpty(items)) {
            for (TreeItem<Node> item : items) {
                Label label = ViewUtil.getLabel(item);
                ToggleSwitch itemSwitch = ViewUtil.getToggleSwitch(item);
                //如果toggleSwitch是被选中的，则将其他切换为未被选中选中
                if (itemSwitch != null && !itemSwitch.equals(toggleSwitch) && newSelect) {
                    itemSwitch.setSelected(false);
                }
                //将该host保存为系统当前的host
                if (itemSwitch != null && itemSwitch.equals(toggleSwitch) && newSelect) {
                    String hostFile = FileConstants.HOST_PATH + File.separator + label.getText() + FileConstants.HOST_SUFFIX;
                    String codeAreaText = getCodeAreaTextWithFileName(hostFile);
                    try {
                        FileUtils.writeStringToFile(new File(FileConstants.WIN_HOST), codeAreaText, "UTF-8");
                        FileUtils.writeStringToFile(new File(hostFile), codeAreaText, "UTF-8");
                        ToastUtil.toast("host保存成功", 2000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private void setHostFileMenu(TreeItem<Node> switchHost) {
        Label label = ViewUtil.getLabel(switchHost);
        if (label.getContextMenu() != null) {
            return;
        }
        ContextMenu switchHostMenu = new ContextMenu();
        MenuItem addItem = new MenuItem("添加");
        switchHostMenu.getItems().add(addItem);
        addItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addOrEditHostFileEvent(true);
            }
        });
        label.setContextMenu(switchHostMenu);
    }

    private void addOrEditHostFileEvent(boolean add) {
        FXMLLoader hostFileNameLoader = FXMLLoaderUtils.getFXMLLoader(FxmlConstant.HOST_FILE_NAME);
        Stage hostFileStage = new Stage();
        try {
            hostFileStage.setScene(new Scene(hostFileNameLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        hostFileStage.initStyle(StageStyle.UTILITY);
        hostFileStage.show();

        GridPane hostFileGridPane = hostFileNameLoader.getRoot();

        TextField fileNameTextField = (TextField) hostFileGridPane.getChildren().get(1);

        Button submit = (Button) hostFileGridPane.getChildren().get(2);

        Button cancel = (Button) hostFileGridPane.getChildren().get(3);


        submit.setOnMouseClicked(e -> {
            String fileName = fileNameTextField.getText();
            if (StringUtils.isEmpty(fileName)) {
                ToastUtil.toast("文件名称不能为空", 2000);
                return;
            }

            if (hostFileNameExist(fileName)) {
                ToastUtil.toast("文件名称已经存在", 2000);
                return;
            }

            if (add) {
                addHostAndFile(fileNameTextField.getText());
                hostFileStage.close();
                ToastUtil.toast("保存文件成功", 2000);
            } else {
                ToastUtil.toast("修改文件名称成功", 2000);
            }

        });

        cancel.setOnMouseClicked(cancelEvent -> {
            hostFileStage.close();
        });
    }


    //初始出host显示的文本
    public void initHostArea(CodeArea hostArea) {

        hostArea.setParagraphGraphicFactory(LineNumberFactory.get(hostArea));
        hostArea.getStylesheets().add(CssLoadUtil.getResourceUrl(CssConstants.SWITCH_HOST_CSS));
        hostArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(new Supplier<Task<StyleSpans<Collection<String>>>>() {
                    @Override
                    public Task<StyleSpans<Collection<String>>> get() {
                        String text = hostArea.getText();
                        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
                            @Override
                            protected StyleSpans<Collection<String>> call() throws Exception {
                                return computeHighlighting(text);
                            }
                        };
                        executor.execute(task);
                        return task;
                    }
                })
                .awaitLatest(hostArea.richChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(new Consumer<StyleSpans<Collection<String>>>() {
                    @Override
                    public void accept(StyleSpans<Collection<String>> highlighting) {
                        hostArea.setStyleSpans(0, highlighting);
                    }
                });

        hostArea.setOnKeyPressed(event -> {
            //快捷键 ctrl + s
            if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(event)) {
                try {
                    FileUtils.writeStringToFile(new File(codeAreaFile.get(hostArea)), hostArea.getText(), "UTF-8");
                    ToastUtil.toast("保存host成功", 2000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = Constants.PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("COMMENT") != null ? "comment" :
                                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public CodeArea addHostTab(String fileName, TabPane tabPaneMain) {
        Tab tab;
        CodeArea hostArea;
        //如果不存在host tab，则新建并缓存
        if (!hostTableExist(fileName)) {
            tab = new Tab(fileName);
            hostTab.put(fileName, tab);
            hostArea = new CodeArea();
            tab.setContent(hostArea);
            tabPaneMain.getTabs().add(tab);
            tabPaneMain.getSelectionModel().select(tab);
            this.initHostArea(hostArea);
        } else {
            tab = hostTab.get(fileName);
            hostArea = (CodeArea) tab.getContent();
        }

        //保存codeArea和fileName
        String hostPath;
        if (Constants.CURRENT_HOST.equals(fileName)) {
            hostPath = FileConstants.WIN_HOST;
        } else {
            hostPath = FileConstants.HOST_PATH + File.separator + fileName + FileConstants.HOST_SUFFIX;
        }
        codeAreaFile.put(hostArea, hostPath);
        //添加host名字
        addHostFileName(fileName);
        return hostArea;
    }

    public Tab getHostTab(String text) {
        Tab tab = hostTab.get(text);
        return tab;
    }

    public void switchHostTab(String text) {
        Tab tab = this.getHostTab(text);
        if (tab != null) {
            SwitchHostController switchHostController = ControllerMangerUtil.getSwitchHostController();
            //设置为tabPane
            IndexController indexController = ControllerMangerUtil.getIndexController();
            indexController.getIndexPane().setCenter(switchHostController.getTabPaneMain());
            //切换host tab
            if (!hostTableExist(text)) {
                switchHostController.getTabPaneMain().getTabs().add(tab);
            }

            switchHostController.getTabPaneMain().getSelectionModel().select(tab);
        }
    }

    public void loadSystemHost(CodeArea hostArea, String fileName) {
        loadHost(hostArea, SystemUtils.getHostContent(fileName));
    }

    public void loadHost(CodeArea hostArea, String hostContent) {
        hostArea.clear();
        hostArea.replaceText(hostContent);
    }

    public boolean hostTableExist(String text) {
        SwitchHostController switchHostController = ControllerMangerUtil.getSwitchHostController();
        ObservableList<Tab> tabs = switchHostController.getTabPaneMain().getTabs();
        if (CollectionUtils.isNotEmpty(tabs)) {
            for (Tab tab : tabs) {
                if (tab.getText().equals(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    public TreeItem<Node> getSwitchHost() {
        IndexController controller = (IndexController) ControllerMangerUtil.getController(IndexController.class.getName());
        TreeItem<Node> root = controller.getRootTree().getRoot();
        TreeItem<Node> network = root.getChildren().get(0);
        TreeItem<Node> switchHost = network.getChildren().get(0);
        return switchHost;
    }

    public void handleTabPaneEvent(TabPane tabPaneMain) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem closeMenuItem = new MenuItem("关闭选中");
        MenuItem closeAllMenuItem = new MenuItem("关闭所有");
        MenuItem closeOtherMenuItem = new MenuItem("关闭其他");
        contextMenu.getItems().add(closeMenuItem);
        contextMenu.getItems().add(closeAllMenuItem);
        contextMenu.getItems().add(closeOtherMenuItem);

        //关闭选中监听
        closeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleCloseMenuItemEvent(tabPaneMain);
            }
        });

        //关闭全部
        closeAllMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleCloseAllMenuItemEvent(tabPaneMain);
            }
        });

        //关闭其他监听
        closeOtherMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleCloseOtherMenuItemEvent(tabPaneMain);
            }

        });

        tabPaneMain.setContextMenu(contextMenu);
    }

    public void addHostAndFile(String fileName) {
        int index = AtomicIntegerUtils.getAndIncrement();
        SwitchHostController switchHostController = ControllerMangerUtil.getSwitchHostController();
        addHostTab(fileName, switchHostController.getTabPaneMain());
        String hostType = MenuTypeEnum.SWITCH_HOST.getType() + "_" + index;
        TreeItem<Node> newHostItem = new TreeItem<>(ViewUtil.getTreeItemWithSwitch(hostType, fileName));
        TreeItem<Node> switchHost = this.getSwitchHost();
        switchHost.getChildren().add(newHostItem);
        newHostItem.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    //点击切换host tab
                    switchHostTab(fileName);
                }
            }
        });

        //添加host切换事件
        ToggleSwitch toggleSwitch = ViewUtil.getToggleSwitch(newHostItem);
        toggleSwitch.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldSelect, Boolean newSelect) {
                setToggleSwitchEvent(newSelect, switchHost, toggleSwitch);
            }
        });


        //写文件
        String hostFile = FileConstants.HOST_PATH + File.separator + fileName + FileConstants.HOST_SUFFIX;
        try {
            FileUtils.writeStringToFile(new File(hostFile), "", "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //添加菜单
        Label label = ViewUtil.getLabel(newHostItem);
        setHostItemMenu(label);
    }

    private void handleCloseMenuItemEvent(TabPane tabPaneMain) {
        tabPaneMain.getTabs().removeIf(tab -> tab.selectedProperty().getValue());
    }

    private void handleCloseAllMenuItemEvent(TabPane tabPaneMain) {
        tabPaneMain.getTabs().clear();
    }

    private void handleCloseOtherMenuItemEvent(TabPane tabPaneMain) {
        tabPaneMain.getTabs().removeIf(tab -> !tab.selectedProperty().getValue());
    }

    public void setMenuItemVisible(TabPane tabPaneMain) {

        //如果只剩下当前系统页面和添加host页面，则隐藏菜单
        ContextMenu contextMenu = tabPaneMain.getContextMenu();
        if (tabPaneMain.getTabs().size() <= 0) {
            for (MenuItem item : contextMenu.getItems()) {
                item.setVisible(false);
            }
        } else {
            for (MenuItem item : contextMenu.getItems()) {
                item.setVisible(true);
            }
        }
    }

    public boolean existItem(TreeItem<Node> treeItem, String name) {

        if (treeItem == null || treeItem.getChildren() == null) {
            return false;
        }
        ObservableList<TreeItem<Node>> items = treeItem.getChildren();
        for (TreeItem<Node> item : items) {
            Label label = ViewUtil.getLabel(item);
            if (label.getText().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean hostFileNameExist(String fileName) {
        for (String name : hostFileName) {
            if (name.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public void addHostFileName(String fileName) {
        if (hostFileNameExist(fileName)) {
            return;
        }
        hostFileName.add(fileName);
    }

    public void setHostItemMenu(Label label) {
        ContextMenu menu = new ContextMenu();
        //MenuItem updateItem = new MenuItem("修改名字");
        MenuItem deleteItem = new MenuItem("删除");
        menu.getItems().addAll(deleteItem);
//        updateItem.setOnAction(event ->{
//            addOrEditHostFileEvent(false);
//        });


        deleteItem.setOnAction(event -> {
            //删除tab
            SwitchHostController hostController = ControllerMangerUtil.getSwitchHostController();
            Tab tab = hostTab.remove(label.getText());
            hostController.getTabPaneMain().getTabs().remove(tab);
            hostFileName.remove(label.getText());
            deleteHostItem(label.getText());
            //删除文件
            String fileName = FileConstants.HOST_PATH + File.separator + label.getText() + FileConstants.HOST_SUFFIX;
            try {
                FileUtils.forceDelete(new File(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        label.setContextMenu(menu);
    }

    private void deleteHostItem(String text) {
        TreeItem<Node> switchHost = getSwitchHost();
        ObservableList<TreeItem<Node>> items = switchHost.getChildren();
        if (CollectionUtils.isNotEmpty(items)) {
            items.removeIf(treeItem -> {
                Label label = ViewUtil.getLabel(treeItem);
                return label.getText().equals(text);
            });
        }
    }


    private String getCodeAreaTextWithFileName(String fileName) {
        Collection<CodeArea> codeAreas = codeAreaFile.keySet();
        if (CollectionUtils.isNotEmpty(codeAreas)) {
            for (CodeArea codeArea : codeAreas) {
                if (codeAreaFile.get(codeArea).equals(fileName)) {
                    return codeArea.getText();
                }
            }
        }
        return "";
    }


    public void setIndexPaneCenter(){
        SwitchHostController switchHostController = ControllerMangerUtil.getSwitchHostController();
        IndexController indexController = ControllerMangerUtil.getIndexController();
        indexController.getIndexPane().setCenter(switchHostController.getTabPaneMain());
    }
}
