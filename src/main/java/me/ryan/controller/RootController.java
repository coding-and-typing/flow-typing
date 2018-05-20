package me.ryan.controller;

import javafx.event.ActionEvent;
import javafx.scene.input.Clipboard;
import me.ryan.model.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RootController {
    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    private final TextController textController;

    private final ScoresController scoresController;

    private final Score scoreNow;

    @Autowired
    public RootController(TextController textController, ScoresController scoresController, Score score) {
        this.textController = textController;
        this.scoresController = scoresController;
        this.scoreNow = score;
    }

    // 剪切版载文
    public void setTextFromCupboard(ActionEvent actionEvent) {
        // 1. 载文
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String text = clipboard.getString();
        textController.setTextShow(text);

        // TODO 2. 初始化输入框和 ScoreNow
//        scoresController.newScore();
    }

    // TODO 在载文时，要创建新的 Score 对象。


    // TODO 重打


}
