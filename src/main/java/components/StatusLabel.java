package components;
import model.observer.GameObserver;

import javafx.scene.control.Label;

public class StatusLabel extends Label implements GameObserver {

    @Override
    public void update(String mensagem){
        setText(mensagem);
    }

}
