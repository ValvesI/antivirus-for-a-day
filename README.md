# antivirus-for-a-day
Trabalho úniversitário disciplina (Paradigmas de programação) sobre o paradigma de orientação a objetos

# COMO FUNCIONA O RENDERER: PARA BURROS E A LETÍCIA

O `Renderer` monta a interface do jogo em uma matriz de caracteres. Cada posição da
matriz representa um caractere que será mostrado no terminal.

O fluxo básico é:

```java
renderer.update();
renderer.draw();
renderer.present();
```

- `update()` atualiza os dados dos elementos.
- `draw()` limpa a matriz e pede para cada elemento se desenhar nela.
- `present()` transforma a matriz em texto e imprime o resultado no terminal.

## Criando o renderer

```java
Renderer renderer = new Renderer(80, 30);
```

O primeiro valor é a largura e o segundo é a altura.

Para colocar um elemento na tela:

```java
renderer.addElement(elemento);
```

## Element

`Element` é a classe base dos elementos visuais. Todo elemento possui uma posição
formada por `linha` e `coluna`.

Os métodos principais são:

- `update()`: atualiza o estado do elemento.
- `draw(Renderer renderer)`: escreve o elemento na matriz do renderer.
- `getSize()`: retorna um `Pair` contendo altura e largura, nessa ordem.

## TextElement

`TextElement` desenha uma `String` horizontalmente:

```java
TextElement texto = new TextElement(2, 4, "Antivírus");
renderer.addElement(texto);
```

Também pode representar números, símbolos e mensagens de status:

```java
texto.setText("DEF: 6");
```

## Box

`Box` desenha uma caixa e pode guardar outros elementos:

```java
Box painel = new Box(1, 1, 5, 20);
painel.addElement(new TextElement(2, 2, "Jogador P1"));
renderer.addElement(painel);
```

Por padrão, uma `Box` é ajustável. Quando um elemento interno for maior que ela,
sua altura ou largura será aumentada.

Para criar uma caixa de tamanho fixo:

```java
Box painelFixo = new Box(1, 1, 5, 20, false);
```

## GridBox

`GridBox` herda de `Box` e adiciona divisórias. Ela é usada para representar o
tabuleiro:

```java
GridBox tabuleiro = new GridBox(1, 1, 11, 21, 5);
renderer.addElement(tabuleiro);
```

Nesse exemplo, a caixa tem altura 11, largura 21 e forma uma grade 5x5.

O método `getCellCenter(x, y)` converte uma posição relativa da grade para a linha
e coluna correspondentes no terminal. As posições da grade começam em 1.

## PlayerView

`PlayerView` mostra as informações de um jogador dentro de uma caixa:

```java
PlayerView playerView = new PlayerView(1, 25, player);
renderer.addElement(playerView);
```

Quando `update()` é executado, os textos de nome, ataque e defesa são atualizados
com os valores atuais do jogador.

## Exemplo completo

```java
Renderer renderer = new Renderer(80, 30);
GridBox tabuleiro = new GridBox(1, 1, 11, 21, 5);

renderer.addElement(tabuleiro);

renderer.update();
renderer.draw();
renderer.present();
```

Quando alguma informação mudar, basta executar novamente `update()`, `draw()` e
`present()` para redesenhar a interface.

## Controles do jogo

Execute em um terminal Linux/macOS (pode ser o terminal integrado da IDE), com
`javac *.java` e `java Main`. O console de execucao da IDE pode nao oferecer as
funcoes de terminal necessarias. As teclas funcionam sem Enter.

- P1: W, A, S, D.
- P2: setas do teclado.
- O painel acompanha automaticamente o jogador da vez. Apenas ele pode agir.
- F seguido de 1, 2 ou 3: ataca o inimigo indicado com o jogador da vez.
- No inicio do turno de acoes de P2: C seguido de 1 (P1 no mesmo setor) ou 2 (o proprio P2)
  recupera 2 DEF. Nao ha teto de DEF definido no enunciado.
- B: procura com o jogador da vez. Dado 1-3: nada; 4: +1 DEF;
  5: +2 DEF; 6: -1 DEF de todos os inimigos locais, eliminando os que chegarem a zero.
  Setores privados impedem a procura.
- Q: encerra e restaura a configuracao do terminal.

No mapa, `P` representa os dois jogadores juntos; separados, aparecem `P1` e `P2`.

A fonte de infeccao (`X`) e sorteada uma vez por partida entre os 24 setores
fora do centro. O primeiro jogador a entrar nela vence imediatamente.

## Turnos e ciclos

No turno 0, P1 sai do centro com WASD e depois P2 sai com as setas.
A partir dai, cada ciclo tem os turnos de acoes de P1 e P2, seguidos pelos
inimigos. Antes das acoes dos ciclos seguintes, cada jogador pode se mover
uma vez (apenas sem inimigos no setor) ou pressionar E para ficar.

Cada jogador tem duas acoes de ataque/procura. Um ataque que erra em setor
oculto tambem consome uma acao; comandos invalidos nao consomem acoes.
A cura do suporte substitui as duas acoes e so pode ser escolhida no inicio
do turno. E durante as acoes encerra o turno antecipadamente, inclusive em
setores privados sem inimigos, onde nao ha ataque nem procura disponiveis.

Cada inimigo vivo ataca uma vez cada jogador vivo no mesmo setor, com um
sorteio independente de 1 a 6: par causa dano igual ao ATK; impar erra.
Jogadores com DEF zero sao eliminados e seus turnos sao pulados. O jogo
termina imediatamente ao encontrar a fonte, quando ambos morrem ou ao
final do ciclo 25 sem encontrar a fonte. O painel mostra ciclo, fase,
acoes restantes e o resultado dos ataques dos inimigos.
