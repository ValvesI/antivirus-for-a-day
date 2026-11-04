# Guia explicado do projeto

Este documento acompanha o `README.md` e explica como o código está organizado, o que fazem seus métodos principais e onde aparecem os conceitos de orientação a objetos estudados na disciplina. Ele descreve a implementação atual; não altera nem acrescenta regras ao jogo.

## Visão geral

O programa é um jogo cooperativo para dois jogadores. O objetivo é explorar setores, enfrentar os inimigos e encontrar a fonte de infecção. O código separa as regras do jogo, os objetos que representam o tabuleiro e os objetos que desenham a tela no terminal.

| Parte | Classes | Responsabilidade |
|---|---|---|
| Regras do jogo | [`Board.java`](Board.java), [`Sector.java`](Sector.java), [`Player.java`](Player.java), [`Enemy.java`](Enemy.java), [`Entity.java`](Entity.java), [`Direction.java`](Direction.java) | Guardar o estado e executar as regras de turno, movimento, combate e procura. |
| Desenho | [`Element.java`](Element.java), [`Box.java`](Box.java), [`GridBox.java`](GridBox.java), [`TextElement.java`](TextElement.java), [`Renderer.java`](Renderer.java), [`PlayerView.java`](PlayerView.java), [`SectorView.java`](SectorView.java) | Montar e desenhar o tabuleiro e as informações no terminal. |
| Apoio | [`Pair.java`](Pair.java), [`Main.java`](Main.java) | Agrupar dois valores e iniciar o jogo, ler comandos e conectar as outras classes. |

## Conceitos da disciplina encontrados no código

### Classes, atributos, objetos e encapsulamento

Uma classe define dados e operações. Por exemplo, `Player` guarda nome, tipo e defesa; cada jogador criado com `new Player(...)` é um objeto dessa classe.

Vários atributos são `private`, e o acesso ocorre por métodos da classe. Isso é encapsulamento: a classe controla como seus dados são lidos ou alterados. Há também atributos `protected` em `Element` e `Entity`, que ficam acessíveis às subclasses. Os métodos de leitura e escrita mais simples são descritos junto da classe, mas não repetidos um por um.

### Herança

`extends` declara que uma classe herda de outra. A subclasse recebe os membros acessíveis da superclasse e pode acrescentar ou redefinir comportamentos.

```text
Entity (abstrata)
├── Player
└── Enemy

Element
├── Board
├── Box
│   ├── GridBox
│   └── PlayerView
├── TextElement
└── SectorView
```

### Classe abstrata

`Entity` é declarada `abstract`, então não pode ser instanciada diretamente. Ela reúne dados comuns a `Player` e `Enemy`, como posição, vida e dano. A disciplina apresenta que uma classe abstrata pode ter métodos concretos e pode até não declarar nenhum método abstrato; é o caso desta classe.

`Element` não é abstrata. Seus métodos `draw` e `update` têm implementações vazias para fornecer um comportamento padrão que as subclasses podem redefinir.

### Interfaces: o que existe e o que não existe

O projeto **não declara uma interface própria**: não há uma declaração `interface` nem uma classe do projeto usando `implements`.

O código usa a interface padrão `java.util.List` como tipo de alguns atributos, por exemplo `List<Player>` e `List<Enemy>`, e cria objetos `ArrayList`. `List` é uma interface da biblioteca Java; `ArrayList` é uma classe que a implementa. Isso demonstra o uso de uma interface existente, mas não significa que o grupo tenha definido uma interface para o domínio do jogo. Se a avaliação pedir uma interface criada pelo grupo, este ponto deve ser tratado com cuidado no relatório e na entrevista.

### Polimorfismo: os tipos presentes

Os slides distinguem inclusão, sobrecarga e polimorfismo paramétrico. Os três aparecem de formas diferentes:

1. **Inclusão (subtipo), com chamada polimórfica em tempo de execução.** Uma referência de uma superclasse ou interface pode apontar para um objeto de um subtipo. `Renderer` guarda elementos como `Element` e chama `element.draw(this)`. O objeto real pode ser um `Board`, `Box`, `GridBox`, `TextElement` ou `SectorView`; a versão de `draw` executada depende da classe real do objeto. O mesmo princípio vale para `update` quando uma subclasse redefine esse método.
2. **Sobrecarga (polimorfismo ad hoc), resolvida pelo compilador.** Construtores ou métodos têm o mesmo nome, mas listas de parâmetros diferentes. Exemplos: os construtores de `Box`, `GridBox`, `Player` e `Board`; e `Board.getSector(int, int)` versus `Board.getSector(Player)`.
3. **Polimorfismo paramétrico, por generics.** `Pair<A, B>` funciona com tipos escolhidos pelo código que instancia o par. Coleções como `List<Player>` e `List<Enemy>` também declaram o tipo de objeto guardado. Isso evita tratar todos os valores como um tipo genérico e fazer conversões manuais.

