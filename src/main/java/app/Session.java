package app;

import model.uteis.Jogo;

//SINGLETON
public class Session {
    private static final Session INSTANCE = new Session();

    private Jogo jogoAtual;

    private Session(){}

    public static Session getInstance(){
        return INSTANCE;
    }

    public Jogo getJogoAtual(){
        return jogoAtual;
    }

    public void setJogoAtual(Jogo jogoAtual){
        this.jogoAtual = jogoAtual;
    }

    public void limpar(){
        this.jogoAtual = null;
    }
    
}
