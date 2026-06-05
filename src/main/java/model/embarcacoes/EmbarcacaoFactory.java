package model.embarcacoes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EmbarcacaoFactory {

    // Hashmap identificador para construção de embarcações a partir de uma string
    private static final Map<String, Supplier<Embarcacao>> REGISTRY = new HashMap<>();

    static {
        REGISTRY.put("cruzador", Cruzador::new);
        REGISTRY.put("encouracado", Encouracado::new);
        REGISTRY.put("portaavioes", PortaAvioes::new);
        REGISTRY.put("submarino", Submarino::new);
    }

    // Factory Method que cria uma nova embarcação baseada no nome identificador
    public static Embarcacao criar(String tipo) {
        Supplier<Embarcacao> supplier = REGISTRY.get(tipo.toLowerCase().replaceAll("\\s+", ""));
        if (supplier == null) {
            throw new IllegalArgumentException("Tipo de embarcação desconhecido: " + tipo);
        }
        return supplier.get();
    }
}