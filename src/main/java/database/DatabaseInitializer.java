package database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {

        criarTabelaPontuacoes();

    }

    private static void criarTabelaPontuacoes() {

        String sql = """
                    CREATE TABLE IF NOT EXISTS pontuacoes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        jogador1 TEXT NOT NULL,
                        jogador2 TEXT NOT NULL,
                        vencedor TEXT NOT NULL,
                        numero_jogadas INTEGER,
                        duracao INTEGER,
                        data_partida TEXT
                    )
                """;

        try {

            Connection conn = Database.getInstance().getConnection();

            Statement stmt = conn.createStatement();

            stmt.execute(sql);

            System.out.println("Tabela pontuacoes verificada.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}