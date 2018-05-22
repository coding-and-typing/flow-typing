package me.ryan.controller;

import javafx.event.ActionEvent;
import javafx.scene.input.Clipboard;
import me.ryan.model.ScoreUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RootController {
    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    private final TextController textController;

    private final ScoresController scoresController;

    private final ScoreUpdater scoreUpdator;

    @Autowired
    public RootController(TextController textController, ScoresController scoresController, ScoreUpdater scoreUpdator) {
        this.textController = textController;
        this.scoresController = scoresController;
        this.scoreUpdator = scoreUpdator;
    }

    /**
     * 剪切版载文
     * TODO QQ群文章格式分析，拿到段号，文章名。
     *
     * @param actionEvent xx
     */
    public void setTextFromCupboard(ActionEvent actionEvent) {
        // 1. 从剪切版获取文章
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String text = clipboard.getString();

        // 2. 显示文章
        textController.setTextShow(text);


        // 3. 初始化成绩
        scoreUpdator.reInit();
    }

    // TODO 重打

}
