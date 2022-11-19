package com.zeros.devtool.utils;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Timer;
import java.util.TimerTask;

public class ToastUtil {

    private static Stage stage = new Stage();
    private static Label label = new Label();

    static {
        stage.initStyle(StageStyle.TRANSPARENT);//舞台透明
    }

    //默认3秒
    public static void toast(String msg) {
        toast(msg, 3000);
    }

    public static Stage getStage(){
        return stage;
    }

    /**
     * 指定时间消失
     *
     * @param msg
     * @param time
     */
    public static void toast(String msg, int time) {
        label.setText(msg);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> stage.close());
            }
        };
        init(msg);
        Timer timer = new Timer();
        timer.schedule(task, time);
        stage.show();
    }

    public static void close() {
        if (stage != null) {
            stage.close();
        }
    }

    //设置消息
    private static void init(String msg) {
        //默认信息
        Label label = new Label(msg);
        label.setStyle("-fx-background: rgba(56,56,56,0.7);-fx-border-radius: 10;-fx-background-radius: 10");//label透明,圆角
        //消息字体颜色
        label.setTextFill(Color.rgb(225, 255, 226));
        label.setPrefHeight(20);
        label.setPadding(new Insets(15));
        //居中
        label.setAlignment(Pos.CENTER);
        //字体大小
        label.setFont(new Font(16));
        Scene scene = new Scene(label);
        //场景透明
        scene.setFill(null);
        stage.setScene(scene);
    }

}