**Sobrescrita não é sobrecarga.** `@Override` aparece quando uma subclasse fornece sua própria implementação de um método herdado, como `GridBox.draw`. Já a sobrecarga usa o mesmo nome com parâmetros diferentes.

### Coleções

As fontes apresentam `List` e `ArrayList`, além de generics e laços para percorrer coleções. O projeto usa listas para armazenar os jogadores, inimigos, portas e elementos de tela. Os `for-each` percorrem essas listas. `getPlayers()` e `getEnemies()` devolvem uma nova `ArrayList` com os mesmos objetos, de modo que quem recebe a lista não possa adicionar ou remover elementos diretamente das listas internas.

## Explicação das classes e métodos

### `Main` — início e interação

`main(String[] args)` cria o `Renderer`, o `Board`, a vista do setor e os textos da interface. Depois, mantém o ciclo principal do programa: atualiza as informações, desenha a tela, lê um comando com `Scanner` e chama o método correspondente do tabuleiro.

O comando é convertido para minúsculas e o programa usa seu primeiro caractere. Cada comando deve ser digitado em uma linha e confirmado com Enter. `W/A/S/D` movimenta o jogador da vez; `E` fica no setor durante a fase de movimento ou encerra o turno de ações; `B` procura; `F` pede o número do inimigo; `C` pede o alvo da cura; `Q` encerra.

As variáveis `escolhendoInimigo` e `escolhendoAlvoCura` indicam que o próximo comando será interpretado como um índice, em vez de uma ação normal. O jogador da vez vem de `Board.getActivePlayer()`, portanto os dois jogadores usam os mesmos comandos em turnos diferentes.

### `Board` — tabuleiro e regras de turno

`Board` herda de `Element` para que o próprio tabuleiro possa ser adicionado ao `Renderer` e desenhado junto aos outros elementos.

O enum `Board.Phase` representa as seis fases possíveis: movimento de P1, movimento de P2, ações de P1, ações de P2, turno dos inimigos e partida encerrada. O atributo `phase` guarda uma dessas opções, em vez de texto livre.

**Construtores:** `Board(linha, coluna)` cria um gerador aleatório padrão e encaminha a inicialização para `Board(linha, coluna, random)`. O segundo construtor recebe um `Random`, cria os 25 setores, liga suas portas, escolhe a fonte de infecção e cria P1 e P2 no centro. Esses construtores também são um exemplo de sobrecarga e de encadeamento com `this(...)`.

**Controle de turnos:**

- `getActivePlayer()` devolve P1 ou P2 de acordo com a fase; durante a fase dos inimigos ou depois do fim, devolve `null`.
- `isMoving()` informa se o jogo está em uma das fases de movimento.
- `checkTurn(player, movement)` verifica se a partida acabou, se é a vez do jogador, se ele está vivo e se a ação corresponde à fase atual. Devolve `null` quando a ação pode prosseguir ou uma mensagem quando deve ser recusada.
- `hasAvailableAction()` verifica se o jogador atual ainda tem alguma ação válida no setor. Um setor privado impede procura; nele, P2 só pode usar a cura se ainda não gastou ação alguma.
- `advancePhase()` avança pela ordem P1 movimento, P2 movimento, P1 ações, P2 ações e inimigos. Também pula turnos de jogadores eliminados e turnos sem ações possíveis.
- `consumeAction()` reduz as ações restantes depois de uma procura ou ataque. Avança a fase quando acabaram as duas ações ou quando nenhuma ação válida restou.
- `endTurn(player)` encerra antecipadamente a fase de ações do jogador, se o pedido for válido.
- `stay(player)` encerra a fase de movimento sem trocar de setor. No ciclo inicial, recusa essa opção para obrigar a saída do centro.
- `recoverDefense(player, target)` valida turno, papel de suporte, participantes vivos, setor compartilhado e uso no começo do turno. Se tudo estiver correto, delega a cura a `Player.recoverDefense` e encerra a fase de ações.
- `resolveEnemyTurn()` percorre os setores e faz cada inimigo vivo atacar cada jogador vivo que esteja no mesmo setor. Cada ataque sorteia de 1 a 6; resultado par causa dano. Depois verifica derrota por eliminação ou pelo limite do ciclo 25; caso contrário começa o próximo ciclo.

