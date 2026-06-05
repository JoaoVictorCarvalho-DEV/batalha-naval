package controller.game.command;

import javafx.scene.control.Button;
import model.uteis.Jogo;
import model.uteis.Resultado;
import model.uteis.Tabuleiro;
import model.uteis.Posicao;

public class AtacarCommand implements AcaoCommand {
    private final Jogo jogo;
    private final int linha;
    private final int coluna;
    private final Button botaoAlvo;

    private Resultado resultadoObtido;
    private String estiloAnterior;

    public AtacarCommand(Jogo jogo, int linha, int coluna, Button botaoAlvo) {
        this.jogo = jogo;
        this.linha = linha;
        this.coluna = coluna;
        this.botaoAlvo = botaoAlvo;
    }

    @Override
    public void executar() {
        // 1. Guarda o estado visual anterior do botão JavaFX
        this.estiloAnterior = botaoAlvo.getStyle();

        // 2. Executa a lógica de negócio no Model
        this.resultadoObtido = jogo.atacar(linha, coluna);

        // 3. Atualiza o visual da célula baseado no resultado
        if (resultadoObtido == Resultado.ACERTOU) {
            botaoAlvo.setStyle("-fx-background-color: red;");
        } else {
            botaoAlvo.setStyle("-fx-background-color: gray;");
        }
    }

    @Override
    public void desfazer() {
        if (resultadoObtido != null) {
            Tabuleiro tabuleiroInimigo = jogo.getOponente().getTabuleiro();
            Posicao posicaoResetada = new Posicao(linha, coluna);
            tabuleiroInimigo.resetarPosicao(linha, coluna);
            botaoAlvo.setStyle(estiloAnterior);
            botaoAlvo.setText("");
            jogo.getJogadorAtual().decrementarTiros();
        }
    }
    public int getLinha() {
        return this.linha;
    }

    public int getColuna() {
        return this.coluna;
    }


    public Resultado getResultadoObtido() {
        return this.resultadoObtido;
    }
}