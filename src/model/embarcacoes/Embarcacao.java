package model.embarcacoes;

import model.uteis.Posicao;

import java.util.List;

public class Embarcacao {
    protected int tamanho;
    protected String nome;
    private List<Posicao> posicoes;
    private int partesRestantes;

    public Embarcacao(int tamanho, String nome) {
        this.tamanho = tamanho;
        this.nome = nome;
    }

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