**Criação e consulta do tabuleiro:**

- `DOORS` descreve as portas de cada setor usando letras (`N`, `L`, `S`, `O` para norte, leste, sul e oeste), em vez de codificar portas como bits.
- `getDoorLetter(direction)` converte uma direção para a letra usada nessa tabela.
- `getSector(x, y)` devolve o setor na posição indicada e rejeita coordenadas fora do tabuleiro. `getSector(player)` usa a posição do jogador para chamar essa versão. São métodos sobrecarregados.
- `getPlayers()` devolve uma cópia da lista de jogadores. Os objetos `Player` continuam sendo os mesmos; a cópia evita que o chamador altere a lista interna do tabuleiro.
- `isFinished()` informa se a fase atual é `FINISHED`. `getWinner()` e `getInfectionSource()` expõem, respectivamente, o vencedor e o setor que contém a fonte.

**Ações do jogo:**

- `move(player, direction)` valida fase e jogador, impede a saída de um setor com inimigos e exige uma porta aberta. Atualiza posição, visita o destino e verifica se o jogador encontrou a fonte. Se encontrou, registra a vitória; caso contrário, avança a fase.
- `search(player)` valida o turno e impede procura em setor privado. Delega o sorteio e seus efeitos ao `Sector`, depois consome uma ação.
- `attack(player, index)` valida o turno e a posição do inimigo na lista local, delega o combate ao `Sector` e consome uma ação.
- `draw(renderer)` desenha a grade, os números dos eixos, as portas visíveis dos setores visitados, os marcadores de centro e fonte, e os jogadores presentes em cada setor. É uma sobrescrita de `Element.draw`.

### `Sector` — estado e ações locais

`Sector` representa uma casa do tabuleiro. Seu `enum Type` limita o tipo a `NORMAL`, `HIDDEN` ou `PRIVATE`. A lista `doors` registra as direções abertas e a lista `enemies` guarda os inimigos do setor.

- `openDoor(direction)` adiciona uma porta à lista do setor; `hasDoor(direction)` verifica se ela está aberta.
- `hasEnemies()` informa se há inimigos na lista.
- `getEnemies()` devolve uma cópia da lista. Isso protege a lista interna contra inclusão ou remoção externa, embora os objetos inimigos dessa cópia sejam os mesmos.
- `visit(random)` gera inimigos apenas na primeira visita. O centro não gera inimigos; nos demais setores, sorteia de um a três inimigos, com ataque e defesa iguais entre um e três.
- `search(player, random)` exige que o jogador esteja no setor, esteja vivo e que o setor não seja privado. O dado de 1 a 3 não encontra nada; 4 aumenta a defesa em 1; 5 aumenta em 2; 6 reduz a defesa de cada inimigo local em 1 e remove inimigos que chegam a zero.
- `attack(player, enemyIndex, random)` exige jogador no setor e índice válido. Em setor oculto, o ataque pode errar. Quando acerta, reduz a defesa do inimigo pelo ataque do jogador e remove o inimigo se chegar a zero.

### `Player`, `Enemy` e `Entity` — personagens e herança

#### `Entity`

`Entity` é a superclasse abstrata de `Player` e `Enemy`. Seu construtor inicializa posição, vida e dano chamando os métodos de validação. `setLinha` e `setColuna` só alteram os valores quando estão dentro dos limites definidos por `Renderer`; valores inválidos são ignorados. `setVida` ignora valores negativos. `setDano` rejeita valores fora de 1 a 3 com `IllegalArgumentException`.

O atributo `vida` existe, mas as regras atuais de combate usam `defesa`; não há uso de `vida` nas ações implementadas. A classe não declara métodos abstratos, embora seja abstrata e não possa ser instanciada.

#### `Player`

`Player` estende `Entity` e acrescenta nome, tipo e defesa. O tipo `1` identifica o suporte, conforme `isSupport()`.

- O construtor completo recebe posição, vida, ataque/dano, nome e tipo; chama `super(...)` para inicializar a parte herdada.
- `Player(nome, ataque, defesa)` é uma versão mais curta e sobrecarregada. Encaminha a chamada ao construtor completo com `this(...)` e depois define a defesa.
- `recoverDefense(target)` verifica se quem cura é suporte vivo e se o alvo existe, está vivo e ocupa a mesma posição. Então aumenta a defesa do alvo em 2. Não aplica limite máximo, pois o enunciado não define um.
- `getAtaque()` e `setAtaque(...)` usam o atributo `dano` herdado como valor de ataque. O setter encaminha para `setDano`, portanto também aplica a validação de 1 a 3.
- `setDefesa(...)` atribui o valor diretamente, sem impor intervalo.

