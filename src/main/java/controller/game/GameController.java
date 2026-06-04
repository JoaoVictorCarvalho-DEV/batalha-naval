package controller.game;

import java.time.LocalDateTime;

import app.Main;
import app.Session;
import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import model.embarcacoes.*;
import model.observer.EventManager;
import model.state.EstadoFinalizado;
import model.state.EstadoPartida;
import model.state.EstadoPosicionamento;
import model.state.EstadoPreJogo;
import model.strategy.AtaqueAleatorio;
import model.strategy.EstrategiaDeAtaque;
import model.uteis.*;
import components.StatusLabel;
import javafx.scene.Node;
import repository.PontuacaoRepository;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.util.Random;


public class GameController {

    @FXML
    private GridPane playerBoard;
    @FXML
    public GridPane enemyBoard;
    @FXML
    private StatusLabel labelInstrucoes;

    private EventManager eventManager;

    private Jogo jogo;
    private final EstrategiaDeAtaque estrategiaDeAtaque = new AtaqueAleatorio();

    // Variáveis para rastrear posicionamento;
    private boolean faseDePosicionamento = true;
    private int indiceNavioAtual = 0;
    private Embarcacao[] sequenciaDeNavios;
    private Orientacao orientacaoAtual = Orientacao.HORIZONTAL; // Horientação padrão
    private PontuacaoRepository pontuacaoRepository = new PontuacaoRepository(Database.getInstance().getConnection());

    private final Random random = new Random();

    @FXML
    public void initialize() {
        jogo = Session.getInstance().getJogoAtual();

        jogo.setEstado(new EstadoPosicionamento());
        System.out.println("[State] Estado atual: " + jogo.getEstado().getNome());

        eventManager = new EventManager();
        eventManager.addObserver(labelInstrucoes);

        // Sequência de posicionamento
        sequenciaDeNavios = new Embarcacao[] {

                new Cruzador(),
                new Encouracado(),
                new PortaAvioes(),

                new Submarino()
        };

        // Iniciando tabuleiros e jogadores
        System.out.println("Iniciando jogador:" + jogo.getJogadorAtual().getNome());
        buildBoard(playerBoard, jogo.getJogadorAtual().getTabuleiro(), true);
        jogo.alternarJogador();

        System.out.println("Iniciando jogador:" + jogo.getJogadorAtual().getNome());
        buildBoard(enemyBoard, jogo.getJogadorAtual().getTabuleiro(), false);
        jogo.alternarJogador();

        // Fase de posicionamento
        System.out.println("Fase de posicionamento! Monte sua frota clicando no tabuleiro.");
        System.out.println("Posicione agora: " + sequenciaDeNavios[indiceNavioAtual].getNome());
        atualizarLabelInstrucoes();

        // Captura a letra "R" para rotacionar a embarcação
        playerBoard.setOnKeyPressed(event -> {
            if (faseDePosicionamento) {
                if (event.getCode() == javafx.scene.input.KeyCode.R) {

                    Orientacao novaOrientacao = this.orientacaoAtual.rotacionar();
                    setOrientacaoAtual(novaOrientacao);
                    System.out.println("Nova orientação: " + this.orientacaoAtual);

                    atualizarLabelInstrucoes();
                }
            }
        });

        playerBoard.setFocusTraversable(true);
        playerBoard.requestFocus();
    }

    // Atualiza UI para mostrar orietação atual do usuário
    private void atualizarLabelInstrucoes() {
        if (faseDePosicionamento && indiceNavioAtual < sequenciaDeNavios.length) {
            String nomeNavio = sequenciaDeNavios[indiceNavioAtual].getNome();
            String direcao = obterTextoOrientacaoAmigavel();

            eventManager.notifyObservers(
                    new Evento(TipoEvento.INFO,
                            String.format(
                                    "FASE DE POSICIONAMENTO | Navio: %s | Orientação: %s [Pressione 'R' para Girar]",
                                    nomeNavio, direcao)));
        }
    }

