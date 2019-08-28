package GUI;

import com.company.QueryUtils;
import com.company.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

public class InterfaceForm extends JFrame{

    private JPanel mainPanel;
    private JTextField inputTextField;
    private JButton button1;
    private JList usersList;

    public InterfaceForm() {
        setContentPane(mainPanel);
        setVisible(true);

        final DefaultListModel usersListModel = new DefaultListModel();

        usersList.setModel(usersListModel);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {



//                TODO: Put users data to list1;
            }
        });
    }




}
