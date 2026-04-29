package model;

public class Posicao {
    private int linha;
    private int coluna;
    private Embarcacao ocupadaPor;
    private boolean foiAtacada;

    public boolean estaDisponivel() {
        System.out.println("Posicao nao disponivel.");
        return false;
    }

    public void marcarAtaque() {
        System.out.println("Marcar ataque...");
    }
}
