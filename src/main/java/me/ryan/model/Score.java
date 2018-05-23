package me.ryan.model;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 成绩的模型类，用于计算各项成绩用
 */

@Component
public class Score {

    // 用于格式化输出小数
    private static final DecimalFormat df = new DecimalFormat("0.00");

    // 用于格式化输出时间
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("mm:ss:SS");

    //下面是各项属性

    // 第xx段
    private int idOfArticle;

    // 键数
    private int keystrokes;

    // 字数
    // 长按的时候，只记一个键，但字符数却是一直增的，所以有时字数比键数大，是正常的
    private int charactersCount;

    // 用时
    private LocalTime timeInterval;

    // characters per minute, 字符输入速度
    private double cpm;

    // keystrokes per second, 击键
    private double kps;

    // 码长
    private double keysEachChar;

    // 回车
    private int keyEnterCount;

    // 错字
    private int typos;

    // 打词率
    private double ratioOfWords;

    // 退格
    private int backspaceCount;

    // 键准
    private double keysAccuracy;

    // 重打
    private int retypeCount;

    /**
     *  无参构造器
     */
    public Score() {
        this.idOfArticle = 0;
        this.keystrokes = 0;
        this.charactersCount = 0;
        this.timeInterval = LocalTime.of(0, 0);
        this.cpm = 0;
        this.kps = 0;
        this.keysEachChar = 0;
        this.keyEnterCount = 0;
        this.typos = 0;
        this.ratioOfWords = 0;
        this.backspaceCount = 0;
        this.keysAccuracy = 0;
        this.retypeCount = 0;
    }


    // 下面的getXXXString()方法，用于格式化输出各属性

    public String getIdOfArticleString() {
        return idOfArticle + "";
    }

    public String getKeystrokesString() {
        return keystrokes + "";
    }

    public String getCharactersCountString() {
        return charactersCount + "";
    }

    public String getTimeIntervalString() {
        return timeInterval.format(dateTimeFormatter);
    }

    public String getCpmString() {
        return df.format(cpm);
    }

    public String getKpsString() {
        return df.format(kps);
    }

    public String getKeysEachCharString() {
        return df.format(keysEachChar);
    }

    public String getKeyEnterCountString() {
        return keyEnterCount + "";
    }

    public String getTyposString() {
        return typos + "";
    }

    /**
     * 打词率，百分比
     */
    public String getRatioOfWordsString() {
        return df.format(ratioOfWords * 100) + "%";
    }

    public String getBackspaceCountString() {
        return backspaceCount + "";
    }

    /**
     * 键准，百分比
     */
    public String getKeysAccuracyString() {
        return df.format(keysAccuracy * 100) + "%";
    }

    public String getRetypeCountString() {
        return retypeCount + "";
    }


    // 下面是各属性的 get set 方法

    public int getIdOfArticle() {
        return idOfArticle;
    }

    public void setIdOfArticle(int idOfArticle) {
        this.idOfArticle = idOfArticle;
    }

    public int getKeystrokes() {
        return keystrokes;
    }

    public void setKeystrokes(int keystrokes) {
        this.keystrokes = keystrokes;
    }

    public int getCharactersCount() {
        return charactersCount;
    }

    public void setCharactersCount(int charactersCount) {
        this.charactersCount = charactersCount;
    }

    public LocalTime getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(LocalTime timeInterval) {
        this.timeInterval = timeInterval;
    }

    public double getCpm() {
        return cpm;
    }

    public void setCpm(double cpm) {
        this.cpm = cpm;
    }

    public double getKps() {
        return kps;
    }

    public void setKps(double kps) {
        this.kps = kps;
    }

    public double getKeysEachChar() {
        return keysEachChar;
    }

    public void setKeysEachChar(double keysEachChar) {
        this.keysEachChar = keysEachChar;
    }

    public int getKeyEnterCount() {
        return keyEnterCount;
    }

    public void setKeyEnterCount(int keyEnterCount) {
        this.keyEnterCount = keyEnterCount;
    }

    public int getTypos() {
        return typos;
    }

    public void setTypos(int typos) {
        this.typos = typos;
    }

    public double getRatioOfWords() {
        return ratioOfWords;
    }

    public void setRatioOfWords(double ratioOfWords) {
        this.ratioOfWords = ratioOfWords;
    }

    public int getBackspaceCount() {
        return backspaceCount;
    }

    public void setBackspaceCount(int backspaceCount) {
        this.backspaceCount = backspaceCount;
    }

    public double getKeysAccuracy() {
        return keysAccuracy;
    }

    public void setKeysAccuracy(double keysAccuracy) {
        this.keysAccuracy = keysAccuracy;
    }

    public int getRetypeCount() {
        return retypeCount;
    }

    public void setRetypeCount(int retypeCount) {
        this.retypeCount = retypeCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Score)) return false;
        Score score = (Score) o;
        return getIdOfArticle() == score.getIdOfArticle() &&
                getKeystrokes() == score.getKeystrokes() &&
                getCharactersCount() == score.getCharactersCount() &&
                Double.compare(score.getKeysEachChar(), getKeysEachChar()) == 0 &&
                getKeyEnterCount() == score.getKeyEnterCount() &&
                getTypos() == score.getTypos() &&
                Double.compare(score.getRatioOfWords(), getRatioOfWords()) == 0 &&
                getBackspaceCount() == score.getBackspaceCount() &&
                Double.compare(score.getKeysAccuracy(), getKeysAccuracy()) == 0 &&
                getRetypeCount() == score.getRetypeCount() &&
                Objects.equals(getTimeInterval(), score.getTimeInterval()) &&
                Objects.equals(getCpm(), score.getCpm()) &&
                Objects.equals(getKps(), score.getKps());
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                getIdOfArticle(),
                getKeystrokes(),
                getCharactersCount(),
                getTimeInterval(),
                getCpm(),
                getKps(),
                getKeysEachChar(),
                getKeyEnterCount(),
                getTypos(),
                getRatioOfWords(),
                getBackspaceCount(),
                getKeysAccuracy(),
                getRetypeCount()
        );
    }

    /**
     * 校验码好像都只取6位
     */
    public String getHashCodeString() {
        return (hashCode() + "").substring(0, 6);
    }

    @Override
    public String toString() {
        return "第" + getIdOfArticleString() + "段" +
                " 键数" + getKeystrokesString() +
                " 字数" + getCharactersCountString() +
                " 用时" + getTimeIntervalString() +
                " 速度" + getCpmString() +
                " 击键" + getKpsString() +
                " 码长" + getKeysEachCharString() +
                " 回车" + getKeyEnterCountString() +
                " 错字" + getTyposString() +
                " 打词" + getRatioOfWordsString() +
                " 退格" + getBackspaceCountString() +
                " 键准" + getKeysAccuracyString() +
                " 重打" + getRetypeCountString() +
                " 校验" + getHashCodeString();
    }


    /**
     * 重置成绩
     */
    public void reInit() {
        this.idOfArticle = 0;
        this.keystrokes = 0;
        this.charactersCount = 0;
        this.timeInterval = LocalTime.of(0, 0);
        this.cpm = 0;
        this.kps = 0;
        this.keysEachChar = 0;
        this.keyEnterCount = 0;
        this.typos = 0;
        this.ratioOfWords = 0;
        this.backspaceCount = 0;
        this.keysAccuracy = 0;
        this.retypeCount = 0;
    }
}