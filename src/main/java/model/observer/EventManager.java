package model.observer;

import java.util.List;
import java.util.ArrayList;
import model.uteis.Evento;
public class EventManager {
    private List<GameObserver> observers = new ArrayList<>();

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Evento evento) {
        for (GameObserver observer : observers) {
            observer.update(evento);
        }
    }

}
