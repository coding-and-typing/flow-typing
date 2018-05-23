package me.ryan.controller;

import javafx.event.ActionEvent;
import javafx.scene.input.Clipboard;
import me.ryan.model.ScoreUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class RootController {
    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    private final TextController textController;

    private final ScoresController scoresController;

    private final ScoreUpdater scoreUpdater;

    @Autowired
    public RootController(TextController textController, ScoresController scoresController, ScoreUpdater scoreUpdater) {
        this.textController = textController;
        this.scoresController = scoresController;
        this.scoreUpdater = scoreUpdater;
    }

    /**
     * QQ群剪切版载文
     * TODO QQ群文章格式分析，拿到段号，文章名。
     *
     * @param actionEvent xx
     */
    public void setTextFromCupboard(ActionEvent actionEvent) {
        // 1. 保存上一次的成绩，并初始化成绩
        scoresController.updateScores();
        scoreUpdater.reInit();


        // 2. 从剪切版获取文章
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String text = clipboard.getString();

        if (text == null) {
            logger.info("剪切版无内容");
            return;
        }

        // 3. 分析文章
        // 正则匹配
        Pattern pattern =
                Pattern.compile(".+"     // 这个是 ”xxx群xxx赛文“ 之类的东西
                        + "第\\d+期-"    // 第 xxx 期
                        + ".+制作(?:\\r\\n|\\n|\\r)"    // xx制作
                        + "([\\s\\S]+)"        // 赛文内容
                        + "(?:\\r\\n|\\n|\\r)-{3,10}第(\\d+)段"  // -----第xxx段
                        + "[\\s\\S]*"          // 本文由xxx组成
                );
        Matcher matcher = pattern.matcher(text);

        if (matcher.matches()) {
            // 如果匹配上了

            // 4. 设置段号
            scoreUpdater.setIdOfArticle(Integer.parseInt(matcher.group(2)));
            // 5. 设置赛文内容
            text = matcher.group(1);

        } else {
            logger.error("赛文格式不匹配，已加载全文");
        }

        // 6. 显示文章
        textController.setTextShow(text);
    }

    // TODO 重打

}
