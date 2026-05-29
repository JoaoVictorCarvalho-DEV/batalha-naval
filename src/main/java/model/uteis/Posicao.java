package model.uteis;

import model.embarcacoes.Embarcacao;

public class Posicao {

    private final int linha;
    private final int coluna;

    private Embarcacao embarcacao;
    private boolean foiAtacada;

    public Posicao(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
        this.foiAtacada = false;
    }


    public boolean temEmbarcacao() {
        return embarcacao != null;
    }

    public boolean jaFoiAtacada() {
        return foiAtacada;
    }

    public void ocupar(Embarcacao embarcacao) {
        this.embarcacao = embarcacao;
    }


    public Resultado receberAtaque() {
        if (foiAtacada) {
            return Resultado.JA_ATACADO;
        }

        foiAtacada = true;

        if (temEmbarcacao()) {
            embarcacao.receberDano();
            return Resultado.ACERTOU;
        }

        return Resultado.ERROU;
    }


    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    public Embarcacao getEmbarcacao() {
        return embarcacao ;
    }
}