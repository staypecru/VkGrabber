package GUI.JavaFx;

import GUI.JavaFx.RenameModal.RenameModalGUI;
import com.company.QueryUtils;
import com.company.User;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import database.DatabaseUtils;
import database.UsersDatabaseTable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;


public class Controller {

    @FXML
    private TextField inputTextField;

    @FXML
    private ListView<User> usersListView;

    @FXML
    public ListView blockListView;

    @FXML
    private ProgressIndicator usersProgressIndicator;

    @FXML
    private Button findUsersButton;

    @FXML
    public Button saveButton;

    @FXML
    public Button downloadButton;

    @FXML
    public Button deleteButton;
    @FXML
    public Button renameButton;

    public static ImageView userPhotoView;

    private ObservableList<User> users;

    private static volatile ArrayList<User> usersArrayList;

    private ObservableList<String> blocks;

    private static final String PHOTOS_DIRECTORY = "src/R/photos/";
    private static final String EXPORT_DIRECTORY = "exportFiles/";

    @FXML
    public void initialize() {

        initializeListViews();

        DatabaseUtils.createDatabaseVkGrabberIfNoExists();

    }

    private void initializeListViews() {
        initializeUsersListView();

        initializeBlocksListView();
    }


    private void initializeUsersListView() {
        users = FXCollections.observableList(new ArrayList<>());

        users.addListener((ListChangeListener<User>) change -> fillListView(usersListView, users));

        usersListView.setCellFactory(usersListView -> new UserCell(this));


    }

    private void initializeBlocksListView() {
        blocks = FXCollections.observableList(new ArrayList<>());

        blocks.addListener((ListChangeListener<String>) change -> fillListView(blockListView, blocks));

        blockListView.setCellFactory(tablesListView -> new BlockCell(this));

        blocks.addAll(DatabaseUtils.getBlocksFromDatabase());

    }



    private void fillListView(ListView listView, ObservableList items) {
        if (items.size() != 0) listView.setItems(items);
    }


    public void clickFindUsersButton(ActionEvent actionEvent) {
        users.clear();

        createAsyncThreadToFindAndAddUsers();

        System.out.println(users);
    }

    private void createAsyncThreadToFindAndAddUsers() {
        ExecutorService executorService = Executors.newCachedThreadPool();


        executorService.execute(() -> {
            turnOnProgressIndicator(usersProgressIndicator);

            findAndAddUsers();

            turnOffProgressIndicator(usersProgressIndicator);
        });

        executorService.shutdown();
    }


    private void turnOnProgressIndicator(ProgressIndicator progressIndicator) {
        progressIndicator.setVisible(true);
    }

    private void turnOffProgressIndicator(ProgressIndicator progressIndicator) {
        progressIndicator.setVisible(false);
    }


    private void findAndAddUsers() {

        usersArrayList = QueryUtils.getListOfUsersFromUrlRequest(getLinkAddress());

        users.addAll(usersArrayList);

    }


    private String getLinkAddress() {
        return inputTextField.getText();
    }


    public void clickSaveButton(ActionEvent actionEvent) {

        createAsyncThreadToSaveBlock();

    }

