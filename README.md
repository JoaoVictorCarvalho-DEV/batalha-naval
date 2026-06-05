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