Os demais getters e setters apenas leem ou atribuem nome e tipo.

#### `Enemy`

`Enemy` também estende `Entity`. Seu construtor inicializa os dados herdados e define o tipo e a defesa. `setDefesa(...)` só aceita valores de 0 a 3; esse limite permite representar inimigos eliminados com defesa zero. Os getters e `setTipo(...)` apenas leem ou atribuem seus campos.

### `Direction` — enumeração de direções

`Direction` é um `enum`: um conjunto fechado de quatro valores, cada um associado a deslocamentos `dx` e `dy`. A movimentação soma esses deslocamentos à posição do jogador. `opposite()` devolve a direção oposta com base na ordem em que os quatro valores foram declarados (norte, leste, sul, oeste). Os outros métodos apenas retornam os deslocamentos.

### `Element` — base dos objetos desenháveis

`Element` guarda linha e coluna, rejeita posições iniciais negativas e oferece operações padrão às subclasses.

- `update()` tem corpo vazio para que uma subclasse possa redefinir atualização quando precisar.
- `draw(renderer)` também tem corpo vazio e pode ser redefinido para desenhar a forma concreta.
- `getSize()` devolve o tamanho padrão de uma célula: altura 1 e largura 1.

`Board`, `Box`, `TextElement` e `SectorView` herdam de `Element`. A posição é `protected`, para que subclasses como `GridBox` usem a posição herdada.

### `Box`, `GridBox` e `Pair` — composição e dados auxiliares

#### `Box`

`Box` herda de `Element` e representa uma borda retangular com elementos dentro.

- Seus quatro construtores são sobrecarregados: tamanho padrão 1x1; caixa quadrada; altura e largura; ou altura, largura e opção de ajuste automático.
- `addElement(element)` rejeita `null`, guarda o elemento na lista e, se o ajuste automático estiver ligado, aumenta largura/altura para pelo menos o tamanho informado pelo elemento. Esse cálculo considera o tamanho do elemento, não sua posição relativa dentro da caixa.
- `removeElement(element)` remove o elemento da lista.
- `getSize()` devolve altura e largura em um `Pair`, nessa ordem.
- `draw(renderer)` chama `drawBorder` e depois `drawElements`.
- `drawBorder(renderer)` desenha as quatro bordas e os cantos usando caracteres.
- `drawElements(renderer)` chama `draw` para cada elemento guardado. Como a lista tem elementos de subclasses de `Element`, esta chamada usa sobrescrita polimórfica.
- `update()` chama `update` para cada elemento guardado.

`addElement` é `final`, portanto uma subclasse não pode sobrescrever esse método. Os setters de largura e altura rejeitam valores não positivos; os demais getters e o seletor de ajuste apenas leem ou alteram esses campos.

#### `GridBox`

`GridBox` estende `Box` e divide seu retângulo em células.

- Os dois construtores permitem criar grade com largura e altura iguais ou especificar dimensões separadas. Ambos validam o número de células e o espaço disponível.
- `getCellCenter(x, y)` recebe coordenadas de célula que começam em 1 e devolve a linha e coluna do centro correspondente no terminal, como `Pair<Integer, Integer>`.
- `setAltura` e `setLargura` sobrescrevem os métodos de `Box` para impedir dimensões menores ou iguais ao número de células.
- `draw(renderer)` sobrescreve o desenho de `Box`: desenha borda, divisórias, cruzamentos e depois quaisquer elementos internos.
- `calcularPosicao(...)` converte uma divisão da grade em uma coordenada da tela, arredondando para distribuir as divisórias pelo espaço disponível.
- `validarGridSize(...)` rejeita quantidade de células não positiva ou uma caixa pequena demais. É `static` porque usa apenas os parâmetros recebidos, sem consultar um objeto.

#### `Pair<A, B>`

`Pair` guarda dois valores, que podem ter tipos diferentes, sem exigir uma classe específica para cada combinação. Os parâmetros genéricos `A` e `B` são um exemplo de polimorfismo paramétrico. O construtor guarda os dois objetos; os getters apenas devolvem cada valor. Os campos são `final`, então a referência armazenada não pode ser substituída depois da criação.

### `Renderer`, `TextElement` e vistas — desenho da tela

#### `Renderer`

`Renderer` não herda de `Element`; ele coordena a tela. Sua matriz `char[][]` guarda o quadro antes de imprimi-lo, e sua lista contém referências do tipo `Element`.

