# antivirus-for-a-day
Trabalho úniversitário disciplina (Paradigmas de programação) sobre o paradigma de orientação a objetos

# COMO FUNCIONA O RENDERER: PARA BURRINHOS E A LETÍCIA

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
