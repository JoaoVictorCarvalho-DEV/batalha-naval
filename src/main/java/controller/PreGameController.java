package controller;

import app.Main;
import app.Session;
import model.uteis.Jogo;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class PreGameController {

    @FXML
    private TextField txtNomeJogador;

    @FXML
    private Label lblError;

    @FXML
    private Button btnIniciar;

    @FXML
    public void initialize(){
        btnIniciar.disableProperty().bind(
            txtNomeJogador.textProperty().isEmpty()
        );
    }

    public void viewMenu() {
        Main.changeScreen("menu.fxml");
    }

    public void viewJogo() {
        String nomeJogador = txtNomeJogador.getText().trim();

        if (nomeJogador == null || nomeJogador.isEmpty()) {
            lblError.setText("Campo obrigatório. Por favor, insira o nome do jogador.");
            return;
        }

        Jogo jogo = new Jogo(nomeJogador, "Máquina");

        Session.getInstance().setJogoAtual(jogo);

        Main.changeScreen("game/game.fxml");
    }

}
