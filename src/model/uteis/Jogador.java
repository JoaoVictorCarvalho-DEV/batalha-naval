package model.uteis;

import java.util.HashMap;
import java.util.Map;

public class Jogador {
    private String nome;
    private Tabuleiro tabuleiro;
    private int tiros;
    private int tirosAcertados;
    private int tirosConsecutivos;


    public Jogador(String nome, Tabuleiro tabuleiro) {
        this.nome = nome;
        this.tabuleiro = tabuleiro;
        this.tiros = 0;
        this.tirosAcertados = 0;
        this.tirosConsecutivos = 0;
    }

    public void atirar(Tabuleiro tabuleiro, Posicao posicao){
        /*switch (tabuleiro.getPosicao(posicao).getTipo()){

        }*/
    }


    public Map getStatus(){
        Map<String, Object> status = new HashMap<>();
        String nome = getNome();
        int tiros = getTiros();
        int tirosConsecutivos = getTirosConsecutivos();

        System.out.println("Status de : " + nome);
        status.put("nome", nome);
        System.out.println("Tiros disparados: " + tiros);
        status.put("tiros", String.valueOf(tiros));
        System.out.println("Tiros consecutivos: " + tirosConsecutivos);

        return status;
    }


    public String getNome() {
        return nome;
    }

    public int getTiros() {
        return tiros;
    }

    public int getTirosAcertados() {
        return tirosAcertados;
    }

    public int getTirosConsecutivos() {
        return tirosConsecutivos;
    }
}
