package model.uteis;

import model.embarcacoes.Embarcacao;

import java.util.List;

public class Tabuleiro {

    private int tamanho;
    private List<Embarcacao> embarcacoes;
    private Posicao[][] matrizPosicao;

    public Tabuleiro(int tamanho) {
        this.tamanho = tamanho;
        this.matrizPosicao = new Posicao[tamanho][tamanho];
    }

    public boolean posicionarEmbarcacao(Embarcacao embarcacao, int linha, int coluna, Orientacao orientacao){
        System.out.println("Posicionar embarcacao...");
        return true;
    }

    public Resultado receberAtaque(int linha, int coluna){
        System.out.println("Recebendo ataque na linha " + linha + " e coluna " + coluna);
        return Resultado.ACERTOU;
    }

    public void exibirTabuleiro(boolean ocultarNavios){
        System.out.println(ocultarNavios ? "Exibir tabuleiro com navios" : "Exibir tabuleiro sem navios");
    }

    public boolean todasEmbarcacoesDestruidas(){
        System.out.println("Todas as embarcacoe nao destruidas");
        return false;
    }

    public Posicao getPosicao(Posicao posicao){
        return matrizPosicao[posicao.getLinha()][posicao.getColuna()];
    }
}
