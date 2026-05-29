package model.uteis;

public class Jogo {

    private Jogador jogador1;
    private Jogador jogador2;
    private Jogador jogadorAtual;
    private int tamanho = 10;

    public Jogo(String nome1, String nome2) {
        this.jogador1 = new Jogador(nome1, new Tabuleiro(tamanho));
        this.jogador2 = new Jogador(nome2, new Tabuleiro(tamanho));
        this.jogadorAtual = jogador1;
    }

    public Jogador getJogadorAtual() {
        return jogadorAtual;
    }

    public Jogador getOponente() {
        return jogadorAtual == jogador1 ? jogador2 : jogador1;
    }

    public Resultado atacar(int linha, int coluna) {
        Tabuleiro tabuleiroInimigo = getOponente().getTabuleiro();
        Resultado resultado = getJogadorAtual().atirar(tabuleiroInimigo, linha, coluna);

        System.out.println("Resultado: " + resultado);
        
        if (resultado == Resultado.ERROU) {
            alternarJogador();
        }

        return resultado;
    }

    public boolean acabou() {
        return jogador1.getTabuleiro().todasEmbarcacoesDestruidas()
                || jogador2.getTabuleiro().todasEmbarcacoesDestruidas();
    }

    public void alternarJogador() {
        jogadorAtual = (jogadorAtual == jogador1) ? jogador2 : jogador1;
        System.out.println("Trocando para o jogador: "+ jogadorAtual.getNome());
    }
}