    private void createAsyncThreadToSaveBlock() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            saveBlock();
        });
        executorService.shutdown();
    }

    private void saveBlock() {
        String tableName = getTableName();

        blocks.add(tableName);

        UsersDatabaseTable.createOrRefillCurrentTable(users, tableName);

        for (User user : users) {
            File file = new File(PHOTOS_DIRECTORY + user.getId() + ".jpg");
            try {
                ImageIO.write(user.getUserPhoto(), "jpg", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getTableName() {
        String tableName;
        if (textFieldIsEmpty(inputTextField)) {
            tableName = getSelectedBlockName();
        } else {
            tableName = getTableNameFromInputTextField();
        }
        return tableName;
    }

    private boolean textFieldIsEmpty(TextField textField) {
        return textField.getText().isEmpty();
    }

    private String getTableNameFromInputTextField() {
        String tableName;
        tableName = parseTableNameFromInputTextField();

        if (isPostFromGroup(tableName)) {
            tableName = noteNameWithSymbolGAndDeleteMinus(tableName);
        }
        return tableName;
    }

    private String parseTableNameFromInputTextField() {
        String linkAddress = getLinkAddress();

        String[] secondPartOfRequestAddress = linkAddress.split("/?w=wall");

        return secondPartOfRequestAddress[1];
    }


    private boolean isPostFromGroup(String tableName) {
        return tableName.charAt(0) == '-';
    }

    private String noteNameWithSymbolGAndDeleteMinus(String tableName) {
        tableName = "g" + tableName.substring(1);
        return tableName;
    }


    public void clickDownloadButton(ActionEvent actionEvent) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            downloadBlock();
        });
        executorService.shutdown();
    }

    private void downloadBlock() {
        users.clear();
        String blockName = getSelectedBlockName();
        users.addAll(DatabaseUtils.downloadUsersFromBlock(blockName));
    }


    private String getSelectedBlockName() {
        return getSelectedBlock().toString();
    }

    private Object getSelectedBlock() {
        return blockListView.getSelectionModel().getSelectedItem();
    }


    public void onClickDeleteButton(ActionEvent actionEvent) {
        deleteBlock();
    }

    private void deleteBlock() {
        String blockName = getSelectedBlockName();

        blocks.remove(blockName);

        DatabaseUtils.deleteBlock(blockName);
    }


    public void onClickRenameButton(ActionEvent actionEvent) {
        String blockName = getSelectedBlockName();
        String newName = getNewNameFromModalWindow();
        if (!newName.isEmpty()) {
            DatabaseUtils.renameTable(blockName, newName);
            renameBlock(blockName, newName);
        }
    }


    /**
     * @return String from input in modal window, if there is no text - return empty String
     **/
    private String getNewNameFromModalWindow() {
        RenameModalGUI renameModalGUI = new RenameModalGUI();
        Stage modal = initModalStage();
        return openModalWindowAndReturnNewName(renameModalGUI, modal);
    }

    private Stage initModalStage() {
        Stage modal = new Stage();
        modal.initOwner(renameButton.getScene().getWindow());
        modal.initModality(Modality.APPLICATION_MODAL);
        return modal;
    }

    private String openModalWindowAndReturnNewName(RenameModalGUI renameModalGUI, Stage modal) {
        String newName = "";
        try {
            newName = renameModalGUI.startWithReturn(modal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newName;
    }

    private void renameBlock(String oldName, String newName) {
        blocks.remove(oldName);
        blocks.add(newName);
    }

    public void onClickExportIdsButton(ActionEvent actionEvent) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            exportIds();
        });
        executorService.shutdown();
    }

    private void exportIds() {
        createExportDirectory();
        File exportFile = new File(EXPORT_DIRECTORY + getTableName() + "_id.txt");

        try {
            createFileIfNotExists(exportFile);

            writeUsersIdToFile(exportFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createExportDirectory() {
        new File(EXPORT_DIRECTORY).mkdir();
    }

    private void writeUsersIdToFile(File exportFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(exportFile));
        for (User user : users) {
            writer.write(user.getId().toString());
            writer.newLine();
        }
        writer.close();
    }

    private void createFileIfNotExists(File exportFile) throws IOException {
        if (exportFile.exists()) {
            exportFile.createNewFile();
        }
    }

    public void onClickExportUsersButton(ActionEvent actionEvent) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            exportUsers();
        });
        executorService.shutdown();
    }

    private void exportUsers() {
        createExportDirectory();
        File exportFile = new File(EXPORT_DIRECTORY + getTableName() + "_Users.txt");

        try {
            createFileIfNotExists(exportFile);

            writeUsersInfoToFile(exportFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeUsersInfoToFile(File exportFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(exportFile));
        for (User user : users) {
            writer.write(user.getName() + " " + user.getSurname() );
            writer.newLine();
        }
        writer.close();
    }
}
