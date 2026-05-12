package controller;

import app.Main;

public class MenuController {

    public void jogar() {
        Main.changeScreen("setup.fxml");
    }

    public void sair() {
        System.exit(0);
    }
}