    public void viewMenu() {
        Main.changeScreen("menu.fxml");
    }

    private void handleCellClick(
            GridPane grid,
            Tabuleiro tabuleiro,
            boolean isPlayerGrid,
            Button cell,
            int row,
            int col) {

        System.out.println("[State] Estado atual: " + jogo.getEstado().getNome());
        if (faseDePosicionamento) {
            handlePosicionamento(grid, tabuleiro, cell, row, col, isPlayerGrid);
            return;
        }

        handleCombate(cell, row, col, isPlayerGrid);
    }

    private void handleCombate(Button cell, int row, int col, boolean isPlayerGrid) {
        // =======================================================================
        // FASE DE COMBATE: TURNO DO JOGADOR 1
        // =======================================================================

        if (!isPlayerGrid) {
            Posicao posAlvo = jogo.getOponente().getTabuleiro().getPosicao(new Posicao(row, col));

            if (posAlvo.jaFoiAtacada()) {
                eventManager.notifyObservers(new Evento(TipoEvento.INFO, "Você já atacou essa célula! Escolha outra."));
                return;
            }

            Resultado resultado = jogo.atacar(row, col);
            atualizarCelula(cell, resultado);

            if (jogo.getOponente().getTabuleiro().todasEmbarcacoesDestruidas()) {
                eventManager.notifyObservers(
                        new Evento(TipoEvento.VITORIA, "VITÓRIA! Você destruiu toda a frota inimiga!"));
                enemyBoard.setDisable(true); // Freeze interface
                finalizarJogo();
                return;
            }

            if (resultado == Resultado.ERROU) {
                executarTurnoDaMaquina();
                eventManager.notifyObservers(new Evento(TipoEvento.INFO, "Máquina atacou."));
            } else {
                eventManager.notifyObservers(new Evento(TipoEvento.ACERTO, "Acertou! Ataque novamente!"));
            }
        }
    }

    private void handlePosicionamento(GridPane grid, Tabuleiro tabuleiro, Button cell, int row, int col,
            boolean isPlayerGrid) {
        // Ações do usuário no próprio tabuleiro
        if (isPlayerGrid && indiceNavioAtual < sequenciaDeNavios.length) {

            Embarcacao navioParaPosicionar = sequenciaDeNavios[indiceNavioAtual];
            boolean sucesso = tabuleiro.posicionarEmbarcacao(navioParaPosicionar, row, col,
                    orientacaoAtual);
            if (sucesso) {
                renderizarNavioNoGrid(grid, navioParaPosicionar, row, col, orientacaoAtual);

                // =======================================================================
                // LOGICA DE TESTE: Espelha o navio idêntico no Tabuleiro do Jogador 2
                // =======================================================================

                jogo.alternarJogador();
                Tabuleiro tabJogador2 = jogo.getJogadorAtual().getTabuleiro();
                // Instancia um novo navio do mesmo tipo para evitar referências duplicadas na
                // memória
                Embarcacao navioEspelho = clonarNavioParaTeste(navioParaPosicionar);
                boolean sucessoEspelho = tabJogador2.posicionarEmbarcacao(navioEspelho, row, col,
                        orientacaoAtual);
                System.out.println("[TESTE] Espelhando " + navioEspelho.getNome() + " no Player 2: "
                        + (sucessoEspelho ? "Sucesso" : "Falha"));
                jogo.alternarJogador();
                indiceNavioAtual++;

                // =======================================================================

                if (indiceNavioAtual < sequenciaDeNavios.length) {
                    atualizarLabelInstrucoes();
                    System.out
                            .println("Próximo navio: " + sequenciaDeNavios[indiceNavioAtual].getNome());
                } else {
                    System.out.println("Todos os navios posicionados! Fase de combate iniciada.");
                    jogo.setEstado(new EstadoPartida());
                    faseDePosicionamento = false;
                    eventManager.notifyObservers(
                            new Evento(TipoEvento.INFO,
                                    "FASE DE COMBATE! Sua vez de atacar: Escolha uma célula no tabuleiro inimigo."));

                }
            }
        }
    }

