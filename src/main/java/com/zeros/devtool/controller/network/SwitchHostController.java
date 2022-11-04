package com.zeros.devtool.controller.network;

import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.controller.index.IndexController;
import com.zeros.devtool.enums.MenuTypeEnum;
import com.zeros.devtool.service.MenuService;
import com.zeros.devtool.utils.AtomicIntegerUtils;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.SystemUtils;
import com.zeros.devtool.view.network.SwitchHostView;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.apache.commons.collections4.CollectionUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;

public class SwitchHostController extends SwitchHostView {

    private final MenuService menuService = new MenuService();


    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ConcurrentHashMap<String, Tab> hostTab = new ConcurrentHashMap<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initService();
        initEvent();
    }

    private void initView() {
    }


    private void initEvent() {

        tabPaneMain.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MouseButton button = event.getButton();
                //右击显示菜单
                if (button == MouseButton.SECONDARY) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem addMenuItem = new MenuItem("添加");
                    MenuItem closeMenuItem = new MenuItem("关闭选中");
                    MenuItem closeAllMenuItem = new MenuItem("关闭所有");
                    MenuItem closeOtherMenuItem = new MenuItem("关闭其他");
                    contextMenu.getItems().add(addMenuItem);
                    contextMenu.getItems().add(closeMenuItem);
                    contextMenu.getItems().add(closeAllMenuItem);
                    contextMenu.getItems().add(closeOtherMenuItem);

                    //添加监听
                    addMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            int index = AtomicIntegerUtils.getAndIncrement();
                            String name = "新文件" + index;
                            SwitchHostController.this.addHostTab(name);
                            String hostType = MenuTypeEnum.SWITCH_HOST.getType() + "_" + index;
                            menuService.addHostType(hostType);
                            TreeItem<Label> newHostItem = new TreeItem<>(menuService.getLabel(hostType, name));
                            TreeItem<Label> switchHost = getSwitchHost();
                            switchHost.getChildren().add(newHostItem);
                        }
                    });

                    //关闭选中监听
                    closeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            TreeItem<Label> switchHost = getSwitchHost();
                            Iterator<Tab> iterator = tabPaneMain.getTabs().iterator();
                            while (iterator.hasNext()) {
                                Tab tab = iterator.next();
                                if (tab.selectedProperty().getValue()) {
                                    String tabText = tab.getText();
                                    iterator.remove();
                                    if (!Constants.CURRENT_HOST.equals(tabText)) {
                                        hostTab.remove(tabText);
                                        switchHost.getChildren().removeIf(host -> tabText.equals(host.getValue().getText()));
                                        break;
                                    }
                                }
                            }
                        }
                    });

                    //关闭全部
                    closeAllMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            TreeItem<Label> switchHost = getSwitchHost();
                            Iterator<Tab> iterator = tabPaneMain.getTabs().iterator();
                            while (iterator.hasNext()) {
                                Tab tab = iterator.next();
                                String tabText = tab.getText();
                                if (!Constants.CURRENT_HOST.equals(tabText)){
                                    switchHost.getChildren().removeIf(host -> tabText.equals(host.getValue().getText()));
                                    hostTab.remove(tabText);
                                }
                                iterator.remove();
                            }
                        }
                    });

                    //关闭其他监听
                    closeOtherMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            TreeItem<Label> switchHost = getSwitchHost();
                            Iterator<Tab> iterator = tabPaneMain.getTabs().iterator();
                            while (iterator.hasNext()) {
                                Tab tab = iterator.next();
                                String tabText = tab.getText();
                                if (!tab.selectedProperty().getValue()) {
                                    if (!Constants.CURRENT_HOST.equals(tabText)){
                                        hostTab.remove(tabText);
                                        switchHost.getChildren().removeIf(host -> tabText.equals(host.getValue().getText()));
                                    }
                                    iterator.remove();
                                }
                            }
                        }

                    });

                    tabPaneMain.setContextMenu(contextMenu);
                }
            }
        });
    }

    public TreeItem<Label> getSwitchHost(){
        IndexController controller = (IndexController) ControllerMangerUtil.getController(IndexController.class.getName());
        TreeItem<Label> root = controller.getRootTree().getRoot();
        TreeItem<Label> network = root.getChildren().get(0);
        TreeItem<Label> switchHost = network.getChildren().get(0);
        return switchHost;
    }

    private void initService() {
        ControllerMangerUtil.setController(SwitchHostController.this.getClass().getName(), this);
    }

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

    public CodeArea addHostTab(String text) {
        Tab tab;
        CodeArea hostArea;
        //如果不存在host tab，则新建并缓存
        if (!hostTableExist(text)) {
            tab = new Tab(text);
            hostTab.put(text, tab);
            hostArea = new CodeArea();
            tab.setContent(hostArea);
            this.getTabPaneMain().getTabs().add(tab);
            this.initHostArea(hostArea);
            tab.setOnClosed(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    //关闭
                    String tabText = tab.getText();
                    TreeItem<Label> switchHost = getSwitchHost();
                    if (!Constants.CURRENT_HOST.equals(tabText)){
                        hostTab.remove(tabText);
                        switchHost.getChildren().removeIf(host -> tabText.equals(host.getValue().getText()));
                    }
                }
            });
        } else {
            tab = hostTab.get(text);
            hostArea = (CodeArea) tab.getContent();
        }
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
            this.getTabPaneMain().getSelectionModel().select(tab);
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
        ObservableList<Tab> tabs = this.getTabPaneMain().getTabs();
        if (CollectionUtils.isNotEmpty(tabs)) {
            for (Tab tab : tabs) {
                if (tab.getText().equals(text)) {
                    return true;
                }
            }
        }
        return false;
    }


}
