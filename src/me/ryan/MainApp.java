package me.ryan;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.ryan.controller.RootController;
import me.ryan.controller.ScoresController;
import me.ryan.controller.TextController;
import me.ryan.model.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 程序从这里启动
 */
public class MainApp extends Application {
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    // 根标签
    private VBox root;
    private Stage primaryStage;

    //controller
    RootController rootController;
    ScoresController scoresController;
    TextController textController;

    // 用做成绩保存与显示的结构
    private ObservableList<Score> scoresList = FXCollections.observableArrayList();


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("专心跟打器");

        // TODO 暂时将Score的创建放在这。以后需要修改
        Score score = new Score();
        scoresList.add(score);

        // 初始化 RootView 为 根.
        initRootView();

        // 在根上依次添加 TextView 和 ScoreView。
        showTextView();
        showScoresView();
    }

    private void initRootView() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("view/RootView.fxml"));

        try {
            root = loader.load();
        } catch (IOException e) {
            logger.error("加载 RootView.fxml 失败");
        }

        rootController = loader.getController();

        // 创建包含了 RootView 的 Scene，并将其放入 PrimaryStage。
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // 显示primaryStage
        primaryStage.show();
    }

    private void showTextView() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("view/TextView.fxml"));

        try {
            // 将 TextView 添加为 root 的 children
            root.getChildren().add(loader.load());
        } catch (IOException e) {
            logger.error("加载 TextView.fxml 失败");
        }

        // TODO Score 的初始化修改后，这里也需要同步修改
        textController = loader.getController();
        textController.setScore(scoresList.get(0));

        // rootController 载文需要用到 TextController
        rootController.setTextController(textController);
    }

    private void showScoresView() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("view/ScoresView.fxml"));

        try {
            root.getChildren().add(loader.load());
        } catch (IOException e) {
            logger.error("加载 ScoresView.fxml 失败");
        }

        // set ScoresList
        scoresController = loader.getController();
        scoresController.setScoresTableItems(scoresList);
    }


    public static void main(String[] args) {
        MainApp.launch(args);
    }
}
