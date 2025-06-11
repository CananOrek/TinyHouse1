package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private Button button1;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private void button12() {
        System.out.println("Button1 clicked!");
    }
}