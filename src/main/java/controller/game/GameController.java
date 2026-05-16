package controller.game;

import app.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import model.embarcacoes.Encouracado;
import model.embarcacoes.PortaAvioes;
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

        System.out.println("Iniciar jogador:" + jogo.getJogadorAtual().getNome());
        buildBoard(playerBoard, jogo.getJogadorAtual().getTabuleiro());
        jogo.alternarJogador();

        System.out.println("Iniciar jogador:" + jogo.getJogadorAtual().getNome());
        buildBoard(enemyBoard, jogo.getJogadorAtual().getTabuleiro());
        jogo.alternarJogador();

        jogo.getJogadorAtual().tabuleiro.posicionarEmbarcacao(new Encouracado(), 1,4, Orientacao.HORIZONTAL);
        jogo.getJogadorAtual().tabuleiro.posicionarEmbarcacao(new PortaAvioes(), 6,5, Orientacao.HORIZONTAL_INVERSA);

    }

    public void viewMenu(){
        Main.changeScreen("menu.fxml");
    }


    private void buildBoard(GridPane grid, Tabuleiro tabuleiro) {
        int size = tabuleiro.getTamanho();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                Button cell = new Button();
                cell.setPrefSize(35, 35);

                int r = row;
                int c = col;

                cell.setOnAction(e -> {
                    Resultado resultado = tabuleiro.receberAtaque(r, c);
                    atualizarCelula(cell, resultado);
                });

               grid.add(cell, col, row);
            }
        }
    }


    private void atualizarCelula(Button cell, Resultado r){
        if(r == Resultado.ACERTOU){
            cell.setStyle("-fx-background-color: red;");
        } else {
            cell.setStyle("-fx-background-color: gray;");
        }
    }
}
