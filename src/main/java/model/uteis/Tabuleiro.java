package model.uteis;

import model.embarcacoes.Embarcacao;

import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {

    private int tamanho;
    private List<Embarcacao> embarcacoes;
    private Posicao[][] matrizPosicao;

    public Tabuleiro(int tamanho) {
        this.tamanho = tamanho;
        this.embarcacoes = new ArrayList<>();
        this.matrizPosicao = new Posicao[tamanho][tamanho];
        preencherMatriz();
    }

    private void preencherMatriz() {
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                matrizPosicao[i][j] = new Posicao(i, j);
            }
        }
    }

    public boolean posicionarEmbarcacao(Embarcacao embarcacao, int linha, int coluna, Orientacao orientacao) {
        for (int i = 0; i < embarcacao.getTamanho(); i++) {
            if (!testarPosicao(embarcacao, linha, coluna, orientacao)) return false;

            if (orientacao == Orientacao.VERTICAL) {
                matrizPosicao[linha - i][coluna].ocupar(embarcacao);
            } else if (orientacao == Orientacao.HORIZONTAL) {
                matrizPosicao[linha][coluna + i].ocupar(embarcacao);
            } else if (orientacao == Orientacao.VERTICAL_INVERSA) {
                matrizPosicao[linha + i][coluna].ocupar(embarcacao);
            } else if (orientacao == Orientacao.HORIZONTAL_INVERSA) {
                matrizPosicao[linha][coluna - i].ocupar(embarcacao);
            }
        }
        embarcacoes.add(embarcacao);
        return true;
    }

    private boolean testarPosicao(Embarcacao embarcacao, int linha, int coluna, Orientacao orientacao) {
        if (matrizPosicao[linha][coluna].temEmbarcacao() &&
                matrizPosicao[linha][coluna].getEmbarcacao() != embarcacao) return false;

        if (orientacao == Orientacao.VERTICAL && (linha + 1) - embarcacao.getTamanho() < 0) return false;
        if (orientacao == Orientacao.HORIZONTAL && (coluna + 1) + embarcacao.getTamanho() > this.tamanho) return false;
        if (orientacao == Orientacao.VERTICAL_INVERSA && (linha + 1) + embarcacao.getTamanho() > this.tamanho) return false;
        if (orientacao == Orientacao.HORIZONTAL_INVERSA && (coluna + 1) - embarcacao.getTamanho() < 0) return false;

        return true;
    }

    public Resultado receberAtaque(int linha, int coluna) {
        Posicao posicao = matrizPosicao[linha][coluna];
        Resultado resultado = posicao.atacar();

        if (resultado == Resultado.ACERTOU && posicao.getEmbarcacao().estaDestruido()) {
            return Resultado.AFUNDOU;
        }
        return resultado;
    }

    public boolean todasEmbarcacoesDestruidas() {
        if (embarcacoes.isEmpty()) return false;
        return embarcacoes.stream().allMatch(Embarcacao::estaDestruido);
    }

    public void exibirTabuleiro(boolean ocultarNavios) {
        for (Posicao[] linha : matrizPosicao) {
            for (Posicao pos : linha) {
                System.out.print("[" + pos.getLinha() + " " + pos.getColuna() + "]");
            }
            System.out.println();
        }
    }

    public Posicao getPosicao(Posicao posicao) {
        return matrizPosicao[posicao.getLinha()][posicao.getColuna()];
    }

    public int getTamanho() {
        return this.tamanho;
    }
}