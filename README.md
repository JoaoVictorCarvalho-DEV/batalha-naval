# Projeto Batalha Naval - Padroes de Projeto GoF

Este projeto e uma implementacao do classico jogo Batalha Naval desenvolvida em Java e JavaFX. O principal objetivo e desafio arquitetural deste sistema foi integrar **6 Padroes de Projeto GoF (Gang of Four)** em uma unica base de codigo cooperativa, garantindo alta coesao, baixo acoplamento e respeito estrito aos principios SOLID.

---

## Como o Projeto Funciona

O sistema adota uma arquitetura em camadas estruturada para separar a Interface Grafica (UI) das Regras de Negocio (Dominio) e da Persistencia:

1. **Inicializacao e Estado:** O jogo e controlado por uma maquina de estados que dita se o sistema esta na fase de configuracao, combate ou finalizacao.
2. **Fase de Posicionamento:** O Jogador 1 posiciona sua frota clicando no tabuleiro visual. O sistema utiliza fabricas para instanciar os navios correspondentes e processa uma busca adaptativa para posicionar a frota do Jogador 2 (Maquina) de forma assimetrica e oculta.
3. **Fase de Combate:** Os jogadores alternam turnos realizando disparos. Os cliques do jogador humano sao convertidos em objetos autonomos de acao, enquanto os disparos da maquina sao gerenciados por threads de background nao-bloqueantes com delays assincronos para simular o tempo de resposta humano.
4. **Finalizacao e Persistencia:** Ao detectar que todas as embarcacoes de um tabuleiro foram destruidas, o jogo calcula as estatisticas de tempo e pontuacao, persiste os dados no banco de dados atraves de repositorios e varre a memoria para expor a auditoria completa da partida.

---

## Implementacao dos Padroes de Projeto Destacados

### 1. Padrao Comportamental: Command

* **Arquivo Principal:** `src/main/java/controller/game/command/AtacarCommand.java`
* **Como e Utilizado:** Toda intencao de disparo no tabuleiro inimigo e encapsulada dentro de um objeto `AtacarCommand`. Em vez de o controlador da tela executar o ataque diretamente, ele cria uma instancia deste comando (passando as coordenadas e referencias do botao clicado) e dispara o metodo `.executar()`.
* **Geracao do Relatorio Final:** Cada comando executado com sucesso e armazenado em uma pilha historica (`Stack<AcaoCommand>`) mantida no controlador central. No final da partida, o metodo `finalizarJogo()` invoca uma varredura sequencial nesta pilha. Como cada objeto `AtacarCommand` retem internamente as coordenadas do tiro e o enumerador do resultado obtido, o sistema reconstroi a cronologia exata turn-by-turn do combate, gerando o relatorio estatistico no terminal sem a necessidade de criar variaveis de log paralelas na camada de modelo.

### 2. Padrao Criacional: Factory Method

* **Arquivo Principal:** `src/main/java/model/embarcacoes/EmbarcacaoFactory.java`
* **Como e Utilizado:** A fabrica elimina a necessidade do uso do operador `new` e de checagens dinamicas de tipo (`instanceof`) no controlador de interface, centralizando a criacao das subclasses de `Embarcacao` (como `Cruzador`, `Submarino`, etc.) atraves de um registro estatico de construtores.
* **Instanciacao dos Navios dos Dois Jogadores:** * **Jogador 1:** Durante a fase de posicionamento, o laco de leitura percorre um catalogo de identificadores textuais (Strings) e invoca `EmbarcacaoFactory.criar(tipo)` para materializar as pecas que o usuario posicionara na tela.
    * **Jogador 2 (Maquina):** Assim que o posicionamento do Jogador 1 e validado, o sistema aciona novamente a `EmbarcacaoFactory` passando o nome do navio recem-criado. Isso garante o nascimento de uma nova instancia inedita, isolada em memoria, para ser submetida ao algoritmo assimetrico de busca de coordenadas do oponente, mantendo a frota de ambos os jogadores homogenea na assinatura, mas heterogenea na disposicao geografica do tabuleiro.