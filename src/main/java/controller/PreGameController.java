package controller;

import app.Main;


public class PreGameController {

    public void viewMenu(){
        Main.changeScreen("menu.fxml");
    }

    public void viewJogo(){
        Main.changeScreen("game/game.fxml");
    }
}
