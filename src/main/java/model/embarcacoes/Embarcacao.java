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
        this.partesRestantes = tamanho;
    }

    public boolean receberDano(){
        partesRestantes = partesRestantes - 1;
        return true;
    }

    public boolean estaDestruido(){
        return false;
    }

    public int getTamanho(){
        return this.tamanho;
    }


    public String getNome() {
        return nome;
    }
}
