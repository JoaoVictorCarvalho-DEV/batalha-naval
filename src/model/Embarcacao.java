package model;

import java.util.List;

public class Embarcacao {
    protected int tamanho;
    protected String nome;
    private List<Posicao> posicoes;
    private int partesRestantes;

    public boolean receberDano(){
        return false;
    }

    public boolean estaDestruido(){
        return false;
    }

    public int getTamanho(){
        return this.tamanho;
    }


}
