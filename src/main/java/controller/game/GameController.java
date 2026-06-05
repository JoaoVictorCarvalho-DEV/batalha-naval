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
import model.strategy.AtaqueAleatorio;
import model.strategy.AtaqueInteligente;
import model.strategy.EstrategiaDeAtaque;
import model.uteis.*;
import components.StatusLabel;
import javafx.scene.Node;
import repository.PontuacaoRepository;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.util.Random;
import controller.game.command.AcaoCommand;
import controller.game.command.AtacarCommand;
import java.util.Stack;


public class GameController {

    @FXML
    private GridPane playerBoard;
    @FXML
    public GridPane enemyBoard;
    @FXML
    private StatusLabel labelInstrucoes;

    private EventManager eventManager;

    private Jogo jogo;
    private final AtaqueAleatorio estrategiaAleatoria = new AtaqueAleatorio();
    private final AtaqueInteligente estrategiaInteligente = new AtaqueInteligente();
    private EstrategiaDeAtaque estrategiaDeAtaque = estrategiaAleatoria;

    // Variáveis para rastrear posicionamento;
    private boolean faseDePosicionamento = true;
    private int indiceNavioAtual = 0;
    private Embarcacao[] sequenciaDeNavios;
    private Orientacao orientacaoAtual = Orientacao.HORIZONTAL; // Horientação padrão
    private PontuacaoRepository pontuacaoRepository = new PontuacaoRepository(Database.getInstance().getConnection());

    private final Random random = new Random();
    private final Stack<AcaoCommand> historicoComandos = new Stack<>();

