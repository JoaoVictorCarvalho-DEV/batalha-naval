package model;

import java.util.List;

public class Tabuleiro {
    private int linhas = 10;
    private int colunas = 10;
    private List<Embarcacao> embarcacoes;
    private Posicao[][] matrizPosicao;

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
}
