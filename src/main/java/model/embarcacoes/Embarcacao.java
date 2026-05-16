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
        partesRestantes--;
        return estaDestruido(); // Agora não retorna sempre True
    }

    public boolean estaDestruido(){
        return partesRestantes == 0; // Agora não retorna sempre false
    }

    public int getTamanho(){
        return this.tamanho;
    }


    public String getNome() {
        return nome;
    }
}
