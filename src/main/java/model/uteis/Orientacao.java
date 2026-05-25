package model.uteis;

public enum Orientacao {
    VERTICAL,
    HORIZONTAL,
    VERTICAL_INVERSA,
    HORIZONTAL_INVERSA;

    // Método para descobrir proxima orientação
    public Orientacao rotacionar() {
        Orientacao[] valores = Orientacao.values();
        int proximoIndice = (this.ordinal() + 1) % valores.length;
        return valores[proximoIndice];
    }

    /*
    #
    *           #
    *           #
    *           #       VERTICAL
    *
    *           ####      HORIZONTAL
    *
    *
    *           #       VERTICAL_INVERSA
    *           #
    *           #
    *           #
    *
    *        ####        HORIZONTAL_INVERSA
    *
    * */


}
