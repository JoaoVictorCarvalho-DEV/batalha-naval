package controller.game;

import app.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import model.embarcacoes.*;
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

    // Variáveis para rastrear posicionamento;
    private boolean faseDePosicionamento = true;
    private int indiceNavioAtual = 0;
    private Embarcacao[] sequenciaDeNavios;
    private Orientacao orientacaoAtual = Orientacao.HORIZONTAL; // Horientação padrão

    @FXML
    public void initialize() {
        jogo = new Jogo("Jogador 1", "Jogador 2");

        sequenciaDeNavios = new Embarcacao[]{
                new Cruzador(),
                new Encouracado(),
                new PortaAvioes(),
                new Submarino()
        };

        System.out.println("Fase de posicionamento! Monte sua frota clicando no tabuleiro.");
        System.out.println("Posicione agora: " + sequenciaDeNavios[indiceNavioAtual].getNome());

        System.out.println("Iniciar jogador:" + jogo.getJogadorAtual().getNome());
        buildBoard(playerBoard, jogo.getJogadorAtual().getTabuleiro(), true);

        jogo.alternarJogador();

        System.out.println("Iniciar jogador:" + jogo.getJogadorAtual().getNome());
        buildBoard(enemyBoard, jogo.getJogadorAtual().getTabuleiro(), false);

        jogo.alternarJogador();

    }

    public void viewMenu(){
        Main.changeScreen("menu.fxml");
    }


    private void buildBoard(GridPane grid, Tabuleiro tabuleiro, boolean isPlayerGrid) {
        int size = tabuleiro.getTamanho();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                Button cell = new Button();
                cell.setPrefSize(35, 35);

                int r = row;
                int c = col;

                cell.setOnAction(e -> {
                    // Fase de posicionamento: Ações posicionam embaração no tabuleiro
                    if (faseDePosicionamento) {

                        // Ações do usuário no próprio tabuleiro
                        if (isPlayerGrid && indiceNavioAtual < sequenciaDeNavios.length) {
                            Embarcacao navioParaPosicionar = sequenciaDeNavios[indiceNavioAtual];

                            boolean sucesso = tabuleiro.posicionarEmbarcacao(navioParaPosicionar, r, c, orientacaoAtual);

                            if (sucesso) {
                                // 1. Renderiza visualmente no grid do Jogador 1
                                renderizarNavioNoGrid(grid, navioParaPosicionar, r, c, orientacaoAtual);

                                // =======================================================================
                                // LOGICA DE TESTE: Espelha o navio idêntico no Tabuleiro do Jogador 2
                                // =======================================================================
                                jogo.alternarJogador(); // Vai para o Jogador 2
                                Tabuleiro tabJogador2 = jogo.getJogadorAtual().getTabuleiro();

                                // Instancia um novo navio do mesmo tipo para evitar referências duplicadas na memória
                                Embarcacao navioEspelho = clonarNavioParaTeste(navioParaPosicionar);
                                boolean sucessoEspelho = tabJogador2.posicionarEmbarcacao(navioEspelho, r, c, orientacaoAtual);

                                System.out.println("[TESTE] Espelhando " + navioEspelho.getNome() + " no Player 2: " + (sucessoEspelho ? "Sucesso" : "Falha"));

                                jogo.alternarJogador(); // Retorna o contexto ao Jogador 1
                                // =======================================================================
                                indiceNavioAtual++; // Próximo navio da lista

                                if (indiceNavioAtual < sequenciaDeNavios.length) {
                                    System.out.println("Próximo navio: " + sequenciaDeNavios[indiceNavioAtual].getNome());
                                } else {
                                    System.out.println("Todos os navios posicionados! Fase de combate iniciada.");
                                    faseDePosicionamento = false;
                                }
                            }
                        }
                    } else {
                        // Fase de combate: ações atacam o tabuleiro adversário
                        if (!isPlayerGrid){
                            Resultado resultado = tabuleiro.receberAtaque(r, c);
                            atualizarCelula(cell, resultado);
                        }
                    }
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

    // Metodo utilitário para rotacionar embarcação
    public void setOrientacaoAtual(Orientacao orientacao) {
        this.orientacaoAtual = orientacao;
    }

    // Método utilitário visuall para que altera estado da celula para indicar posicionamento
    private void renderizarNavioNoGrid(GridPane grid, Embarcacao navio, int linha, int coluna, Orientacao o) {
        for (int i = 0; i < navio.getTamanho(); i++) {
            int targetL = linha;
            int targetC = coluna;

            if (o == Orientacao.VERTICAL) targetL = linha - i;
            if (o == Orientacao.HORIZONTAL) targetC = coluna + i;
            if (o == Orientacao.VERTICAL_INVERSA) targetL = linha + i;
            if (o == Orientacao.HORIZONTAL_INVERSA) targetC = coluna - i;

            for (javafx.scene.Node node : grid.getChildren()) {
                Integer nodeCol = GridPane.getColumnIndex(node);
                Integer nodeRow = GridPane.getRowIndex(node);

                if (nodeCol != null && nodeRow != null && nodeCol == targetC && nodeRow == targetL && node instanceof Button) {
                    node.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
                    ((Button) node).setText(navio.getNome().substring(0, 1));
                }
            }
        }
    }
    // Factory method auxiliar para clonar a instância limpa do navio durante o loop de testes
    private Embarcacao clonarNavioParaTeste(Embarcacao navio) {
        if (navio instanceof Cruzador) return new Cruzador();
        if (navio instanceof Encouracado) return new Encouracado();
        if (navio instanceof PortaAvioes) return new PortaAvioes();
        return new Submarino();
    }
}
