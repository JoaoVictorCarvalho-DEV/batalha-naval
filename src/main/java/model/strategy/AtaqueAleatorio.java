package model.strategy;

import model.uteis.Tabuleiro;
import java.util.Random;

// Escolhe uma célula aleatória ainda não atacada
public class AtaqueAleatorio implements EstrategiaDeAtaque {
    private final Random random = new Random();

    @Override
    public int[] calcularPosicaoDeAtaque(Tabuleiro tabuleiro) {
        int tamanho = tabuleiro.getTamanho();
        int linha, coluna;

        // Sorteia até encontrar uma célula não atacada
        do {
            linha = random.nextInt(tamanho);
            coluna = random.nextInt(tamanho);
        } while (tabuleiro.getPosicao(new model.uteis.Posicao(linha, coluna)).jaFoiAtacada());

        System.out.println("[Ataque Aleatório] Posicao escolhida: [" + linha + ", " + coluna +"]");
        return new int[]{linha, coluna};
    }
}
