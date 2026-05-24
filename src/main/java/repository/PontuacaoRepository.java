package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.uteis.Pontuacao;

public class PontuacaoRepository {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Connection connection;

    public PontuacaoRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Pontuacao> listarPontuacoes() {

        List<Pontuacao> pontuacoes = new ArrayList<>();

        String sql = """
                    SELECT jogador1,jogador2,vencedor,numero_jogadas, duracao, data_partida
                    FROM pontuacoes
                    ORDER BY numero_jogadas DESC
                """;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                LocalDateTime data = LocalDateTime.parse(
                        rs.getString("data_partida"),
                        DATE_FORMATTER);

                Pontuacao pontuacao = new Pontuacao(
                        rs.getString("jogador1"),
                        rs.getString("jogador2"),
                        rs.getString("vencedor"),
                        rs.getInt("numero_jogadas"),
                        rs.getInt("duracao"),
                        data);

                pontuacoes.add(pontuacao);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pontuacoes;
    }

    public boolean salvarPontuacao(Pontuacao pontuacao) {
        String sql = """
                    INSERT INTO pontuacoes (jogador1, jogador2, vencedor, numero_jogadas, duracao, data_partida)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Formatando a data para salvar no banco
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dataFormatada = pontuacao.getDataPartida().format(formatter);

            // Preenchendo os parâmetros
            stmt.setString(1, pontuacao.getJogador1());
            stmt.setString(2, pontuacao.getJogador2());
            stmt.setString(3, pontuacao.getVencedor());
            stmt.setInt(4, pontuacao.getNumeroJogadas());
            stmt.setLong(5, pontuacao.getDuracao());
            stmt.setString(6, dataFormatada);

            // Executa a inserção
            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0; // Retorna true se salvou com sucesso

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
