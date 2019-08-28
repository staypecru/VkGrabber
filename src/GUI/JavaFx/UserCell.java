package GUI.JavaFx;

import com.company.User;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.awt.image.BufferedImage;

/**
 * Create a pattern for a row in ListView
 * It looks like ImageView NameTextView SurnameTextView
 */
class UserCell extends ListCell<User> {
    Controller controller;

    public UserCell(Controller controller) {
        this.controller = controller;
    }

    @Override
    protected void updateItem(User user, boolean b) {
        if (b || user == null ) {
//            setText(null);
            setGraphic(null);
        }
//          TODO:  This is quite bad! checking on the name Thread, but how to solve
//          TODO:  problem with trying to update items in sub Threads???
        if (user != null && Thread.currentThread().getName() == "JavaFX Application Thread") {


            super.updateItem(user, b);
            GridPane userRow = new GridPane();

            userRow.add(new Text(user.getName() + " "), 1, 0);
            userRow.add(new Text(user.getSurname() + " "), 2, 0);

            controller.userPhotoView = setUserImage(user);

            userRow.add(controller.userPhotoView, 0, 0);

            this.setGraphic(userRow);
        }
    }
    private ImageView setUserImage(User user) {
        BufferedImage bufferedUserPhoto = user.getUserPhoto();
        Image userPhoto = SwingFXUtils.toFXImage(bufferedUserPhoto, new WritableImage(40, 40));


        controller.userPhotoView = new ImageView();
        controller.userPhotoView.setFitHeight(40);
        controller.userPhotoView.setFitWidth(40);
        controller.userPhotoView.setImage(userPhoto);

        return controller.userPhotoView;
    }
}