- O construtor sem parâmetros cria uma grade 10x10; o construtor com dimensões rejeita largura/altura não positivas e preenche a tela com espaços.
- `getGrade()` devolve uma cópia da matriz, para o chamador não alterar a matriz interna diretamente.
- `addElement(element)` rejeita `null` e adiciona um elemento à lista; `removeElement(element)` remove-o.
- `clear()` preenche a grade com espaços.
- `setChar(linha, coluna, caractere)` grava um caractere se a coordenada couber na tela e devolve `true`; fora dos limites, não grava e devolve `false`.
- `update()` chama `update()` de cada elemento.
- `draw()` limpa a tela e chama `draw(this)` de cada elemento. Aqui se vê inclusão: a lista está tipada como `Element`, mas guarda objetos de várias subclasses; a versão sobrescrita de `draw` é escolhida conforme o objeto real.
- `render()` transforma as linhas da matriz em uma única `String`, separando as linhas por quebra de linha.
- `present()` limpa a tela do terminal usando sequências ANSI, imprime o resultado de `render()` e força a saída com `flush()`.

#### `TextElement`

`TextElement` estende `Element` e desenha uma `String` começando em sua posição.

- O construtor e `setText(text)` rejeitam `null`.
- `draw(renderer)` percorre cada caractere do texto e pede ao renderer que o escreva na linha e coluna apropriadas. Caracteres que ultrapassarem os limites são ignorados pelo retorno `false` de `setChar`.
- `getSize()` devolve altura 1 e largura igual ao número de caracteres do texto.
- `getText()` apenas devolve o texto atual.

#### `PlayerView`

`PlayerView` estende `Box`, mantém uma referência ao jogador e coloca três `TextElement` dentro da caixa: nome, ataque e defesa. `update()` atualiza esses textos com os valores atuais do jogador e chama `super.update()` para atualizar os elementos internos. Esta classe existe no projeto, mas **não é criada por `Main` na interface atual**.

#### `SectorView`

`SectorView` estende `Element` e mantém referências ao tabuleiro e ao jogador cujos dados serão exibidos. `setPlayer(...)` troca o jogador observado. `draw(renderer)` consulta o setor desse jogador e desenha coordenadas, tipo do setor, aviso da fonte, borda, portas, inimigos e jogadores presentes.

## Construtores e chamadas a `this` e `super`

- `this(...)` chama outro construtor da mesma classe. O projeto usa isso para compartilhar inicialização nos construtores de `Board`, `Box`, `GridBox` e `Player`.
- `super(...)` chama o construtor da superclasse. `Board`, `Box`, `TextElement` e `SectorView` chamam `Element`; `GridBox` e `PlayerView` chamam `Box`; `Player` e `Enemy` chamam `Entity`.
- Os slides de construtores ensinam que a chamada a `this(...)` precisa ser a primeira instrução do construtor. Nos construtores que a usam, o código segue essa regra.

## Pontos do enunciado para a equipe conferir

1. O enunciado pede um comentário com nome e GRR dos integrantes em `Main.java`. O arquivo atual não contém esse comentário; os dados do grupo precisam ser preenchidos pela equipe.
2. O enunciado lista interface entre os conceitos avaliados. O código usa a interface de biblioteca `List`, mas não define uma interface própria. Confiram com a professora se esse uso atende ao requisito do trabalho.
3. `Entity.vida` está declarado, mas as regras atuais usam defesa como condição de eliminação. Se forem questionados, descrevam o comportamento existente sem afirmar que `vida` controla o combate.
4. `PlayerView` está implementada, mas a tela iniciada por `Main` usa `SectorView` e não instancia `PlayerView`.

## Fontes da disciplina consultadas

As explicações acima seguem os tópicos destas fontes disponíveis na pasta `fontes`:

- `Slides01_IntroducaoJava.pdf` — classes, método `main`, entrada com `Scanner` e saída.
- `Slides06_Construtores.pdf` — construtores, `this(...)` e `super(...)`.
- `Slides07_AtributoTipoClasse.pdf` — atributos que guardam referências a outros objetos.
- `Slides08_Heranca.pdf` — superclasse, subclasse e herança.
- `Slides09_Sobrecarga.pdf` — sobrecarga de construtores e métodos.
- `Slides10_ClasseAbstrata.pdf` — classes abstratas e seus métodos.
- `Slides11_Interface.pdf` — declaração de interfaces e relação `implements`.
- `Slides12_Polimorfismo.pdf` — inclusão, sobrecarga e polimorfismo paramétrico.
- `Slides13_Colecoes.pdf` — `List`, `ArrayList`, generics e laços sobre coleções.
- `TrabalhoPratico.pdf` — regras do jogo e conceitos cobrados na avaliação.
