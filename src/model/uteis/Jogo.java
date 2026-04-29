package model.uteis;

public class Jogo {

    private Jogador jogador1;
    private Jogador jogador2;
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
