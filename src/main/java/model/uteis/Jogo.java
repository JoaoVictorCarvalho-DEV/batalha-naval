package model.uteis;

public class Jogo {

    private Jogador jogador1;
    private Jogador jogador2;
    private Jogador jogadorAtual;

    public Jogo() {
        this.jogador1 = new Jogador("Jogador 1", new Tabuleiro(10));
        this.jogador2 = new Jogador("Jogador 2", new Tabuleiro(10));
    }

    public void iniciarPartida(){
        System.out.println("Iniciando a partida...");
        this.jogadorAtual = this.jogador1;

        //SETAR POSICOES DO JOGADOR 1
        System.out.println("Defina a posição das suas embarcacoes");
        jogadorAtual.tabuleiro.exibirTabuleiro(false);

        this.alternarJogador();
        //SETAR POSICOES DO JOGADOR 2


        //AGUARDAR JOGADAS E VERIFICAR ESTADO DO JOGO ATÉ ACABAR
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
