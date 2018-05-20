package me.ryan.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import me.ryan.model.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ScoresController {
    private static final Logger logger = LoggerFactory.getLogger(ScoresController.class);

    @FXML
    public TableView<Score> scoresTable;

    // 泛型参数<模型类型，本列的类型>
    @FXML
    private TableColumn<Score, String> cpmColumn;

    @FXML
    private TableColumn<Score, String> kpsColumn;

    @FXML
    private TableColumn<Score, String> timeIntervalColumn;

    private ObservableList<Score> scoresList = FXCollections.observableArrayList();

    @Autowired
    public ScoresController(Score scoreNow) {
        scoresList.add(scoreNow);
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // 在这个方法被调用的时候，才能访问到 fxml 文件里定义的属性。

        cpmColumn.setCellValueFactory(new PropertyValueFactory<>("cpm"));
        kpsColumn.setCellValueFactory(new PropertyValueFactory<>("kps"));
        timeIntervalColumn.setCellValueFactory(new PropertyValueFactory<>("timeInterval"));

        this.scoresTable.setItems(scoresList);
    }
}
