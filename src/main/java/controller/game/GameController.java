package controller.game;

import app.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import model.embarcacoes.Cruzador;
import model.embarcacoes.Encouracado;
import model.embarcacoes.PortaAvioes;
import model.embarcacoes.Submarino;
import model.uteis.Jogo;
import model.uteis.Orientacao;
import model.uteis.Resultado;
import model.uteis.Tabuleiro;

public class GameController {

    @FXML
    private GridPane playerBoard;
    @FXML
    public GridPane enemyBoard;

    private Jogo jogo;

    @FXML
    public void initialize() {
        jogo = new Jogo("Jogador 1", "Jogador 2");

        colocarNavios(jogo.getJogadorAtual().getTabuleiro());
        colocarNavios(jogo.getOponente().getTabuleiro());

        // tabuleiro do jogador: só visual, sem clique
        buildBoard(playerBoard, false);

        // tabuleiro inimigo: pode clicar
        buildBoard(enemyBoard, true);
    }

    private void colocarNavios(Tabuleiro tabuleiro) {
        tabuleiro.posicionarEmbarcacao(new PortaAvioes(), 0, 0, Orientacao.HORIZONTAL);
        tabuleiro.posicionarEmbarcacao(new Encouracado(), 2, 0, Orientacao.HORIZONTAL);
        tabuleiro.posicionarEmbarcacao(new Cruzador(), 4, 0, Orientacao.HORIZONTAL);
        tabuleiro.posicionarEmbarcacao(new Submarino(), 6, 0, Orientacao.HORIZONTAL);
    }


    private void buildBoard(GridPane grid, boolean clicavel) {
        int size = jogo.getJogadorAtual().getTabuleiro().getTamanho();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                Button cell = new Button();
                cell.setPrefSize(35, 35);

                if (clicavel) {
                    int r = row, c = col;
                    cell.setOnAction(e -> {
                        Resultado resultado = jogo.atacar(r, c);
                        atualizarCelula(cell, resultado);

                        if (jogo.acabou()) {
                            mostrarVencedor();
                        }
                    });
                }
                grid.add(cell, col, row);
            }
        }
    }

    private void atualizarCelula(Button cell, Resultado r) {
        switch (r) {
            case ACERTOU -> cell.setStyle("-fx-background-color: orange;");
            case AFUNDOU -> cell.setStyle("-fx-background-color: red;");
            case ERROU -> cell.setStyle("-fx-background-color:  blue;");
            case JA_ATACADO -> {
            }
        }
    }

    private void mostrarVencedor() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fim de jogo!");
        alert.setHeaderText(null);
        alert.setContentText("Vencedor: " + jogo.getJogadorAtual().getNome());
        alert.showAndWait();
        Main.changeScreen("menu.fxml");
    }

    public void viewMenu() {
        Main.changeScreen("menu.fxml");
    }
}