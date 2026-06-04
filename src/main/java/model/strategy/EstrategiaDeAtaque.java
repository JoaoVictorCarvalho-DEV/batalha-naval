package model.strategy;

import model.uteis.Tabuleiro;
public interface EstrategiaDeAtaque{
    // Retorna [linha, coluna] da célula escolhida para atacar
    int[] calcularPosicaoDeAtaque(Tabuleiro tabuleiro);
}
