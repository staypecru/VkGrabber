package GUI.JavaFx;

import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class BlockCell extends ListCell<String> {
    Controller controller;

    public BlockCell(Controller controller) {
        this.controller = controller;
    }

    @Override
    protected void updateItem(String block, boolean b) {
        if (b) {
            setText(null);
            setGraphic(null);
        }
        if (block != null && Thread.currentThread().getName() == "JavaFX Application Thread") {
            super.updateItem(block, b);
            GridPane userRow = new GridPane();

            userRow.add(new Text(block), 1, 0);

            this.setGraphic(userRow);
        }
    }
}
