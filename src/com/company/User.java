package com.company;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class User {
    private Long id;
    private String name;
    private String surname;
    private BufferedImage userPhoto;



    public User(String name, String surname, BufferedImage userPhoto) {
        this.name = name;
        this.surname = surname;
        this.userPhoto = userPhoto;
    }

    public User(Long id, String name, String surname, BufferedImage userPhoto) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.userPhoto = userPhoto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public BufferedImage getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(BufferedImage userPhoto) {
        this.userPhoto = userPhoto;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", userPhoto=" + userPhoto +
                '}';
    }
}
