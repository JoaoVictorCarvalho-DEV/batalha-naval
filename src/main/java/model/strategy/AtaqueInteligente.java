package model.strategy;

import model.uteis.Tabuleiro;

// Estratégia alternativa: varre o tabuleiro em ordem (linha por linha)
// Só existe para demonstrar que o padrão permite trocar o comportamento facilmente
public class AtaqueInteligente implements EstrategiaDeAtaque{
    @Override
    public int[] calcularPosicaoDeAtaque(Tabuleiro tabuleiro) {
        int tamanho = tabuleiro.getTamanho();

        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++){
                if (!tabuleiro.getPosicao(new model.uteis.Posicao(i, j)).jaFoiAtacada()) {
                    System.out.println("[AtaqueInteligente] Posicao escolhida: [" + i + ", "+ j + "]");
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{0,0};
    }
}
