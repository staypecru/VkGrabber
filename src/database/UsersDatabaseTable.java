package database;

import com.company.User;
import javafx.collections.ObservableList;

import java.sql.*;

public class UsersDatabaseTable {

    private static final String USER = "root";
    private static final String PASSWORD = "JHM74defla";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306";
    private static final String DATABASE_NAME = "VKGRABBER";
    private static final String DATABASE_PROPERTIES = "?serverTimezone=Europe/Moscow&useSSL=false";
    private static final String CREATE_TABLE_START = "CREATE TABLE ";
    private static final String CREATE_TABLE_FINISH = "\n(\n" +
            "  id integer NOT NULL,\n" +
            "  name text NOT NULL,\n" +
            "  surname text NOT NULL,\n" +
            "  photo text,\n" +
            "  CONSTRAINT id PRIMARY KEY (id)\n" +
            ");";


    public static void createOrRefillCurrentTable(ObservableList<User> users, String tableName) {

        //        @param tableName  - is UserId_PostId; (temporary)
        //        TODO: find better way to name table


        String CreateTableRequest = CREATE_TABLE_START + tableName + CREATE_TABLE_FINISH;
        try {
            Connection connection = DriverManager.getConnection(DB_URL +
                            "/" +
                            DATABASE_NAME +
                            DATABASE_PROPERTIES,
                    USER,
                    PASSWORD);

            Statement statement = connection.createStatement();
            statement.execute(CreateTableRequest);

            DatabaseUtils.fillTableWithUsers(users, tableName, statement);

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
