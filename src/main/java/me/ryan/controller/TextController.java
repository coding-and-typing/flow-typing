package me.ryan.controller;


import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
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
        // 1. 重置状态
        this.start();

        // 清空两个组件的内容
        textShowArea.getChildren().clear();

        // 2. 每个字符为一个 Text 对象，这样就可以分别控制显示效果了。
        text.codePoints().forEach(
                c -> {
                    var text1 = new Text(new String(Character.toChars(c)));
                    text1.setFont(Font.font("Verdana", 20));
                    textShowArea.getChildren().add(text1);
                }
        );
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
        // TODO 只在已经上屏时, 才调用 updateText() 方法. (需要找到一个检测字符是否已经上屏的方法,)
        textInputArea.getInputMethodRequests();
        updateText();
    }

    private void updateText() {
        String[] inputText = Utils.codepoint2Strings(
                textInputArea.getText().codePoints().toArray());

        var textList = textShowArea.getChildren();

        // code point, 字符数
        int inputLengthNow = inputText.length;


        logger.debug("inputLengthLast:{}, inputLengthNow:{}", inputLengthLast, inputLengthNow);

        if (inputLengthLast < inputLengthNow) {
            // 1. 更新字符数
            int count = inputLengthNow - inputLengthLast;
            scoreUpdater.addToCharactersCount(count);
            // 如果字符数添加了不止一个，还要更新打词字符数
            if (count > 1) {
                // TODO 打词率应该排除标点符号和空白字符
                scoreUpdater.addToCharCountInWord(count);
            }

            // 2. 检测刚刚键入的字符是否敲对了
            for (int i = inputLengthLast; i < inputLengthNow; i++) {
                var inputChar = inputText[i];
                var textShouldBe = (Text) textList.get(i);

                logger.info("inputText_{}: '{}', textShouldBe: {}", i, inputChar, textShouldBe.getText());

                if (textShouldBe.getText().equals(inputChar)) {
                    textShouldBe.setOpacity(0.5);  // 敲对了
                    textShouldBe.setFill(Paint.valueOf("Orange"));
                } else {
                    textShouldBe.setFill(Paint.valueOf("Red"));
                    // 更新错字数
                    scoreUpdater.incTypos();
                }
            }
        } else if (inputLengthLast == inputLengthNow) {
            // 相等的话，就不需要更新任何东西。打中文会有这种情况
            return;
        } else {
            // 删减了字符，需要重置被删除的字符的显示状态。
            for (int i = inputLengthNow; i < inputLengthLast; i++) {
                var text = (Text) textList.get(i);

                // 检测被删掉的是不是错字（红色），如果是错字，要减去错字数
                if (text.getFill().equals(Color.RED))
                    scoreUpdater.decTypos();

                // 重置颜色和透明度
                text.setOpacity(1);
                text.setFill(Paint.valueOf("Black"));
            }
        }

        // 用户在跟打过程中删除了已经输入的所有内容，这时要调用重打方法
        // 但是调用start前，要先恢复文章显示框的所有字符的样式，所以它要放这下面。
        if (inputLengthNow == 0 && inputLengthLast > 0) {
            this.start();
        }

        // 输入字数等于文章字数，且结尾无错字，就结束跟打
        var textLast = (Text) textList.get(textList.size() - 1);
        var inputLast = inputText[inputText.length - 1];
        if (inputLengthNow == textList.size()
                && textLast.getText().equals(inputLast)) {
            this.stop();

            // 将成绩放入剪切版
            Clipboard clipboard = Clipboard.getSystemClipboard();
            var content = new ClipboardContent();
            content.putString(scoreUpdater.toString());
            clipboard.setContent(content);
        }

        // 检测到光标不在末尾时, 就不仅需要刷新结尾部分的状态。还需要查看前面所有的字符的状态
        // position 是 code point 偏移，不是 char 偏移
        var position = textInputArea.getCaretPosition();
        if (position != inputLengthNow) {
            int end = Math.min(inputLengthLast, inputLengthNow);
            for (int i = 0; i < end; i++) {
                var inputChar = inputText[i];
                var textShouldBe = (Text) textList.get(i);

                if (textShouldBe.getText().equals(inputChar)) {
                    // 进入这里说明这个字符是正确的
                    if (textShouldBe.getFill().equals(Color.RED)) {
                        // 说明原来不是正确的(红色)

                        scoreUpdater.decTypos();  // 那就是修正了一个字符

                        // 设为敲对了的样式
                        textShouldBe.setOpacity(0.5);
                        textShouldBe.setFill(Paint.valueOf("Orange"));
                    }

                } else {
                    if (textShouldBe.getFill().equals(Color.RED)) {
                        // 说明原来就不正确的(红色)，现在也还是不正确，啥都不需要干
                        return;
                    }

                    scoreUpdater.incTypos();  // 那就是多了一个错字
                    textShouldBe.setOpacity(1);
                    textShouldBe.setFill(Paint.valueOf("Red"));
                }
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
        textInputArea.setEditable(false);
        done = true;  // 刚初始化时，不允许用户输入，因此 done 应该设为 true
        inputLengthLast = 0;
    }

    /**
     * 在开始跟打前（或按下重打键后）调用这个方法，设置一些必要的参数
     */
    private void start() {
        textInputArea.setEditable(true);
        done = false;   // 开始跟打了，当前是未done的状态
        inputLengthLast = 0;

        textInputArea.setText("");

        scoreUpdater.reInit();
    }

    private void stop() {
        textInputArea.setEditable(false);
        done = true;   // 开始跟打了，当前是未done的状态
        inputLengthLast = 0;

        scoreUpdater.suspended();
    }
}
