package me.ryan.model;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * 计算成绩 并 更新成绩的显示。
 */

@Component
public class ScoreUpdater {
    private static final Logger logger = LoggerFactory.getLogger(ScoreUpdater.class);
    // 与时间相关的属性的更新间隔，单位秒
    private static final double INTERVAL = 0.2;
    // 两个Score对象，一个存计算值，一个是显示值。要同步更新
    private final Score score;
    private final ScoreProperties scoreProperties;
    // 当 inputArea 没有 focus 时，需要暂停更新。这个变量为此而存在
    private boolean active;

    // 每次 start() 被调用时，它被刷新。
    private LocalTime startTime;

    // start() 被调用前，也就是用户离开前，用户输入所用过的时间。
    private LocalTime timeIntervalLast;

    // TODO 通过打词键入的字符数

    // TODO ：退格 等

    @Autowired
    public ScoreUpdater(Score score, ScoreProperties scoreProperties) {
        this.score = score;
        this.scoreProperties = scoreProperties;

        this.active = false;  // 还没开始输入时，为false
        this.startTime = LocalTime.now();
        timeIntervalLast = LocalTime.of(0, 0);  // 初始用时为0

        // 开启自动更新
        updateAutomatically();
    }

    /**
     * 定时更新显示的成绩
     */
    private void updateAutomatically() {
        ScheduledService scheduledService = new ScheduledService() {

            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() {
                        updateScore();
                        return null;
                    }
                };
            }
        };

        scheduledService.setPeriod(Duration.seconds(INTERVAL)); //设定更新间隔
        scheduledService.start();
    }


    public boolean isActive() {
        return active;
    }

    /**
     * 调用任意会读取/更新数据的方法时，都要先check一下。
     * 注意：（文章段号 和 重打次数 是在非跟打状态下更新的，所以不要加）
     */
    private void checkIt(String from) {
        if (!isActive()) {
            logger.error("{}: 在未调用 start() 前，此对象不可用。", from);
        }
    }

    /**
     * 一次跟打结束后，用户点击重打按键时，调用这个，所以也不要加 checkIt
     */
    public void incRetypeCount() {
        score.setRetypeCount(score.getRetypeCount() + 1);
        scoreProperties.setRetypeCount(score.getRetypeCountString());
    }

    public void setIdOfArticle(int id) {
        // Id 是在载文时调用的，所以千万别加 checkIt。

        score.setIdOfArticle(id);
        // TODO 整个地方来显示 idOfArticle
    }

    /**
     * 只要监听到按键消息，就调用此方法
     */
    public void incKeystrokes() {
        checkIt("incKeystrokes");

        // 同步更新两个对象
        score.setKeystrokes(score.getKeystrokes() + 1);
        scoreProperties.setKeystrokes(score.getKeystrokesString());

        logger.debug("keystrokes: {}", score.getKeystrokesString());
    }

    /**
     * 当用户向 InputArea 输入了字符时，调用此方法。
     *
     * @param count 该次输入增加的字符数
     */
    public void addToCharactersCount(int count) {
        checkIt("addToCharactersCount");

        score.setCharactersCount(score.getCharactersCount() + count);
        scoreProperties.setCharactersCount(score.getCharactersCountString());

        logger.debug("charactersCount: {}", score.getCharactersCountString());
    }

    public void incKeyEnterCount() {
        checkIt("incKeyEnterCount");

        score.setKeyEnterCount(score.getKeyEnterCount() + 1);
        scoreProperties.setKeyEnterCount(score.getKeyEnterCountString());
    }

    public void incBackspaceCount() {
        score.setBackspaceCount(score.getBackspaceCount() + 1);
        scoreProperties.setBackspaceCount(score.getBackspaceCountString());

        logger.debug("BackspaceCount + 1:　{}", score.getBackspaceCount());
    }

    /**
     * 开始更新，用户开始输入
     */
    public void start() {
        this.active = true;
        this.startTime = LocalTime.now();

        logger.info("start()：开始记录成绩。");
    }

    /**
     * 暂停更新，用户离开
     */
    public void suspended() {
        // 保存此次的timeIntervalAll。
        timeIntervalLast = getTimeIntervalAll();

        // 暂停时，也要更新一下成绩。
        updateScore();

        this.active = false;
    }

    /**
     * 总用时
     */
    private LocalTime getTimeIntervalAll() {
        checkIt("getTimeInterval");

        return LocalTime.now()  // 当前时间

                .minusHours(startTime.getHour())     // 减去开始时间
                .minusMinutes(startTime.getMinute())
                .minusSeconds(startTime.getSecond())
                .minusNanos(startTime.getNano())

                .plusHours(timeIntervalLast.getHour())     // 加上以前用过的时间
                .plusMinutes(timeIntervalLast.getMinute())
                .plusSeconds(timeIntervalLast.getSecond())
                .plusNanos(timeIntervalLast.getNano());
    }

    /**
     * 更新与时间有关的属性
     */
    private void updateScore() {
        //如果是暂停状态，就不需要更新了
        if (isActive()) {
            // 1. 更新 总用时
            LocalTime timeIntervalAll = getTimeIntervalAll();
            score.setTimeInterval(timeIntervalAll);
            scoreProperties.setTimeInterval(score.getTimeIntervalString());

            // 以秒为单位来表示的总用时
            double timeIntervalAllInSeconds = timeIntervalAll.toSecondOfDay()
                    + timeIntervalAll.getNano() / Math.pow(10, 9);

            // 2. 更新 cpm 速度 = 总字数/时间 单位为分钟，所以要乘60
            double newCpm = score.getCharactersCount() / timeIntervalAllInSeconds * 60;
            score.setCpm(newCpm);
            scoreProperties.setCpm(score.getCpmString());

            // 3. 更新 kps 击键 = 总键数/时间 单位秒
            double newKps = score.getKeystrokes() / timeIntervalAllInSeconds;
            score.setKps(newKps);
            scoreProperties.setKps(score.getKpsString());

            // 4. 更新 码长 = 键数/字数
            double newKeysEachChar = score.getKeystrokes() / (double) score.getCharactersCount();
            score.setKeysEachChar(newKeysEachChar);
            scoreProperties.setKeysEachChar(score.getKeysEachCharString());

            // TODO 5. 更新 打词率


            // TODO 6. 更新 键准
        }
    }

    /**
     * 跟打结束后，要重置当前成绩。（历史成绩的保存在scoresController里完成，这里不需要考虑它。）
     */
    public void reInit() {
        score.reInit();
        scoreProperties.reInit();

        this.active = false;
        this.startTime = LocalTime.now();
        timeIntervalLast = LocalTime.of(0, 0);
    }


    /**
     * TODO 生成收文机器人能识别的成绩字符串
     */
    @Override
    public String toString() {
        return score.toString();
    }
}
