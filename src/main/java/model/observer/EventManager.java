package model.observer;

import java.util.List;
import java.util.ArrayList;

public class EventManager {
    private List<GameObserver> observers = new ArrayList<>();

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String mensagem) {
        for (GameObserver observer : observers) {
            observer.update(mensagem);
        }
    }

}
