package me.ryan.model;

import javafx.beans.property.*;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 成绩的模型类
 */
public class Score {
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(Score.class);

    // cpm 和 kps 的更新间隔，单位秒
    private static final double INTERVAL = 0.2;

    // 用于格式化输出 cpm 和 kps
    DecimalFormat df = new DecimalFormat("0.00");

    // 当 inputArea 没有 focus 时，需要停止刷新时间。这个变量为此而存在
    private boolean active;

    // 每次 start() 被调用时，它被刷新。
    private LocalTime startTime;

    // 从 start() 被调用时开始，用户击键的总次数。
    private int keystrokes;

    // 从 start() 被调用时开始，用户输入过的字符总数。
    // 被退格删除了的字符也算在内，但退格本身不算在内。
    private int charactersCount;

    // start() 被调用前，也就是用户离开前，用户输入所用过的时间。
    private LocalTime timeIntervalLast;

    // 这两个参数需要使用Property实现数据绑定，这样这边修改内容时，TableView会同步更新。

    // characters per minute, 字符输入速度 = charactersCount / timeIntervalLast in minutes
    private final StringProperty cpm = new SimpleStringProperty();

    // keystrokes per second, 击键 = keystrokes / timeIntervalLast in seconds
    private final StringProperty kps = new SimpleStringProperty();

    private final StringProperty timeInterval = new SimpleStringProperty();

    /**
     * 默认的无参构造器
     */
    public Score() {
        // startTime 在 start() 被调用时才会被初始化。active也才会置为true
        this.startTime = null;
        this.active = false;

        this.charactersCount = 0;
        this.keystrokes = 0;

        // 初始时，时间间隔为 0
        this.timeIntervalLast = LocalTime.of(0,0);

        this.cpm.setValue("0.00");
        this.kps.setValue("0.00");
        this.timeInterval.setValue("0.00");

        // 开启自动更新
        updateAutomatically();
    }

    public boolean isActive() {
        return active;
    }

    /**
     * 调用任意需要读取数据的方法时，都要先check一下。
     */
    private void checkIt(String from) {
        if (!isActive()) {
            logger.error("{}: 在未调用 start() 前，此对象不可用。", from);

        }
    }

    /**
     * 返回从 start() 被调用，到现在的时间间隔
     *
     * @return LocalTime
     */
    private LocalTime getTimeInterval() {
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
     * 返回从 start() 被调用，到现在的时间间隔
     *
     * @return 以秒为单位 的 double 值
     */
    private double getTimeIntervalInSeconds() {
        LocalTime timeInterval = getTimeInterval();
        return timeInterval.toSecondOfDay()
                + timeInterval.getNano() / Math.pow(10, 9);
    }

    /**
     * 每次监听到按键消息，就调用此方法
     */
    public void incKeystrokes() {
        checkIt("incKeystrokes");

        keystrokes++;

        logger.info("keystrokes: {}", keystrokes);
    }

    /**
     * 当用户向 InputArea 输入了字符时，调用此方法。
     * @param count
     */
    public void addToCharactersCount(int count) {
        checkIt("addToCharactersCount");

        charactersCount += count;

        logger.info("charactersCount: {}", charactersCount);
    }


    /**
     * 更新速度
     */
    private void updateCpm(){
        double cpm = charactersCount / getTimeIntervalInSeconds() * 60;

        this.cpm.setValue(df.format(cpm));
    }

    public StringProperty cpmProperty() {
        return cpm;
    }

    /**
     * 更新击键
     */
    private void updateKps() {
        double kps = keystrokes / getTimeIntervalInSeconds();

        this.kps.setValue(df.format(kps));
    }

    public StringProperty kpsProperty() {
        return kps;
    }

    private void updateTimeInterval() {
        String time = getTimeInterval().format(
                DateTimeFormatter.ofPattern("mm:ss:SS"));
        this.timeInterval.setValue(time);
    }

    public StringProperty timeIntervalProperty() {
        return timeInterval;
    }

    // 开始记录
    public void start() {
        this.active = true;
        this.startTime = LocalTime.now();
    }

    // 暂停记录
    public void suspended() {
        this.timeIntervalLast = getTimeInterval();  // 保存此次的timeInterval。
        this.active = false;
    }

    // 定时更新显示的成绩
    private void updateAutomatically() {
        ScheduledService scheduledService =  new ScheduledService(){

            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        // active 为 true 时才需要更新
                        if (isActive()){
                            updateCpm();
                            updateKps();
                            updateTimeInterval();
                        }

                        return null;
                    }
                };
            }
        };

        scheduledService.setPeriod(Duration.seconds(INTERVAL)); //设定更新间隔
        scheduledService.start();
    }

}
