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

### 1. Padrao Comportamental: Command

* **Arquivo Principal:** `src/main/java/controller/game/command/AtacarCommand.java`
* **Como e Utilizado:** Toda intencao de disparo no tabuleiro inimigo e encapsulada dentro de um objeto `AtacarCommand`. Em vez de o controlador da tela executar o ataque diretamente, ele cria uma instancia deste comando (passando as coordenadas e referencias do botao clicado) e dispara o metodo `.executar()`.
* **Geracao do Relatorio Final:** Cada comando executado com sucesso e armazenado em uma pilha historica (`Stack<AcaoCommand>`) mantida no controlador central. No final da partida, o metodo `finalizarJogo()` invoca uma varredura sequencial nesta pilha. Como cada objeto `AtacarCommand` retem internamente as coordenadas do tiro e o enumerador do resultado obtido, o sistema reconstroi a cronologia exata turn-by-turn do combate, gerando o relatorio estatistico no terminal sem a necessidade de criar variaveis de log paralelas na camada de modelo.

### 2. Padrao Criacional: Factory Method

* **Arquivo Principal:** `src/main/java/model/embarcacoes/EmbarcacaoFactory.java`
* **Como e Utilizado:** A fabrica elimina a necessidade do uso do operador `new` e de checagens dinamicas de tipo (`instanceof`) no controlador de interface, centralizando a criacao das subclasses de `Embarcacao` (como `Cruzador`, `Submarino`, etc.) atraves de um registro estatico de construtores.
* **Instanciacao dos Navios dos Dois Jogadores:** * **Jogador 1:** Durante a fase de posicionamento, o laco de leitura percorre um catalogo de identificadores textuais (Strings) e invoca `EmbarcacaoFactory.criar(tipo)` para materializar as pecas que o usuario posicionara na tela.
    * **Jogador 2 (Maquina):** Assim que o posicionamento do Jogador 1 e validado, o sistema aciona novamente a `EmbarcacaoFactory` passando o nome do navio recem-criado. Isso garante o nascimento de uma nova instancia inedita, isolada em memoria, para ser submetida ao algoritmo assimetrico de busca de coordenadas do oponente, mantendo a frota de ambos os jogadores homogenea na assinatura, mas heterogenea na disposicao geografica do tabuleiro.
