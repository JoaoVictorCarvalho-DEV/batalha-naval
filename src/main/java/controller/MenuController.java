package controller;

import app.Main;

public class MenuController {



    public void viewJogo() {
        Main.changeScreen("pre-game.fxml");
    }

    public void viewPontuacao(){
        Main.changeScreen("pontuacao.fxml");
    }

    public void sair() {
        System.exit(0);
    }
}