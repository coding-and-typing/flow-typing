package me.ryan.model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.stereotype.Component;

/**
 * Score 的 Properties 对象，用于显示成绩用
 */
@Component
public class ScoreProperties implements Cloneable {
    //属性全用 String，方便调整

    // 速度
    private StringProperty cpm;

    // 击键
    private StringProperty kps;

    // 码长
    private StringProperty keysEachChar;

    // 用时
    private StringProperty timeInterval;

    // 回车
    private StringProperty keyEnterCount;

    // 错字
    private StringProperty typos;

    // 字数
    private StringProperty charactersCount;

    // 打词率
    private StringProperty ratioOfWords;

    // 退格
    private StringProperty backspaceCount;

    // 键数
    private StringProperty keystrokes;

    // 键准
    private StringProperty keysAccuracy;

    // 重打
    private StringProperty retypeCount;

    /**
     * 无参构造器
     */
    public ScoreProperties() {
        this.cpm = new SimpleStringProperty("");
        this.kps = new SimpleStringProperty("");
        this.keysEachChar = new SimpleStringProperty("");
        this.timeInterval = new SimpleStringProperty("");
        this.keyEnterCount = new SimpleStringProperty("");
        this.typos = new SimpleStringProperty("");
        this.charactersCount = new SimpleStringProperty("");
        this.ratioOfWords = new SimpleStringProperty("");
        this.backspaceCount = new SimpleStringProperty("");
        this.keystrokes = new SimpleStringProperty("");
        this.keysAccuracy = new SimpleStringProperty("");
        this.retypeCount = new SimpleStringProperty("");
    }


    /**
     * 用于深拷贝
     */
    private ScoreProperties(ScoreProperties scoreDao) {
        this.cpm = new SimpleStringProperty(scoreDao.cpm.getValue());
        this.kps = new SimpleStringProperty(scoreDao.kps.getValue());
        this.keysEachChar = new SimpleStringProperty(scoreDao.keysEachChar.getValue());
        this.timeInterval = new SimpleStringProperty(scoreDao.timeInterval.getValue());
        this.keyEnterCount = new SimpleStringProperty(scoreDao.keyEnterCount.getValue());
        this.typos = new SimpleStringProperty(scoreDao.typos.getValue());
        this.charactersCount = new SimpleStringProperty(scoreDao.charactersCount.getValue());
        this.ratioOfWords = new SimpleStringProperty(scoreDao.ratioOfWords.getValue());
        this.backspaceCount = new SimpleStringProperty(scoreDao.backspaceCount.getValue());
        this.keystrokes = new SimpleStringProperty(scoreDao.keystrokes.getValue());
        this.keysAccuracy = new SimpleStringProperty(scoreDao.keysAccuracy.getValue());
        this.retypeCount = new SimpleStringProperty(scoreDao.retypeCount.getValue());
    }

    public String getCpm() {
        return cpm.get();
    }

    public void setCpm(String cpm) {
        this.cpm.set(cpm);
    }

    public StringProperty cpmProperty() {
        return cpm;
    }

    public String getKps() {
        return kps.get();
    }

    public void setKps(String kps) {
        this.kps.set(kps);
    }

    public StringProperty kpsProperty() {
        return kps;
    }

    public String getKeysEachChar() {
        return keysEachChar.get();
    }

    public void setKeysEachChar(String keysEachChar) {
        this.keysEachChar.set(keysEachChar);
    }

    public StringProperty keysEachCharProperty() {
        return keysEachChar;
    }

    public String getTimeInterval() {
        return timeInterval.get();
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval.set(timeInterval);
    }

    public StringProperty timeIntervalProperty() {
        return timeInterval;
    }

    public String getKeyEnterCount() {
        return keyEnterCount.get();
    }

    public void setKeyEnterCount(String keyEnterCount) {
        this.keyEnterCount.set(keyEnterCount);
    }

    public StringProperty keyEnterCountProperty() {
        return keyEnterCount;
    }

    public String getTypos() {
        return typos.get();
    }

    public void setTypos(String typos) {
        this.typos.set(typos);
    }

    public StringProperty typosProperty() {
        return typos;
    }

    public String getCharactersCount() {
        return charactersCount.get();
    }

    public void setCharactersCount(String charactersCount) {
        this.charactersCount.set(charactersCount);
    }

    public StringProperty charactersCountProperty() {
        return charactersCount;
    }

    public String getRatioOfWords() {
        return ratioOfWords.get();
    }

    public void setRatioOfWords(String ratioOfWords) {
        this.ratioOfWords.set(ratioOfWords);
    }

    public StringProperty ratioOfWordsProperty() {
        return ratioOfWords;
    }

    public String getBackspaceCount() {
        return backspaceCount.get();
    }

    public void setBackspaceCount(String backspaceCount) {
        this.backspaceCount.set(backspaceCount);
    }

    public StringProperty backspaceCountProperty() {
        return backspaceCount;
    }

    public String getKeystrokes() {
        return keystrokes.get();
    }

    public void setKeystrokes(String keystrokes) {
        this.keystrokes.set(keystrokes);
    }

    public StringProperty keystrokesProperty() {
        return keystrokes;
    }

    public String getKeysAccuracy() {
        return keysAccuracy.get();
    }

    public void setKeysAccuracy(String keysAccuracy) {
        this.keysAccuracy.set(keysAccuracy);
    }

    public StringProperty keysAccuracyProperty() {
        return keysAccuracy;
    }

    public String getRetypeCount() {
        return retypeCount.get();
    }

    public void setRetypeCount(String retypeCount) {
        this.retypeCount.set(retypeCount);
    }

    public StringProperty retypeCountProperty() {
        return retypeCount;
    }

    /**
     * 深拷贝
     */
    @Override
    public ScoreProperties clone() {
        return new ScoreProperties(this);
    }

    /**
     * 重置成绩
     */
    public void reInit() {
        this.cpm.set("");
        this.kps.set("");
        this.keysEachChar.set("");
        this.timeInterval.set("");
        this.keyEnterCount.set("");
        this.typos.set("");
        this.charactersCount.set("");
        this.ratioOfWords.set("");
        this.backspaceCount.set("");
        this.keystrokes.set("");
        this.keysAccuracy.set("");
        this.retypeCount.set("");
    }
}
