package me.ryan.controller;


import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import me.ryan.model.ScoreUpdater;
import me.ryan.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class TextController {
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(TextController.class);

    // 自动检测的时间间隔
    private static final double INTERVAL = 0.1;

    // 跟打文章显示区域
    @FXML
    private TextFlow textShowArea;

    //跟打区域
    @FXML
    private TextArea textInputArea;

    private final ScoreUpdater scoreUpdater;

    // 保存上次按键消息时, textInputArea 的长度。用于判断字符量的增减
    private int inputLengthLast;

    // 跟打完毕的标志(它和 isActive 不一样，isActive 是用户离开界面的标志)
    private boolean done;

    @Autowired
    public TextController(ScoreUpdater scoreUpdater) {
        this.scoreUpdater = scoreUpdater;

        checkFocusAutomatically();
    }

    /**
     * 离开当前界面时，要暂停更新成绩
     * TODO 应该有更好的办法，可以在 RootController 里设置这个。
     */
    private void checkFocusAutomatically() {
        ScheduledService scheduledService = new ScheduledService() {

            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() {
                        if (scoreUpdater.isActive() && !textInputArea.isFocused())
                            scoreUpdater.suspended();
                        return null;
                    }
                };
            }
        };

        scheduledService.setPeriod(Duration.seconds(INTERVAL)); //设定更新间隔
        scheduledService.start();
    }


    /**
     * 载文
     *
     * @param text
     */
    public void setTextShow(String text) {
        // 1. 初始化
        inputLengthLast = 0;
        done = false;

        // 清空两个组件的内容
        textShowArea.getChildren().clear();
        textInputArea.setText("");

        // 2. 每个字符为一个 Text 对象，这样就可以分别控制显示效果了。
        text.codePoints().forEach(
                c -> {
                    var text1 = new Text(new String(Character.toChars(c)));
                    text1.setFont(Font.font("Verdana", 20));
                    textShowArea.getChildren().add(text1);
                }
        );

        // 3. 输入框允许输入
        textInputArea.setEditable(true);
    }


    /**
     * 每当有按键消息时，此方法被触发
     */
    public void update(KeyEvent keyEvent) {
        // 跟打结束后，就不应该更新任何东西了。
        if (done) return;

        // 用户开始输入，开始记录
        if (!scoreUpdater.isActive()) scoreUpdater.start();

        // 1. 更新和键入字符有关的内容
        updateKeys(keyEvent);

        // 2. 更新和文本有关的内容
        updateText();
    }

    private void updateText() {
        String[] inputText = Utils.codepoint2Strings(
                textInputArea.getText().codePoints().toArray());

        // 要算 code point
        int inputLengthNow = inputText.length;

        // 1. 更新字符数
        if (inputLengthLast < inputLengthNow) {
            // 键入了字符，更新
            scoreUpdater.addToCharactersCount(inputLengthNow - inputLengthLast);
        } else if (inputLengthLast == inputLengthNow) {
            // 相等的话，就不需要更新任何东西。打中文会有这种情况
            return;
        } else {
            // TODO 删减了字符，需要重置被删除的字符的显示状态。
        }

        // 2. 更新跟打文本的显示效果、还有错字数
        var textList = textShowArea.getChildren();

        // 跟打结束
        // TODO 结尾无错字才结束跟打
        if (inputLengthNow >= textList.size()) {
            done = true;
            textInputArea.setEditable(false);
            scoreUpdater.suspended();
        }

        // 检测刚刚键入的字符是否敲对了
        for (int i = inputLengthLast; i < inputLengthNow; i++) {
            var inputChar = inputText[i];
            var textShouldBe = (Text) textList.get(i);

            if (textShouldBe.getText().equals(inputChar)) {
                textShouldBe.setOpacity(0.5);  // 敲对了
                textShouldBe.setFill(Paint.valueOf("Orange"));
            } else {
                textShouldBe.setFill(Paint.valueOf("Red"));
                // TODO 更新错字数
            }
        }


        // 最后，更新 inputLengthLast
        inputLengthLast = inputLengthNow;
    }

    private void updateKeys(KeyEvent keyEvent) {
        scoreUpdater.incKeystrokes();

        switch (keyEvent.getCode()) {
            case BACK_SPACE:
                scoreUpdater.incBackspaceCount();
                break;
            case ENTER:
                scoreUpdater.incKeyEnterCount();
                break;
        }

    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // 设置一些样式参数，也可用 css 控制
        textInputArea.setEditable(false);
        done = true;
        inputLengthLast = 0;
    }


}
