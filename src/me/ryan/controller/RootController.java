package me.ryan.controller;

import javafx.event.ActionEvent;
import javafx.scene.input.Clipboard;

public class RootController {
    private TextController textController;

    // 剪切版载文
    public void setTextFromCupboard(ActionEvent actionEvent) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String text = clipboard.getString();
        textController.setText(text);
    }

    public void setTextController(TextController textController) {
        this.textController = textController;
    }
}
