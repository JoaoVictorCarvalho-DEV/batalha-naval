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

        for(int i = 0; i < embarcacao.getTamanho(); i++){

            if(!testarPosicao(embarcacao.getTamanho(), linha, coluna, orientacao)){
                return false;
            }

            if(orientacao == Orientacao.VERTICAL){
                System.out.println("Embarcacao " + embarcacao.getNome() + " foi posicionada na linha" + (linha - i) + " e coluna " + coluna);
                matrizPosicao[linha - i][coluna].ocupar(embarcacao);
            }
            if(orientacao == Orientacao.HORIZONTAL){
                System.out.println("Embarcacao " + embarcacao.getNome() + " foi posicionada na linha" + linha + " e coluna " + (coluna+i));
                matrizPosicao[linha][coluna + i].ocupar(embarcacao);
            }
            if(orientacao == Orientacao.VERTICAL_INVERSA){
                System.out.println("Embarcacao " + embarcacao.getNome() + " foi posicionada na linha" + (linha + i) + " e coluna " + coluna);
                matrizPosicao[linha + i][coluna].ocupar(embarcacao);
            }

            if(orientacao == Orientacao.HORIZONTAL_INVERSA){
                System.out.println("Embarcacao " + embarcacao.getNome() + " foi posicionada na linha" + linha + " e coluna " + (coluna - i));
                matrizPosicao[linha][coluna - i].ocupar(embarcacao);
            }

        }
        return true;
    }

    private boolean testarPosicao(int tamanhoEmbarcao, int linha, int coluna, Orientacao orientacao){
        if(orientacao == Orientacao.VERTICAL){
            if((linha + 1) - tamanhoEmbarcao < 0){
                System.out.println("Tamanho da embarcacao excede o tamanho do tabuleiro em:  " + ( (linha + 1) - tamanhoEmbarcao));
                return false ;
            }
        }
        if(orientacao == Orientacao.HORIZONTAL){
            if((coluna + 1) - tamanhoEmbarcao > this.tamanho){
                System.out.println("Tamanho da embarcacao excede o tamanho do tabuleiro em:  " + ( (coluna + 1) + tamanhoEmbarcao));
                return false ;
            }
        }
        if(orientacao == Orientacao.VERTICAL_INVERSA){
            if((linha + 1) + tamanhoEmbarcao > this.tamanho){
                System.out.println("Tamanho da embarcacao excede o tamanho do tabuleiro em:  " + ( (linha + 1) + tamanhoEmbarcao));
                return false ;
            }
        }

        if(orientacao == Orientacao.HORIZONTAL_INVERSA){
            if((coluna + 1) - tamanhoEmbarcao < 0){
                System.out.println("Tamanho da embarcacao excede o tamanho do tabuleiro em:  " + ( (coluna + 1) - tamanhoEmbarcao));
                return false ;
            }
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
