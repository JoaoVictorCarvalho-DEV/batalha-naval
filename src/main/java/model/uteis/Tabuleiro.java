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
        preencherMatriz();
    }

    private void preencherMatriz() {
        for(int i = 0; i < tamanho; i++){
            for(int j = 0; j < tamanho; j++){
                matrizPosicao[i][j] = new Posicao(i, j);
            }
        }
    }

    public boolean posicionarEmbarcacao(Embarcacao embarcacao, int linha, int coluna, Orientacao orientacao){
        System.out.println("Posicionar embarcacao " + embarcacao.getNome());
        matrizPosicao[linha][coluna].ocupar(embarcacao);

        for(int i = 0; i < embarcacao.getTamanho(); i++){
            matrizPosicao[linha][coluna].ocupar(embarcacao);


        }
        return true;
    }

    public Resultado receberAtaque(int linha, int coluna){
        Posicao posicao = matrizPosicao[linha][coluna];

        return posicao.atacar();
    }

    public void exibirTabuleiro(boolean ocultarNavios){
        System.out.println(ocultarNavios ? "Exibir tabuleiro com navios" : "Exibir tabuleiro sem navios");
        for(Posicao[] posicao :  matrizPosicao){
            for(Posicao pos : posicao){
                System.out.print("[" + pos.getLinha() + " " + pos.getColuna() + "]");
            };
            System.out.println("\n");
        }
    }

    public boolean todasEmbarcacoesDestruidas(){
        System.out.println("Todas as embarcacoe nao destruidas");
        return false;
    }

    public Posicao getPosicao(Posicao posicao){
        return matrizPosicao[posicao.getLinha()][posicao.getColuna()];
    }

    public int getTamanho(){
        return this.tamanho;
    }
}
