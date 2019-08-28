package com.company;

import GUI.JavaFx.MainGUI;
import javafx.stage.Stage;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
	// write your code here
//        TODO: request interfaceForm
        MainGUI GUI = new MainGUI();

        Stage stage = new Stage();
        try {
            GUI.start(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        InterfaceForm GUI = new InterfaceForm();

    }
}
