package controller;

import app.Main;

public class MenuController {

    public void view_jogo() {
        Main.changeScreen("setup.fxml");
    }

    public void viewPontuacao(){
        Main.changeScreen("pontuacao.fxml");
    }

    public void sair() {
        System.exit(0);
    }
}