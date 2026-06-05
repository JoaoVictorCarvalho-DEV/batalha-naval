package controller.game.command;

public interface AcaoCommand {

    // Executa a ação no jogo e atualiza os elementos necessários
    void executar();

    // Reverte completamente os efeitos produzidos pela execução deste comando
    void desfazer();
}