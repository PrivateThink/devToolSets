package com.zeros.devtool.service;

import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.constants.FileConstants;
import com.zeros.devtool.controller.index.IndexController;
import com.zeros.devtool.controller.network.SwitchHostController;
import com.zeros.devtool.enums.MenuTypeEnum;
import com.zeros.devtool.utils.AtomicIntegerUtils;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.SystemUtils;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.PopupWindow;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;


public class SwitchHostService {


    private final List<String> hostTypes = new ArrayList<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    //保存host的tab页面
    private final ConcurrentHashMap<String, Tab> hostTab = new ConcurrentHashMap<>();

    //保存host文件名
    private final List<String> hostFilePath = new ArrayList<>();

    //CodeArea 与 fileName
    private final HashMap<CodeArea, String> codeAreaFile = new HashMap<>();


    //获取网络的树形菜单
    private TreeItem<Label> getNetWorkRootTreeItem() {
        //元素
        TreeItem<Label> network = new TreeItem<>(this.getLabel(MenuTypeEnum.NETWORK.getType(), Constants.NETWORK));
        network.setExpanded(true);
        TreeItem<Label> switchHost = new TreeItem<>(this.getLabel(MenuTypeEnum.SWITCH_HOST.getType(), Constants.SWITCH_HOST));
        TreeItem<Label> currentHostItem = new TreeItem<>(this.getLabel(MenuTypeEnum.CURRENT_HOST.getType(), Constants.CURRENT_HOST));
        network.getChildren().add(switchHost);
        switchHost.getChildren().add(currentHostItem);

        this.addHostType(MenuTypeEnum.CURRENT_HOST.getType());


        currentHostItem.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                //加载系统当前的host
                if (event.getButton() == MouseButton.PRIMARY) {
                    String hostFile = "";
                    if (SystemUtils.isWindows()) {
                        hostFile = FileConstants.WIN_HOST;
                    } else {
                        hostFile = FileConstants.MAC_HOST;
                    }

                    SwitchHostController switchHostController = ControllerMangerUtil.getSwitchHostController();

                    IndexController indexController = ControllerMangerUtil.getIndexController();
                    indexController.getIndexPane().setCenter(switchHostController.getTabPaneMain());


                    //添加host tab
                    CodeArea codeArea = SwitchHostService.this.addHostTab(Constants.CURRENT_HOST, switchHostController.getTabPaneMain());
                    //读取host
                    SwitchHostService.this.loadSystemHost(codeArea, hostFile);
                    //选择系统当前的host
                    SwitchHostService.this.switchHostTab(Constants.CURRENT_HOST);
                }
            }
        });

        switchHost.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (event.getButton() == MouseButton.PRIMARY) {

                    List<String> hostFileList = new ArrayList<>();

                    SwitchHostController switchHostController = ControllerMangerUtil.getSwitchHostController();

                    IndexController indexController = ControllerMangerUtil.getIndexController();
                    indexController.getIndexPane().setCenter(switchHostController.getTabPaneMain());

                    //添加host tab
                    CodeArea codeArea = SwitchHostService.this.addHostTab(Constants.CURRENT_HOST, switchHostController.getTabPaneMain());
                    //读取host
                    SwitchHostService.this.loadSystemHost(codeArea, FileConstants.WIN_HOST);

                    //选择系统当前的host
                    SwitchHostService.this.switchHostTab(Constants.CURRENT_HOST);

                    //加载保存的host文件
                    File hostPath = new File(FileConstants.HOST_PATH);
                    if (!hostPath.exists()) {
                        hostPath.mkdirs();
                    }
                    File[] hostFiles = hostPath.listFiles();
                    if (hostFiles == null || hostFiles.length == 0) {
                        return;
                    }

                    for (File hostFile : hostFiles) {
                        if (hostFile.isFile() && hostFile.getName().endsWith(FileConstants.HOST_SUFFIX)) {
                            String name = hostFile.getName().substring(0, hostFile.getName().indexOf(FileConstants.HOST_SUFFIX));
                            //添加host tab
                            CodeArea area = SwitchHostService.this.addHostTab(name, switchHostController.getTabPaneMain());
                            //读取host
                            SwitchHostService.this.loadSystemHost(area, hostFile.getAbsolutePath());
                            //保存当前系统host的codeArea和fileName
                            codeAreaFile.put(codeArea, hostFile.getAbsolutePath());
                            TreeItem<Label> switchHost = SwitchHostService.this.getSwitchHost();
                            String hostType = MenuTypeEnum.SWITCH_HOST.getType() + "_" + hostFile.getName();
                            if (!hostTypes.contains(hostType)) {
                                SwitchHostService.this.addHostType(hostType);
                                TreeItem<Label> newHostItem = new TreeItem<>(SwitchHostService.this.getLabel(hostType, name));

                                switchHost.getChildren().add(newHostItem);
                                newHostItem.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent event) {
                                        if (event.getButton() == MouseButton.PRIMARY) {
                                            //点击切换host tab
                                            SwitchHostService.this.switchHostTab(name);
                                        }
                                    }
                                });
                            }
                        }
                    }

                }
            }
        });

        return network;
    }

    private TreeItem<Label> getTextRootTreeItem() {
        TreeItem<Label> text = new TreeItem<>(this.getLabel(MenuTypeEnum.TEXT.getType(), Constants.TEXT));
        text.setExpanded(true);
        TreeItem<Label> clipboardHistory = new TreeItem<>(this.getLabel(MenuTypeEnum.CLIPBOARD_HISTORY.getType(), Constants.CLIPBOARD_HISTORY));
        text.getChildren().add(clipboardHistory);
        return text;
    }

    private TreeItem<Label> getRootTreeItem() {
        TreeItem<Label> text = new TreeItem<>(this.getLabel(MenuTypeEnum.ALL.getType(), Constants.ALL));
        return text;
    }

    //获取树形菜单
    public void loadMenu(TreeView<Label> rootTree) {
        //网络
        TreeItem<Label> network = this.getNetWorkRootTreeItem();
        //文本
        TreeItem<Label> text = this.getTextRootTreeItem();
        //全部
        TreeItem<Label> all = this.getRootTreeItem();
        all.getChildren().add(network);
        all.getChildren().add(text);
        all.setExpanded(true);
        rootTree.setRoot(all);
    }


    public Label getLabel(String id, String text) {
        Label label = new Label();
        label.setTextAlignment(TextAlignment.CENTER);
        label.setId(id);
        label.setText(text);
        return label;
    }


    public List<String> getHostTypes() {
        return hostTypes;
    }

    public synchronized void addHostType(String name) {
        if (hostTypes.contains(name)) {
            return;
        }
        hostTypes.add(name);
    }


    //初始出host显示的文本
    public void initHostArea(CodeArea hostArea) {

        hostArea.setParagraphGraphicFactory(LineNumberFactory.get(hostArea));
        URL switchHostCss = SwitchHostController.class.getResource(Constants.SWITCH_HOST_CSS);
        hostArea.getStylesheets().add(switchHostCss.toExternalForm());
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
            if (Constants.CURRENT_HOST.equals(fileName)) {
                tab.setClosable(false);
            }
            hostTab.put(fileName, tab);
            hostArea = new CodeArea();
            tab.setContent(hostArea);
            tabPaneMain.getTabs().add(tabPaneMain.getTabs().size() - 1, tab);
            tabPaneMain.getSelectionModel().select(tabPaneMain.getTabs().size() - 2);
            this.initHostArea(hostArea);
            tab.setOnClosed(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    //关闭
                    String tabText = tab.getText();
                    TreeItem<Label> switchHost = getSwitchHost();
                    if (!Constants.CURRENT_HOST.equals(tabText)) {
                        hostTab.remove(tabText);
                        switchHost.getChildren().removeIf(host -> tabText.equals(host.getValue().getText()));
                    }
                }
            });
        } else {
            tab = hostTab.get(fileName);
            hostArea = (CodeArea) tab.getContent();
        }

        //保存codeArea和fileName
        String hostPath;
        if (FileConstants.WIN_HOST.equals(fileName)) {
            hostPath = FileConstants.WIN_HOST;
        } else {
            hostPath = FileConstants.HOST_PATH + File.separator + fileName + FileConstants.HOST_SUFFIX;
        }
        codeAreaFile.put(hostArea, hostPath);
        return hostArea;
    }

    public Tab getHostTab(String text) {
        Tab tab = hostTab.get(text);
        return tab;
    }

    public void switchHostTab(String text) {
        Tab tab = this.getHostTab(text);
        if (tab != null) {
            //切换host tab
            SwitchHostController switchHostController = ControllerMangerUtil.getSwitchHostController();
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

    public TreeItem<Label> getSwitchHost() {
        IndexController controller = (IndexController) ControllerMangerUtil.getController(IndexController.class.getName());
        TreeItem<Label> root = controller.getRootTree().getRoot();
        TreeItem<Label> network = root.getChildren().get(0);
        TreeItem<Label> switchHost = network.getChildren().get(0);
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

    public void handleAddMenuItemEvent(TabPane tabPaneMain) {
        int index = AtomicIntegerUtils.getAndIncrement();
        String name = "新文件" + index;
        SwitchHostService.this.addHostTab(name, tabPaneMain);
        String hostType = MenuTypeEnum.SWITCH_HOST.getType() + "_" + index;
        SwitchHostService.this.addHostType(hostType);
        TreeItem<Label> newHostItem = new TreeItem<>(SwitchHostService.this.getLabel(hostType, name));
        TreeItem<Label> switchHost = SwitchHostService.this.getSwitchHost();
        switchHost.getChildren().add(newHostItem);
        newHostItem.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    //点击切换host tab
                    SwitchHostService.this.switchHostTab(name);
                }
            }
        });
    }

    private void handleCloseMenuItemEvent(TabPane tabPaneMain) {
        TreeItem<Label> switchHost = SwitchHostService.this.getSwitchHost();
        Iterator<Tab> iterator = tabPaneMain.getTabs().iterator();
        while (iterator.hasNext()) {
            Tab tab = iterator.next();
            if (tab.selectedProperty().getValue()) {
                String tabText = tab.getText();
                //系统当前host不能关闭,添加host按钮也不能关闭
                if (!Constants.CURRENT_HOST.equals(tabText) && !Constants.ADD_HOST.equals(tabText)) {
                    iterator.remove();
                    SwitchHostService.this.getHostTypes().remove(tabText);
                    switchHost.getChildren().removeIf(host -> tabText.equals(host.getValue().getText()));
                    break;
                }
            }
        }
    }

    private void handleCloseAllMenuItemEvent(TabPane tabPaneMain) {
        TreeItem<Label> switchHost = SwitchHostService.this.getSwitchHost();
        Iterator<Tab> iterator = tabPaneMain.getTabs().iterator();
        while (iterator.hasNext()) {
            Tab tab = iterator.next();
            String tabText = tab.getText();
            if (!Constants.CURRENT_HOST.equals(tabText) && !Constants.ADD_HOST.equals(tabText)) {
                iterator.remove();
                switchHost.getChildren().removeIf(host -> tabText.equals(host.getValue().getText()));
                SwitchHostService.this.getHostTypes().remove(tabText);
            }
        }
    }

    private void handleCloseOtherMenuItemEvent(TabPane tabPaneMain) {
        TreeItem<Label> switchHost = SwitchHostService.this.getSwitchHost();
        Iterator<Tab> iterator = tabPaneMain.getTabs().iterator();
        while (iterator.hasNext()) {
            Tab tab = iterator.next();
            String tabText = tab.getText();
            if (!tab.selectedProperty().getValue() && !Constants.ADD_HOST.equals(tabText)) {
                if (!Constants.CURRENT_HOST.equals(tabText)) {
                    SwitchHostService.this.getHostTypes().remove(tabText);
                    switchHost.getChildren().removeIf(host -> tabText.equals(host.getValue().getText()));
                }
                iterator.remove();
            }
        }
    }

    public void setMenuItemVisible(TabPane tabPaneMain) {

        //如果只剩下当前系统页面和添加host页面，则隐藏菜单
        ContextMenu contextMenu = tabPaneMain.getContextMenu();
        if (tabPaneMain.getTabs().size() <= 2) {
            for (MenuItem item : contextMenu.getItems()) {
                item.setVisible(false);
            }
        } else {
            for (MenuItem item : contextMenu.getItems()) {
                item.setVisible(true);
            }
        }
    }
}
