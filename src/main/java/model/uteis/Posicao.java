package model.uteis;

import model.embarcacoes.Embarcacao;

public class Posicao {
    private int linha;
    private int coluna;
    private Embarcacao ocupadaPor;
    private boolean foiAtacada;

    public Posicao(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
        this.foiAtacada = false;
    }

    public boolean estaDisponivel() {
        System.out.println("Posicao nao disponivel.");
        return false;
    }

    public void marcarAtaque() {
        System.out.println("Marcar ataque...");
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }
}
