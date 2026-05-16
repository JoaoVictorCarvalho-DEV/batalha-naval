package model.uteis;

import model.embarcacoes.Embarcacao;

import java.util.List;
import java.util.ArrayList;

public class Tabuleiro {

    private int tamanho;
    private List<Embarcacao> embarcacoes;
    private Posicao[][] matrizPosicao;

    public Tabuleiro(int tamanho) {
        this.tamanho = tamanho;
        this.embarcacoes = new ArrayList<>(); // Inicializa a lista
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
        // Verifica se as células são capazes de serem ocupadas
        for (int i = 0; i < embarcacao.getTamanho(); i++) {
            int c = coluna + 1;
            if (c >= tamanho) return false; // Saiu do tabuleiro
            if (!matrizPosicao[linha][c].estaDisponivel()) return false; // Célula ocupada
        }

        // Ocupar as células
        for(int i = 0; i < embarcacao.getTamanho(); i++){
            matrizPosicao[linha][coluna + i].ocupar(embarcacao);
        }
        embarcacoes.add(embarcacao);
        return true;
    }

    public Resultado receberAtaque(int linha, int coluna){
        Posicao posicao = matrizPosicao[linha][coluna];
        Resultado resultado = posicao.atacar();

        // Checar se afundou depois do ataque
        if (resultado == Resultado.ACERTOU && posicao.getEmbarcacao().estaDestruido()) {
            return Resultado.AFUNDOU;
        }
        return resultado;
    }

    public void exibirTabuleiro(boolean ocultarNavios){
        for(Posicao[] linha :  matrizPosicao){
            for(Posicao pos : linha){
                System.out.print("[" + pos.getLinha() + " " + pos.getColuna() + "]");
            };
            System.out.println("\n");
        }
    }

    public boolean todasEmbarcacoesDestruidas(){
        if (embarcacoes.isEmpty()) return false;
        return embarcacoes.stream().allMatch(Embarcacao::estaDestruido);
    }

    public Posicao getPosicao(Posicao posicao){
        return matrizPosicao[posicao.getLinha()][posicao.getColuna()];
    }

    public int getTamanho(){
        return this.tamanho;
    }
}
