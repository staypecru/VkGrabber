package GUI;

import com.company.User;

import javax.swing.*;
import java.util.List;

public class UsersListModel extends AbstractListModel<User> {


    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public User getElementAt(int index) {
        return null;
    }

    public UsersListModel() {
    }
    public void addAll(List<User> users) {

    }
}
