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

## Comportamento atual dos turnos e ciclos

**Pendente de revisão:** o fluxo abaixo descreve a implementação atual e precisa
ser revisado para confirmar se está em conformidade com os requerimentos de
`TrabalhoPratico.pdf`. Em especial, devem ser conferidos a separação entre
movimento e ações, o custo da cura, o encerramento antecipado de turnos e a
contagem dos ciclos.

1. **Turno inicial (ciclo 0):** os dois jogadores começam em [3,3]. P1 escolhe
   uma porta e sai usando WASD; depois P2 faz o mesmo usando as setas. Nessa
   etapa, não é permitido usar E para ficar no centro. Encontrar a fonte já
   encerra a partida, mesmo nessa etapa.
2. **Ciclo 1:** após os movimentos iniciais, começam as ações de P1, depois as
   de P2 e, por último, os ataques dos inimigos.
3. **Ciclos seguintes:** antes das ações, há uma fase de movimento de P1 e
   depois de P2. Cada um pode se mover uma vez por uma porta ou usar E para
   ficar. Só é permitido sair quando não houver inimigos no setor atual.
4. **Ações dos jogadores:** P1 joga antes de P2. Cada um pode atacar/procurar
   duas vezes, combinando essas ações. Ataques que erram em setores ocultos e
   procuras que não encontram nada também gastam uma ação. Comandos inválidos,
   alvos inexistentes e procura em setor privado não gastam ações.
5. **Cura do suporte:** P2 pode recuperar 2 DEF de si mesmo ou de P1 vivo no
   mesmo setor. A cura substitui as duas ações e só pode ser escolhida antes
   de gastar qualquer ação naquele turno.
6. **Encerramento do turno:** E permite encerrar as ações antecipadamente. Se
   não houver ações válidas, o jogo avança automaticamente. Por exemplo, se P2
   eliminar o último inimigo de um setor privado com a primeira ação, seu turno
   termina, pois a ação restante não permite curar. P2 com duas ações ainda
   pode curar em um setor privado vazio; P1 nesse setor tem seu turno pulado.
7. **Inimigos:** após os jogadores, cada inimigo vivo ataca uma vez cada jogador
   vivo no mesmo setor. Cada ataque tem um sorteio independente de 1 a 6:
   resultado par reduz a DEF do alvo pelo ATK do inimigo; ímpar não causa dano.
   Depois desses ataques, começa o próximo ciclo. Jogadores com DEF zero são
   eliminados e seus turnos seguintes são pulados.
8. **Fim da partida:** entrar no setor da fonte dá vitória imediata ao jogador.
   Há derrota quando todos os jogadores são eliminados ou ao terminar os
   ataques inimigos do ciclo 25 sem encontrar a fonte; não existe ciclo 26.

O painel acompanha o jogador ativo e mostra ciclo, fase, ações restantes e o
resultado dos ataques inimigos. Ações fora da vez do jogador são bloqueadas.