    @FXML
    public void initialize() {
        jogo = Session.getInstance().getJogoAtual();

        jogo.setEstado(new EstadoPosicionamento());
        System.out.println("[State] Estado atual: " + jogo.getEstado().getNome());

        eventManager = new EventManager();
        eventManager.addObserver(labelInstrucoes);

        // Sequência de posicionamento + chamada para o Factory de embarcações.
        String[] tiposDeNavios = {"Cruzador", "Encouracado", "PortaAvioes", "Submarino"};
        sequenciaDeNavios = new Embarcacao[tiposDeNavios.length];
        for (int i = 0; i < tiposDeNavios.length; i++) {
            sequenciaDeNavios[i] = EmbarcacaoFactory.criar(tiposDeNavios[i]);
        }

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

    // =======================================================================
    // FASE DE COMBATE: TURNO DO JOGADOR 1
    // =======================================================================
    private void handleCombate(Button cell, int row, int col, boolean isPlayerGrid) {
        if (!isPlayerGrid) {
            Posicao posAlvo = jogo.getOponente().getTabuleiro().getPosicao(new Posicao(row, col));

            if (posAlvo.jaFoiAtacada()) {
                eventManager.notifyObservers(new Evento(TipoEvento.INFO, "Você já atacou essa célula! Escolha outra."));
                return;
            }

           // Instancia o comando encapsulando a intenção do clique
            AtacarCommand comandoAtaque = new AtacarCommand(jogo, row, col, cell);

            // Executa o comando
            comandoAtaque.executar();

            // Guarda no histórico para permitir desfazer futuramente
            historicoComandos.push(comandoAtaque);

            if (jogo.getOponente().getTabuleiro().todasEmbarcacoesDestruidas()) {
                eventManager.notifyObservers(
                        new Evento(TipoEvento.VITORIA, "VITÓRIA! Você destruiu toda a frota inimiga!"));
                enemyBoard.setDisable(true); // Freeze interface
                finalizarJogo();
                return;
            }

            if (comandoAtaque.getResultadoObtido() == Resultado.ERROU) {
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
                // LÓGICA DE POSICIONAMENTO ASSÍMETRICO E INTELIGENTE: JOGADOR 2 (MÁQUINA)
                // =======================================================================
                jogo.alternarJogador();
                Tabuleiro tabJogador2 = jogo.getJogadorAtual().getTabuleiro();
                Embarcacao navioEspelho = EmbarcacaoFactory.criar(navioParaPosicionar.getNome());

                boolean sucessoEspelho = false;

                // Definição dos deslocamentos que tentaremos aplicar em relação à jogada do Player 1
                // Tentamos mover o navio levemente para não expor na mesma posição geográfica
                int[] modificadoresLinha =  { 0, +1, -1,  0, +2, -2 };
                int[] modificadoresColuna = { +1, 0,  0, -1, +1, -2 };
                Orientacao[] orientacoesPossiveis = Orientacao.values();

                // Busca linear adaptativa: tenta encontrar uma região livre e válida nas proximidades
                for (int i = 0; i < modificadoresLinha.length && !sucessoEspelho; i++) {
                    int novaLinha = row + modificadoresLinha[i];
                    int novaColuna = col + modificadoresColuna[i];

                    // Varre as orientações disponíveis silenciosamente para garantir o encaixe dentro das barreiras
                    for (Orientacao orientacaoTeste : orientacoesPossiveis) {

                        // Validação preventiva de limites do tabuleiro para evitar index out of bounds
                        if (novaLinha >= 0 && novaLinha < tabJogador2.getTamanho() &&
                                novaColuna >= 0 && novaColuna < tabJogador2.getTamanho()) {

                            sucessoEspelho = tabJogador2.posicionarEmbarcacao(navioEspelho, novaLinha, novaColuna, orientacaoTeste);

                            if (sucessoEspelho) {
                                System.out.printf("[AI POSICIONAMENTO] %s posicionado assimetricamente em [%d, %d] com orientacao %s\n",
                                        navioEspelho.getNome(), novaLinha, novaColuna, orientacaoTeste.name());
                                break;
                            }
                        }
                    }
                }

                // Fallback de Segurança Crítica: Caso todas as vizinhanças falhem por alta densidade de navios,
                // força uma busca randômica pura no tabuleiro até encontrar um quadrante vago (Garante que o jogo não trave)
                while (!sucessoEspelho) {
                    int randL = random.nextInt(tabJogador2.getTamanho());
                    int randC = random.nextInt(tabJogador2.getTamanho());
                    Orientacao randO = orientacoesPossiveis[random.nextInt(orientacoesPossiveis.length)];

                    sucessoEspelho = tabJogador2.posicionarEmbarcacao(navioEspelho, randL, randC, randO);
                    if (sucessoEspelho) {
                        System.out.printf("[AI POSICIONAMENTO FALLBACK] %s posicionado aleatoriamente em [%d, %d]\n",
                                navioEspelho.getNome(), randL, randC);
                    }
                }

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
        enemyBoard.setDisable(true);
        // Bloqueia o tabuleiro inimigo para o jogador não clicar enquanto a máquina joga

        // Criamos uma tarefa em background para rodar o loop com delay de forma assíncrona
        Task<Void> turnoMaquinaTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Tabuleiro tabJogador = jogo.getOponente().getTabuleiro();
                boolean errou = false;

                do {
                    // 1. Gera o delay aleatório no range de 2 segundos (ex: entre 400ms e 2000ms)
                    long delayMs = 500 + (long) (random.nextDouble() * 2500);
                    Thread.sleep(delayMs);

                    // 2. Processa a lógica de ataque naEngine/Model
                    int[] alvo = estrategiaDeAtaque.calcularPosicaoDeAtaque(tabJogador);
                    int linhaAlvo = alvo[0];
                    int colunaAlvo = alvo[1];

                    Resultado resultadoAI = jogo.atacar(linhaAlvo, colunaAlvo);
                    System.out.printf("[MÁQUINA ATACOU] -> [%d, %d] - %s\n", linhaAlvo, colunaAlvo, resultadoAI);

                    // Strategy: troca de estratégia conforme o resultado do ataque
                    if (resultadoAI == Resultado.ACERTOU) {
                        estrategiaInteligente.registrarAcerto(linhaAlvo, colunaAlvo);
                        estrategiaDeAtaque = estrategiaInteligente; // acertou → persegue o navio
                        System.out.println("[Strategy] Trocando para AtaqueInteligente");
                    } else if (resultadoAI == Resultado.AFUNDOU) {
                        estrategiaInteligente.resetarAlvo();
                        estrategiaDeAtaque = estrategiaAleatoria; // afundou → volta ao aleatório
                        System.out.println("[Strategy] Trocando para AtaqueAleatorio");
                    } else if (resultadoAI == Resultado.ERROU) {
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

        gerarRelatorioCronologico();

        Main.changeScreen("pos-game.fxml");
    }

    private void mostrarMensagem(String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void gerarRelatorioCronologico() {
        System.out.println("\n==================================================");
        System.out.println("      RELATÓRIO CRONOLÓGICO DA PARTIDA (LOG)      ");
        System.out.println("==================================================");

        if (historicoComandos.isEmpty()) {
            System.out.println("Nenhuma ação de combate foi registrada.");
            System.out.println("==================================================");
            return;
        }

        int turno = 1;
        // Iterar diretamente sobre a Stack processa os elementos na ordem cronológica de inserção (FIFO)
        for (AcaoCommand comando : historicoComandos) {
            if (comando instanceof AtacarCommand) {
                AtacarCommand ataque = (AtacarCommand) comando;

                int exibidLinha = ataque.getLinha();
                int exibidColuna = ataque.getColuna();
                Resultado res = ataque.getResultadoObtido();

                // Tradução amigável do Enum Resultado
                String resultadoTexto = (res == Resultado.ACERTOU) ? "FOGO (Acertou)" : "ÁGUA (Errou)";

                System.out.printf("Jogada #%02d | Coordenadas: Alvo [%d, %d] -> Resultado: %s\n",
                        turno++,
                        exibidLinha,
                        exibidColuna,
                        resultadoTexto
                );
            }
        }
        System.out.println("==================================================\n");
    }
}
