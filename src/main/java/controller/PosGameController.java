package controller;

import app.Main;

public class PosGameController {
    public void viewMenu() {
        Main.changeScreen("menu.fxml");
    }
    public void viewPreGame() {
        Main.changeScreen("pre-game.fxml");
    }

    public void viewPontuacao(){
        Main.changeScreen("pontuacao.fxml");
    }
}
