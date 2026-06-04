package controller;

import java.net.URL;
import app.Main;
import app.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import model.uteis.Jogo;
import model.uteis.Pontuacao;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class PosGameController implements Initializable {

    @FXML
    private TableView<Pontuacao> tableViewFimDeJogo;

    @FXML
    private TableColumn<Pontuacao, String> colJogador1;
    @FXML
    private TableColumn<Pontuacao, String> colJogador2;

    @FXML
    private TableColumn<Pontuacao, String> colVencedor;

    @FXML
    private TableColumn<Pontuacao, Integer> colNumeroJogadas;

    @FXML
    private TableColumn<Pontuacao, Long> colDuracao;

    private Jogo jogo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jogo = Session.getInstance().getJogoAtual();

        colJogador1.setCellValueFactory(
                new PropertyValueFactory<>("jogador1"));

        colJogador2.setCellValueFactory(
                new PropertyValueFactory<>("jogador2"));
        colVencedor.setCellValueFactory(new PropertyValueFactory<>("vencedor"));
        colNumeroJogadas.setCellValueFactory(new PropertyValueFactory<>("numeroJogadas"));
        colDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));

        carregarFimDeJogo();
    }

    private void carregarFimDeJogo() {
        ObservableList<Pontuacao> dados = FXCollections.observableArrayList();

        dados.add(new Pontuacao(
                jogo.getJogadorAtual().getNome(),
                jogo.getOponente().getNome(),
                jogo.getJogadorAtual().getNome(),
                jogo.getJogadorAtual().getTiros() + jogo.getOponente().getTiros(),
                jogo.getDuracao(),
                LocalDateTime.now()));

        tableViewFimDeJogo.setItems(dados);
    }

    public void viewMenu() {
        Main.changeScreen("menu.fxml");
    }

    public void viewPreGame() {
        Main.changeScreen("pre-game.fxml");
    }

    public void viewPontuacao() {
        Main.changeScreen("pontuacao.fxml");
    }
}
