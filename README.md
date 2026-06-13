## Padrões de Projeto

### Strategy — Estratégia de ataque da máquina

**Problema que resolve**

A lógica de escolha dos ataques da IA estava concentrada no `GameController`, gerando acoplamento e dificultando a evolução do comportamento da máquina.

**Como foi aplicado**

A responsabilidade de decidir onde atacar foi extraída para a interface `EstrategiaDeAtaque`. O `GameController` apenas solicita uma posição de ataque e executa o disparo.

Atualmente existe uma única estratégia adaptativa:

```text
EstrategiaDeAtaque (interface)
└── AtaqueInteligente
```

O `AtaqueInteligente` inicia realizando ataques aleatórios e, ao acertar um navio, passa a atacar as posições vizinhas até encontrar toda a embarcação. Quando o navio é afundado, a estratégia retorna ao modo de busca normal.

**Onde está no código**

| Arquivo                                  | Papel                                                       |
| ---------------------------------------- | ----------------------------------------------------------- |
| `model/strategy/EstrategiaDeAtaque.java` | Interface com o contrato `calcularPosicaoDeAtaque()`        |
| `model/strategy/AtaqueInteligente.java`  | Implementação da IA adaptativa                              |
| `controller/game/GameController.java`    | Utiliza a estratégia para obter a próxima posição de ataque |

**Benefício**

O controlador não precisa conhecer a lógica da IA. Novas estratégias podem ser adicionadas futuramente sem alterar o fluxo principal do jogo.

```java
private final EstrategiaDeAtaque estrategiaDeAtaque = new AtaqueInteligente();
```

---

### State — Fases do jogo

**Problema que resolve**

O controle das fases do jogo era realizado por verificações simples, o que tende a gerar código difícil de manter conforme novas funcionalidades são adicionadas.

**Como foi aplicado**

Cada fase do jogo passou a ser representada por uma classe de estado. O objeto `Jogo` mantém o estado atual e controla as transições entre as etapas da partida.

```text
EstadoPreJogo → EstadoPosicionamento → EstadoPartida → EstadoFinalizado
```

As mudanças de estado são registradas no console:

```text
[State] Transicao: PRE_JOGO → POSICIONAMENTO
[State] Transicao: POSICIONAMENTO → PARTIDA
[State] Transicao: PARTIDA → FINALIZADO
```

**Onde está no código**

| Arquivo                                 | Papel                                        |
| --------------------------------------- | -------------------------------------------- |
| `model/state/EstadoJogo.java`           | Interface base dos estados                   |
| `model/state/EstadoPreJogo.java`        | Estado inicial                               |
| `model/state/EstadoPosicionamento.java` | Fase de posicionamento dos navios            |
| `model/state/EstadoPartida.java`        | Fase de combate                              |
| `model/state/EstadoFinalizado.java`     | Estado final do jogo                         |
| `model/uteis/Jogo.java`                 | Armazena o estado atual e realiza transições |
| `controller/game/GameController.java`   | Solicita as mudanças de estado               |

**Benefício**

As regras de cada fase ficam organizadas e separadas. Caso seja necessário adicionar novos estados no futuro, basta criar uma nova implementação de `EstadoJogo` e integrá-la ao fluxo da partida.

---

### Observer — Notificação de eventos do jogo

**Problema que resolve**

Durante o jogo, vários eventos ocorrem (ataques acertados, mensagens de erro, mudanças de turno) que precisam ser comunicados aos componentes visuais. Sem o padrão Observer, o `GameController` teria que conhecer e atualizar diretamente cada componente da interface, gerando acoplamento forte.

**Como foi aplicado**

O padrão Observer foi implementado através da interface `GameObserver` que define o contrato `update(Evento evento)`. O `EventManager` mantém uma lista de observadores e, quando um evento ocorre, notifica todos os registrados.

```text
GameObserver (interface)
└── StatusLabel (implementação)

EventManager (gerenciador central)
└── Notifica todos os observadores registrados
```

O fluxo é simples: o `GameController` cria eventos (como `TipoEvento.INFO` ou `TipoEvento.ACERTO`) e os distribui através do `EventManager`, que chama o método `update()` em cada observador registrado.

**Onde está no código**

| Arquivo                                 | Papel                                                  |
| --------------------------------------- | ------------------------------------------------------ |
| `model/observer/GameObserver.java`      | Interface que define o contrato `update(Evento)`       |
| `model/observer/EventManager.java`      | Gerenciador que adiciona, remove e notifica observers   |
| `components/StatusLabel.java`           | Implementação de GameObserver para atualizar a UI       |
| `controller/game/GameController.java`   | Cria eventos e solicita notificação aos observadores    |
| `model/uteis/Evento.java`               | Objeto que encapsula informações do evento              |
| `model/uteis/TipoEvento.java`           | Enumeração dos tipos de eventos (INFO, ACERTO, etc.)    |

