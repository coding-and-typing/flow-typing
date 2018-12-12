package me.ryan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        SpringFxmlLoader loader = new SpringFxmlLoader();

        // 1. 加载 ScoresView
        VBox scoresView = (VBox) loader.load("/view/ScoresView.fxml");

        // 2. 加载 TextView
        VBox textView = (VBox) loader.load("/view/TextView.fxml");

        // 3. 加载 RootView
        VBox root = (VBox) loader.load("/view/RootView.fxml");

        // 4. 将 textView 和 scoresView 添加进RootView
        root.getChildren().addAll(textView, scoresView);

        // 5. 创建包含了 RootView 的 Scene
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("跟打器");

        // 6. 显示primaryStage
        primaryStage.show();
    }
}


/**
 * 用于加载fxml布局的类
 */
class SpringFxmlLoader {
    private static final Logger logger = LoggerFactory.getLogger(SpringFxmlLoader.class);

    private final ApplicationContext context;

    SpringFxmlLoader() {
        this.context = new AnnotationConfigApplicationContext(me.ryan.config.SpringConfig.class);
    }

    Object load(String url) {
        // 创建 FxmlLoader 对象
        FXMLLoader loader = new FXMLLoader();
        // 设置 fxml 文件所在位置
        loader.setLocation(this.getClass().getResource(url));

        // 因为使用了Spring，所以现在需要手动设置 ControllerFactory
        loader.setControllerFactory(context::getBean);

        try {
            return loader.load();
        } catch (Exception e) {
            logger.error("加载fxml文件 {} 失败。", url);
            return null;
        }
    }

}
