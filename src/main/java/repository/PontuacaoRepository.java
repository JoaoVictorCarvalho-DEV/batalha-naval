package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.uteis.Pontuacao;

public class PontuacaoRepository {
    private Connection connection;

    public PontuacaoRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Pontuacao> listarPontuacoes() {

        List<Pontuacao> pontuacoes = new ArrayList<>();

        String sql = """
                    SELECT jogador1,jogador2,vencedor,numeroJogadas, duracaoSegundos
                    FROM pontuacoes
                    ORDER BY pontos DESC
                """;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Pontuacao pontuacao = new Pontuacao(
                        rs.getString("jogador1"),
                        rs.getString("jogador2"),
                        rs.getString("vencedor"),
                        rs.getInt("numeroJogadas"),
                        rs.getInt("duracaoSegundos")
                    );

                pontuacoes.add(pontuacao);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pontuacoes;
    }
}
