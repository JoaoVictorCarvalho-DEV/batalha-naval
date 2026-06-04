package components;

import model.observer.GameObserver;
import model.uteis.Evento;

import javafx.scene.control.Label;

public class StatusLabel extends Label implements GameObserver {

    @Override
    public void update(Evento evento) {
        setText(evento.getMensagem());
        
        switch (evento.getTipoEvento()) {
            case INFO:
                setStyle("-fx-text-fill: grey;");
                break;
            case ACERTO:
                setStyle("-fx-text-fill: green;");
                break;

            case ERRO:
                setStyle("-fx-text-fill: red;");
                break;

            case VITORIA:
                setStyle("-fx-text-fill: gold;");
                break;

            case DERROTA:
                setStyle("-fx-text-fill: orange;");
                break;
        }
    }

}