**Benefício**

O `GameController` não precisa conhecer a implementação específica de cada componente visual. Novos observadores podem ser adicionados dinamicamente sem modificar o controlador. A comunicação entre lógica de jogo e interface fica desacoplada.

---

### Singleton — Instância única da sessão

**Problema que resolve**

A aplicação precisa de um ponto de acesso global e único para gerenciar a sessão atual do jogo. Sem um Singleton, seria necessário passar a instância do jogo através de múltiplos construtores, gerando complexidade e possibilidade de inconsistências.

**Como foi aplicado**

A classe `Session` implementa o padrão Singleton com:
- Construtor privado para evitar instâncias externas
- Atributo estático `INSTANCE` que armazena a única instância
- Método estático `getInstance()` para acesso global

```java
public class Session {
    private static final Session INSTANCE = new Session();
    
    private Session() {}  // Construtor privado
    
    public static Session getInstance() {
        return INSTANCE;
    }
}
```

**Onde está no código**

| Arquivo               | Papel                                                  |
| --------------------- | ------------------------------------------------------ |
| `app/Session.java`    | Singleton que gerencia a instância única de `Jogo`     |

**Benefício**

Qualquer controlador ou componente pode acessar o jogo atual através de `Session.getInstance().getJogoAtual()` sem necessidade de injeção de dependência. A instância é garantida como única e global, simplificando o fluxo de dados da aplicação.

---

### Command — Encapsulamento de ataques e auditoria da partida

**Problema que resolve**

O processamento direto de disparos e o gerenciamento das jogadas sobrecarregavam o controlador com múltiplas responsabilidades, dificultando o rastreamento histórico das ações e gerando acoplamento entre os gatilhos visuais e as regras de negócio.

**Como foi aplicado**

Toda intenção de disparo no tabuleiro inimigo é encapsulada dentro de um objeto `AtacarCommand`. Em vez de o controlador da tela executar o ataque diretamente, ele cria uma instância deste comando (passando as coordenadas e referências do botão clicado) e dispara o método `.executar()`.

Cada comando executado com sucesso é armazenado em uma pilha histórica mantida no controlador central. No final da partida, o método `finalizarJogo()` invoca uma varredura sequencial nesta pilha. Como cada objeto `AtacarCommand` retém internamente as coordenadas do tiro e o enumerador do resultado obtido, o sistema reconstrói a cronologia exata turn-by-turn do combate através do método `gerarRelatorioCronologico()`.

**Onde está no código**

| Arquivo | Papel |
| ------------------------------------------- | ------------------------------------------------------------- |
| `controller/game/command/AcaoCommand.java`  | Interface base que define o contrato de execução de ações     |
| `controller/game/command/AtacarCommand.java` | Implementação concreta que encapsula os dados do disparo       |
| `controller/game/GameController.java`       | Instancia, executa e gerencia a pilha histórica de comandos   |

**Benefício**

Desacoplamento total entre o clique do mouse no JavaFX e o processamento de regras de combate. O histórico permite rastreabilidade via Event Sourcing parcial para gerar logs estatísticos detalhados sem a necessidade de poluir a camada de modelo com variáveis ou estruturas de texto paralelas.

AtacarCommand comandoAtaque = new AtacarCommand(jogo, row, col, cell);
comandoAtaque.executar();
historicoComandos.push(comandoAtaque);

---

### Factory Method — Inicialização e descentralização de embarcações

**Problema que resolve**

A instanciação das frotas acoplava o controlador rigidamente com as subclasses concretas de `Embarcacao`. O espelhamento dos navios para a máquina exigia validações poluidoras em tempo de execução com o operador `instanceof` e o uso repetitivo do operador `new`.

**Como foi aplicado**

A criação das subclasses de navios foi centralizada e delegada para a classe `EmbarcacaoFactory`, que utiliza um catálogo estático indexado (`Map` de referências de construtores/`Supplier`).

A fábrica atende o ciclo de vida de criação dos dois jogadores de forma dinâmica:
- **Jogador 1:** Durante a fase de posicionamento, o laço de leitura percorre um catálogo de identificadores textuais (Strings) e invoca a fábrica para materializar as peças sequencialmente.
- **Jogador 2 (Máquina):** Assim que o posicionamento humano é confirmado, o controlador aciona a fábrica passando o nome do navio recém-criado, gerando uma peça inédita e isolada em memória para alimentar o algoritmo de busca assimétrica da IA.

```text
EmbarcacaoFactory.criar("Cruzador") ──> Retorna nova instância isolada de Cruzador

Arquivo Principal: src/main/java/model/embarcacoes/EmbarcacaoFactory.java
