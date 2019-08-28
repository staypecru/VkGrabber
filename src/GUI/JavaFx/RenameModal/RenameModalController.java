package GUI.JavaFx.RenameModal;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RenameModalController {
    @FXML
    public Button renameModalButton;
    @FXML
    public TextField renameModalTextField;

    private String newName;

    @FXML
    public void initialize(){
        Platform.runLater(() -> {renameModalTextField.requestFocus();});
    }


    public void onClickRenameModalButton(ActionEvent actionEvent) {
        String newName = renameModalTextField.getText();

        if (newName == null) {
            System.out.println("Enter new name!");
        } else {
            this.newName = newName;
            ((Stage) renameModalButton.getScene().getWindow()).close();
        }
    }

    public String  getNewName(){
        return this.newName;
    }


    public void isTypedEnterKey(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){
            onClickRenameModalButton(new ActionEvent());
        }
    }
}
