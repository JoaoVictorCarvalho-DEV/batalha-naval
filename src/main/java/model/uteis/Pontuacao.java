package model.uteis;

import java.time.LocalDateTime;

public class Pontuacao {

    private String jogador1;
    private String jogador2;

    private String vencedor;

    private int numeroJogadas;

    private long duracao;

    private LocalDateTime dataPartida;

    public Pontuacao(
            String jogador1,
            String jogador2,
            String vencedor,
            int numeroJogadas,
            long duracao,
            LocalDateTime dataPartida) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        this.vencedor = vencedor;
        this.numeroJogadas = numeroJogadas;
        this.duracao = duracao;
        this.dataPartida = LocalDateTime.now();
    }


    public String getJogador1() {
        return jogador1;
    }

    public String getJogador2() {
        return jogador2;
    }

    public String getVencedor() {
        return vencedor;
    }

    public int getNumeroJogadas() {
        return numeroJogadas;
    }

    public long getDuracao() {
        return duracao;
    }

    public LocalDateTime getDataPartida() {
        return dataPartida;
    }
}