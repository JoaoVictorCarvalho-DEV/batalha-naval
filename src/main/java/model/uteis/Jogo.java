package model.uteis;

public class Jogo {

    private Jogador jogador1;
    private Jogador jogador2;
    private Jogador jogadorAtual;

    public Jogo(String nome1, String nome2) {
        this.jogador1 = new Jogador(nome1, new Tabuleiro(10));
        this.jogador2 = new Jogador(nome2, new Tabuleiro(10));
        this.jogadorAtual = jogador1;
    }

    public Jogador getJogadorAtual() {
        return jogadorAtual;
    }

    public Jogador getOponente() {
        return jogadorAtual == jogador1 ? jogador2 : jogador1;
    }

    public Resultado atacar(int linha, int coluna) {
        Resultado resultado = getOponente().getTabuleiro().receberAtaque(linha, coluna);

        if (resultado != Resultado.JA_ATACADO) {
            alternarJogador();
        }

        return resultado;
    }

    public boolean acabou() {
        return jogador1.getTabuleiro().todasEmbarcacoesDestruidas()
                || jogador2.getTabuleiro().todasEmbarcacoesDestruidas();
    }

    public void alternarJogador() {
        System.out.println("Trocar de jogador " + jogadorAtual.getNome() + " para " + getOponente().getNome());
        jogadorAtual = (jogadorAtual == jogador1) ? jogador2 : jogador1;
    }
}