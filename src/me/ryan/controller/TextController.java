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
import me.ryan.model.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextController {
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(TextController.class);

    // 跟打文章显示区域
    @FXML
    private TextFlow textShowArea;

    //跟打区域
    @FXML
    private TextArea textInputArea;

    // 当前跟打的成绩
    private Score score;

    // 保存上次按键消息时, textInputArea 的长度。用于判断字符量的增减
    private int inputLengthLast = 0;

    public TextController() {
    }

    // 每当有按键消息时，此方法被触发
    public void update(KeyEvent keyEvent) {
        // 当此方法被调用时，用户一定已经开始输入了，所以要start
        if (!score.isActive()) score.start();

        score.incKeystrokes();

        // update charactersCount
        int InputLengthNow = textInputArea.getLength();
        if (inputLengthLast < InputLengthNow) {
            score.addToCharactersCount(InputLengthNow - inputLengthLast);
        }

        // 刷新跟打文本显示框
        updateTextFlow(keyEvent);

        // 最后才能更新它
        inputLengthLast = InputLengthNow;
    }

    public void setScore(Score score) {
        this.score = score;
        checkFocusAutomatically();
    }

    private void checkFocusAutomatically() {
        ScheduledService scheduledService = new ScheduledService() {

            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        if (score.isActive() && !textInputArea.isFocused())
                            score.suspended();

                        return null;
                    }
                };
            }
        };

        scheduledService.setPeriod(Duration.seconds(.1)); //设定更新间隔
        scheduledService.start();
    }

    public void setText(String text) {
        for (int i = 0; i < text.codePointCount(0, text.length()); i++) {
            Text text1 = new Text(
                    new String(Character.toChars(text.codePointAt(i))));
            text1.setFont(Font.font("Verdana", 20));
            textShowArea.getChildren().add(text1);
        }

        textInputArea.setFont(Font.font("Verdana", 20));
    }

    private void updateTextFlow(KeyEvent keyEvent) {
        var textList = textShowArea.getChildren();
        var inputLength = textInputArea.getLength();

        if (inputLength == 0 || textList.size() == 0) return;

        // 跟打结束
        // TODO 字符数相同，而且结尾无错
        if(textList.size() == inputLength){
            textInputArea.setEditable(false);
            score.suspended();
        }

        // 检测是否敲对了
        for (int i = inputLengthLast; i < inputLength; i++) {
            var textInput = textInputArea.getText(i, i+1);
            var textShouldBe = (Text) textList.get(i);

            if (textShouldBe.getText().equals(textInput)) {
                textShouldBe.setOpacity(0.5);  // 敲对了
                textShouldBe.setFill(Paint.valueOf("Orange"));
            } else {
                textShouldBe.setFill(Paint.valueOf("Red"));
            }
        }
    }
}
