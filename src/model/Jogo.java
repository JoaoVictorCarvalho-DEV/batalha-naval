package model;

public class Jogo {

    private Tabuleiro tabuleiroJogador1;
    private Tabuleiro tabuleiroJogador2;
    private int jogadorAtual;

    public void iniciarPartida(){
        System.out.println("Iniciando a partida...");
    }

    public Resultado realizarJogada() {
        return Resultado.AGUA;
    }

    public boolean verificarFimDeJogo(){
        return false;
    }

    public void alternarJogador(){
        System.out.println("Alternando jogador...");
    }

}