    private void buildBoard(GridPane grid, Tabuleiro tabuleiro, boolean isPlayerGrid) {
        int size = tabuleiro.getTamanho();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                Button cell = new Button();
                cell.setPrefSize(35, 35);

                int r = row;
                int c = col;

                cell.setOnAction(e -> handleCellClick(
                        grid,
                        tabuleiro,
                        isPlayerGrid,
                        cell,
                        r,
                        c));

                grid.add(cell, col, row);
            }
        }
    }

    // Ataque do jogador 2 (Máquina)
    private void executarTurnoDaMaquina() {
        // Bloqueia o tabuleiro inimigo para o jogador não clicar enquanto a máquina joga
        enemyBoard.setDisable(true);

        // Criamos uma tarefa em background para rodar o loop com delay de forma assíncrona
        Task<Void> turnoMaquinaTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Tabuleiro tabJogador = jogo.getJogadorAtual().getTabuleiro();
                boolean errou = false;

                do {
                    // 1. Gera o delay aleatório no range de 2 segundos (ex: entre 400ms e 2000ms)
                    long delayMs = 400 + (long) (random.nextDouble() * 1600);
                    Thread.sleep(delayMs);

                    // 2. Processa a lógica de ataque naEngine/Model
                    int[] alvo = estrategiaDeAtaque.calcularPosicaoDeAtaque(tabJogador);
                    int linhaAlvo = alvo[0];
                    int colunaAlvo = alvo[1];

                    Resultado resultadoAI = jogo.atacar(linhaAlvo, colunaAlvo);
                    System.out.printf("[MÁQUINA ATACOU] -> [%d, %d] - %s\n", linhaAlvo, colunaAlvo, resultadoAI);

                    if (resultadoAI == Resultado.ERROU) {
                        errou = true;
                    }

                    // 3. Modificações na Interface Gráfica (UI) precisam rodar dentro do Platform.runLater
                    Platform.runLater(() -> {
                        // Atualiza a célula atingida na tela
                        Button botaoJogador = obterBotaoNoGrid(playerBoard, linhaAlvo, colunaAlvo);
                        if (botaoJogador != null) {
                            atualizarCelula(botaoJogador, resultadoAI);
                        }

                        // Verifica fim de jogo
                        if (tabJogador.todasEmbarcacoesDestruidas()) {
                            eventManager.notifyObservers(
                                    new Evento(TipoEvento.DERROTA, "DERROTA! A Máquina destruiu todas as suas embarcações."));
                            enemyBoard.setDisable(true);
                            finalizarJogo();
                        }
                    });

                } while (!errou && !tabJogador.todasEmbarcacoesDestruidas());

                // 4. Quando o loop terminar (máquina errar), libera o tabuleiro para o jogador na UI Thread
                Platform.runLater(() -> {
                    if (!tabJogador.todasEmbarcacoesDestruidas()) {
                        enemyBoard.setDisable(false);
                    }
                });

                return null;
            }
        };

        // Inicializa a Thread em background para executar a Task
        Thread thread = new Thread(turnoMaquinaTask);
        thread.setDaemon(true); // Garante que a thread feche se a aplicação for encerrada
        thread.start();
    }

    // Método que pega a exata instancia do botão para ser manipulada durante o jogo
    private Button obterBotaoNoGrid(GridPane grid, int linha, int coluna) {
        for (Node node : grid.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);

            if (nodeCol != null && nodeRow != null && nodeCol == coluna && nodeRow == linha && node instanceof Button) {
                return (Button) node;
            }
        }
        return null;
    }

    // Muda a cor da célula de acordo com o ataque
    private void atualizarCelula(Button cell, Resultado r) {
        if (r == Resultado.ACERTOU) {
            cell.setStyle("-fx-background-color: red;");
        } else {
            cell.setStyle("-fx-background-color: gray;");
        }
    }

    // Metodo utilitário para rotacionar embarcação
    public void setOrientacaoAtual(Orientacao orientacao) {
        this.orientacaoAtual = orientacao;
    }

    // Método utilitário visual que altera estado da celula para indicar
    // posicionamento
    private void renderizarNavioNoGrid(GridPane grid, Embarcacao navio, int linha, int coluna, Orientacao o) {
        for (int i = 0; i < navio.getTamanho(); i++) {
            int targetL = linha;
            int targetC = coluna;

            if (o == Orientacao.VERTICAL)
                targetL = linha - i;
            if (o == Orientacao.HORIZONTAL)
                targetC = coluna + i;
            if (o == Orientacao.VERTICAL_INVERSA)
                targetL = linha + i;
            if (o == Orientacao.HORIZONTAL_INVERSA)
                targetC = coluna - i;

            for (javafx.scene.Node node : grid.getChildren()) {
                Integer nodeCol = GridPane.getColumnIndex(node);
                Integer nodeRow = GridPane.getRowIndex(node);

                if (nodeCol != null && nodeRow != null && nodeCol == targetC && nodeRow == targetL
                        && node instanceof Button) {
                    node.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
                    ((Button) node).setText(navio.getNome().substring(0, 1));
                }
            }
        }
    }

    // Factory method auxiliar para clonar a instância limpa do navio durante o loop
    // de testes
    private Embarcacao clonarNavioParaTeste(Embarcacao navio) {
        if (navio instanceof Cruzador)
            return new Cruzador();
        if (navio instanceof Encouracado)
            return new Encouracado();
        if (navio instanceof PortaAvioes)
            return new PortaAvioes();
        return new Submarino();
    }

    public void setEnemyBoard(GridPane enemyBoard) {
        this.enemyBoard = enemyBoard;
    }

    public void setSequenciaDeNavios(Embarcacao[] sequenciaDeNavios) {
        this.sequenciaDeNavios = sequenciaDeNavios;
    }

    // Traduzindo horientação para interface
    private String obterTextoOrientacaoAmigavel() {
        if (this.orientacaoAtual == null)
            return "Não definida";
        switch (this.orientacaoAtual) {
            case HORIZONTAL:
                return "Direita (Horizontal)";
            case VERTICAL:
                return "Cima (Vertical)";
            case HORIZONTAL_INVERSA:
                return "Esquerda (Horizontal Inversa)";
            case VERTICAL_INVERSA:
                return "Baixo (Vertical Inversa)";
            default:
                return this.orientacaoAtual.name();
        }
    }

    private void finalizarJogo() {
        jogo.setEstado(new EstadoFinalizado());
        jogo.finalizarJogo();

        long duracaoSegundos = jogo.getDuracao();
        
        // Verifica quem venceu para determinar o nome do vencedor no objeto Pontuacao
        String nomeVencedor;
        if (jogo.getOponente().getTabuleiro().todasEmbarcacoesDestruidas()) {
            nomeVencedor = jogo.getJogadorAtual().getNome(); // Jogador 1 ganhou
        } else {
            nomeVencedor = jogo.getOponente().getNome(); // Máquina ganhou
        }

        // Cria o objeto Pontuacao
        Pontuacao pontuacao = new Pontuacao(
                jogo.getJogadorAtual().getNome(),
                jogo.getOponente().getNome(),
                nomeVencedor,
                (jogo.getJogadorAtual().getTiros() + jogo.getOponente().getTiros()),
                duracaoSegundos,
                LocalDateTime.now());

        // Salva no banco de dados
        boolean salvou = pontuacaoRepository.salvarPontuacao(pontuacao);

        if (salvou) {
            mostrarMensagem("Pontuação salva com sucesso!", Alert.AlertType.INFORMATION);
        } else {
            mostrarMensagem("Erro ao salvar pontuação!", Alert.AlertType.ERROR);
        }

        Main.changeScreen("pos-game.fxml");
    }

    private void mostrarMensagem(String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
