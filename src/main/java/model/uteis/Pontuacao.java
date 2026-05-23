package model.uteis;

import java.time.LocalDateTime;

public class Pontuacao {

    private String jogador1;
    private String jogador2;

    private String vencedor;

    private int numeroJogadas;

    private long duracaoSegundos;

    private LocalDateTime dataPartida;

    public Pontuacao(
            String jogador1,
            String jogador2,
            String vencedor,
            int numeroJogadas,
            long duracaoSegundos
    ) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        this.vencedor = vencedor;
        this.numeroJogadas = numeroJogadas;
        this.duracaoSegundos = duracaoSegundos;
        this.dataPartida = LocalDateTime.now();
    }

}