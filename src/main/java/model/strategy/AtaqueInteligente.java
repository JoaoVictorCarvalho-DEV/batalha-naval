package model.strategy;

import model.uteis.Posicao;
import model.uteis.Tabuleiro;
import java.util.Random;

// Estratégia inteligente: ataca aleatório até acertar, depois persegue o navio
public class AtaqueInteligente implements EstrategiaDeAtaque {

    private final Random random = new Random();

    // Última célula acertada — ponto de partida para perseguir o navio
    private int linhaAlvo = -1;
    private int colunaAlvo = -1;

    // Direções para tentar após um acerto: cima, baixo, esquerda, direita
    private final int[][] direcoes = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private int indiceDirecao = 0;

    @Override
    public int[] calcularPosicaoDeAtaque(Tabuleiro tabuleiro) {

        // Se tem um alvo, tenta continuar na mesma direção
        if (linhaAlvo != -1) {
            for (; indiceDirecao < direcoes.length; indiceDirecao++) {
                int novaLinha  = linhaAlvo + direcoes[indiceDirecao][0];
                int novaColuna = colunaAlvo + direcoes[indiceDirecao][1];

                if (dentroDoBound(novaLinha, novaColuna, tabuleiro) &&
                        !tabuleiro.getPosicao(new Posicao(novaLinha, novaColuna)).jaFoiAtacada()) {

                    System.out.println("[AtaqueInteligente] Perseguindo navio em [" + novaLinha + ", " + novaColuna + "]");
                    return new int[]{novaLinha, novaColuna};
                }
            }
            // Todas as direções esgotadas, reseta e vai pro aleatório
            resetarAlvo();
        }

        // Modo aleatório: sorteia célula não atacada
        int linha, coluna;
        do {
            linha  = random.nextInt(tabuleiro.getTamanho());
            coluna = random.nextInt(tabuleiro.getTamanho());
        } while (tabuleiro.getPosicao(new Posicao(linha, coluna)).jaFoiAtacada());

        System.out.println("[AtaqueInteligente] Ataque aleatorio em [" + linha + ", " + coluna + "]");
        return new int[]{linha, coluna};
    }

    // Chamado quando um ataque acerta — inicia o modo perseguição
    public void registrarAcerto(int linha, int coluna) {
        System.out.println("[AtaqueInteligente] Acerto registrado em [" + linha + ", " + coluna + "]");
        this.linhaAlvo    = linha;
        this.colunaAlvo   = coluna;
        this.indiceDirecao = 0;
    }

    // Chamado quando o navio afunda — volta ao modo aleatório
    public void resetarAlvo() {
        System.out.println("[AtaqueInteligente] Navio destruido, resetando alvo.");
        this.linhaAlvo    = -1;
        this.colunaAlvo   = -1;
        this.indiceDirecao = 0;
    }

    private boolean dentroDoBound(int linha, int coluna, Tabuleiro tabuleiro) {
        return linha >= 0 && linha < tabuleiro.getTamanho() &&
                coluna >= 0 && coluna < tabuleiro.getTamanho();
    }
}