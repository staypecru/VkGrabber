package database;

import com.company.User;
import javafx.collections.ObservableList;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseUtils {
    private static final String USER = "root";
    private static final String PASSWORD = "JHM74defla";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306";
    private static final String DATABASE_NAME = "vkgrabber";
    private static final String DATABASE_PROPERTIES = "?serverTimezone=Europe/Moscow&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String VALUES = "VALUES";
    private static final String PHOTOS_DIRECTORY = "src/R/photos/";
    private static final String SELECT_FROM = "SELECT * FROM ";
    private static final String DROP_TABLE = "DROP TABLE vkgrabber.";
    private static final String ALTER_TABLE = "ALTER TABLE ";
    private static final String RENAME_TO = " RENAME TO ";


    //    Table has 4 parameters: Id, Name, Surname, PhotoLink (photos sorted by usersId in src/R/photos);
    public static void fillTableWithUsers(ObservableList<User> users, String tableName, Statement statement) throws SQLException {
        for (User user : users) {
            String insertUserRequest = INSERT_INTO + tableName + " " + VALUES +
                    "(" + user.getId() +
                    ", '" + user.getName() + "'" +
                    ", '" + user.getSurname() + "'" +
                    ", '" + PHOTOS_DIRECTORY + user.getId() + ".jpg" + "')";
            statement.execute(insertUserRequest);
        }
    }

    public static void createDatabaseVkGrabberIfNoExists() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL + DATABASE_PROPERTIES, USER, PASSWORD);
            if (isDatabaseExists(connection)) {
                connection.close();
                return;
            }
            createDatabaseVkGrabber(connection);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static boolean isDatabaseExists(Connection connection) throws SQLException {
        ResultSet resultSet = connection.getMetaData().getCatalogs();
        //iterate each catalog in the ResultSet
        while (resultSet.next()) {
            // Get the database name, which is at position 1
            String databaseName = resultSet.getString(1);
            if (databaseName.equals(DATABASE_NAME)) {
                resultSet.close();
                return true;
            }
        }
        resultSet.close();
        return false;
    }


    private static void createDatabaseVkGrabber(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE DATABASE " + DATABASE_NAME);
    }


    public static ArrayList<String> getBlocksFromDatabase() {
        ArrayList<String> blocks = new ArrayList<>();
        try {
            Connection connection = createConnection();
            blocks = getBlocksNames(connection);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return blocks;
    }

    private static ArrayList<String> getBlocksNames(Connection connection) throws SQLException {
        ArrayList<String> blocks = new ArrayList<>();
//            Table name has index - 3;
        ResultSet resultSet = connection.getMetaData().getTables(DATABASE_NAME, null, "%", null);
        while (resultSet.next()) {
            blocks.add(resultSet.getString(3));
        }
        return blocks;
    }


    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL + "/" + DATABASE_NAME + DATABASE_PROPERTIES, USER, PASSWORD);
    }

    public static ArrayList<User> downloadUsersFromBlock(String blockName) {
        ArrayList<User> users = new ArrayList<>();
        try {
            Connection connection = createConnection();
            users = returnUsersFromTable(blockName, connection);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    private static ArrayList<User> returnUsersFromTable(String blockName, Connection connection) throws SQLException, IOException {
        Statement statement = connection.createStatement();
        ArrayList<User> users = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(SELECT_FROM + blockName);
        while (resultSet.next()) {

            File photo = getPhotoAddress(resultSet);

            FileInputStream fileInputStream = new FileInputStream(photo);

            BufferedImage bufferedPhoto = ImageIO.read(fileInputStream);

            User user = new User(resultSet.getLong(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    bufferedPhoto);

            users.add(user);
        }
        return users;
    }

    private static File getPhotoAddress(ResultSet resultSet) throws SQLException {
        return new File(resultSet.getString(4));
    }


    public static void deleteBlock(String blockName) {
        try {
            Connection connection = createConnection();
            deletePhotosAndTable(blockName, connection);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deletePhotosAndTable(String blockName, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        deletePhotos(blockName, statement);
        statement.execute(DROP_TABLE + blockName);
    }


    private static void deletePhotos(String blockName, Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery(SELECT_FROM + blockName);
        while (resultSet.next()) {
            File photo = getPhotoAddress(resultSet);
            System.out.println(photo.delete());
        }
    }

    public static void renameTable(String oldName, String newName){
        try {
            Connection connection = createConnection();
            Statement statement = connection.createStatement();
//            TODO: do not rename tables with names from SQL syntax ('load', 'table')
            statement.execute(ALTER_TABLE + oldName + RENAME_TO + newName);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
