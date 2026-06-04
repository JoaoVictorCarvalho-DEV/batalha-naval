package model.uteis;

public class Evento {
    private TipoEvento tipoEvento;

    private String mensagem;

    public Evento(TipoEvento tipoEvento, String mensagem){
        this.tipoEvento = tipoEvento;
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

}
