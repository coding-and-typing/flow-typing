package me.ryan.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import me.ryan.model.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoresController {
    private static final Logger logger = LoggerFactory.getLogger(ScoresController.class);

    @FXML
    public TableView<Score> scoresTable;

    // 泛型参数<模型类型，本列的类型>
    @FXML private TableColumn<Score, String> cpmColumn;

    @FXML private TableColumn<Score, String> kpsColumn;

    @FXML
    public TableColumn<Score, String> timeIntervalColumn;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        cpmColumn.setCellValueFactory(new PropertyValueFactory<>("cpm"));
        kpsColumn.setCellValueFactory(new PropertyValueFactory<>("kps"));
        timeIntervalColumn.setCellValueFactory(new PropertyValueFactory<>("timeInterval"));
    }

    public void setScoresTableItems(ObservableList<Score> scoresList) {
        this.scoresTable.setItems(scoresList);
    }
}
