# antivirus-for-a-day
Trabalho úniversitário disciplina (Paradigmas de programação) sobre o paradigma de orientação a objetos

Para uma explicação das classes, métodos e conceitos de orientação a objetos usados no código, consulte o [guia explicado do projeto](README_EXPLICADO.md).

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

Compile com `javac *.java` e execute com `java Main`. Digite um comando e pressione
Enter para cada jogada. O jogo usa `Scanner`, apresentado nas fontes da disciplina.

- Movimento do jogador da vez: W (cima), A (esquerda), S (baixo), D (direita).
- O painel acompanha automaticamente o jogador da vez. Apenas ele pode agir.
- F seguido de 1, 2 ou 3: ataca o inimigo indicado com o jogador da vez.
- No inicio do turno de acoes de P2: C seguido de 1 (P1 no mesmo setor) ou 2 (o proprio P2)
  recupera 2 DEF. Nao ha teto de DEF definido no enunciado.
- B: procura com o jogador da vez. Dado 1-3: nada; 4: +1 DEF;
  5: +2 DEF; 6: -1 DEF de todos os inimigos locais, eliminando os que chegarem a zero.
  Setores privados impedem a procura.
- Q: encerra o jogo.

No mapa, `P` representa os dois jogadores juntos; separados, aparecem `P1` e `P2`.

A fonte de infeccao (`X`) e sorteada uma vez por partida entre os 24 setores
fora do centro. O primeiro jogador a entrar nela vence imediatamente.

## Comportamento atual dos turnos e ciclos

O fluxo abaixo descreve o comportamento conferido no código. A cura substitui
as duas ações do suporte, os turnos avançam quando não há ações válidas e a
derrota por limite ocorre ao fim do ciclo 25. O enunciado não detalha se deve
haver uma fase de movimento separada em cada ciclo nem comandos para ficar no
setor ou encerrar ações antecipadamente; essa sequência é uma escolha da
implementação e deve ser confirmada com a professora se a interpretação estrita
do enunciado for necessária.

1. **Turno inicial (ciclo 0):** os dois jogadores começam em [3,3]. P1 escolhe
   uma porta e sai usando W/A/S/D; depois P2 faz o mesmo usando W/A/S/D. Nessa
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

## Comparação com o enunciado e pendências

Esta comparação foi feita lendo o código e confrontando-o com
[`TrabalhoPratico.pdf`](TrabalhoPratico.pdf). Ela não substitui uma partida de
verificação completa.

### Regras do jogo

| Exigência | Situação no código |
|---|---|
| Dois jogadores: P1 simples com ATK 2/DEF 6 e P2 suporte com ATK 1/DEF 7 | **Implementado na criação do tabuleiro.** P2 recebe o tipo de suporte. Os valores de ataque e o papel ainda podem ser alterados por setters públicos se outro código os chamar. |
| Tabuleiro 5x5, jogadores no centro, fonte aleatória fora do centro | **Implementado.** A fonte é sorteada entre os 24 setores que não são o centro. |
| Portas definidas no programa, centro com quatro portas e bordas externas sem portas | **Implementado.** A tabela fixa `DOORS` define as ligações; o centro tem quatro saídas e as bordas não abrem para fora do tabuleiro. |
| Setores normais, ocultos e privados | **Implementado.** Setor oculto pode fazer o ataque errar; setor privado impede a procura. |
| Primeira visita gera de um a três inimigos com ATK=DEF entre 1 e 3; setor central não gera inimigos | **Implementado** em `Sector.visit`. Setores revisitados não geram inimigos de novo. |
| Jogadores só saem de um setor sem inimigos | **Implementado** em `Board.move`. |
| P1 age antes de P2; cada um tem até duas ações de ataque/procura | **Implementado.** Procura e ataque consomem uma ação; ações inválidas não são consumidas. |
| Resultados da procura: 1–3 nada, 4 +1 DEF, 5 +2 DEF, 6 reduz DEF dos inimigos locais | **Implementado** em `Sector.search`. Inimigos que chegam a zero são removidos. |
| Suporte pode recuperar 2 DEF de si ou do outro jogador no mesmo setor, substituindo as ações | **Implementado.** O alvo precisa estar vivo e a cura só pode ser escolhida antes de qualquer ação do turno de P2. |
| Cada inimigo ataca cada jogador vivo no mesmo setor; dado par causa dano | **Implementado** em `Board.resolveEnemyTurn`, com um sorteio separado para cada ataque. |
| Vitória ao entrar na fonte; derrota se todos forem eliminados ou ao atingir o limite de ciclos | **Implementado.** A partida encerra ao fim dos ataques do ciclo 25 se a fonte não foi encontrada. |

### Conceitos de orientação a objetos e avaliação

| Conceito/entrega | Situação |
|---|---|
| Implementação em Java e compilação | **Atendido no código-fonte.** As classes do projeto estão em Java e a compilação passou na verificação anterior; a partida completa ainda precisa ser conferida manualmente. |
| Classes, atributos, métodos, construtores e encapsulamento | **Presentes.** Há atributos privados e protegidos, construtores sobrecarregados e validações. |
| Herança e classe abstrata | **Presentes.** `Player` e `Enemy` herdam de `Entity`; elementos visuais herdam de `Element` ou `Box`. `Entity` é abstrata. |
| Polimorfismo | **Presente.** O guia [README_EXPLICADO.md](README_EXPLICADO.md) distingue inclusão/sobrescrita em tempo de execução, sobrecarga e polimorfismo paramétrico por generics. |
| Coleções | **Presentes.** `List` e `ArrayList` armazenam jogadores, inimigos, portas e elementos desenháveis. |
| Interface | **Parcial / confirmar com a professora.** O código usa `java.util.List`, uma interface da biblioteca, mas não declara uma interface própria. O detalhe já está no [guia explicado](README_EXPLICADO.md). |
| Nome e GRR dos integrantes como comentário em `Main.java` | **Pendente.** O comentário ainda não está no arquivo; os nomes e GRRs não constam no repositório. |
| Relatório do grupo em PDF | **Pendente neste diretório.** `TrabalhoPratico.pdf` é o enunciado, não o relatório do grupo. |

### O que falta antes da entrega

1. **Completar o comentário de identificação em `Main.java`** com nome e GRR de cada integrante. Não inventar ou deixar esses dados para depois da entrega.
2. **Resolver a exigência de interface.** Conversar com a professora ou com o grupo para decidir se o uso de `List` atende; se for necessária uma interface do próprio projeto, ela ainda não foi criada. Não apresentar `List` como interface criada pelo grupo.
3. **Produzir o relatório PDF** com nome completo e GRR de todos, explicação dos conceitos de orientação a objetos — incluindo os tipos de polimorfismo — e um diagrama ou outro recurso visual. Não usar capturas de tela do código. Respeitar o formato pedido: até 2 páginas em coluna dupla com espaçamento simples, ou até 3 páginas em uma coluna com espaçamento 1,5 ou duplo; nomear o arquivo com o GRR de um integrante.
4. **Descrever no relatório o uso de IA generativa**, indicando qual ferramenta foi usada e como ajudou na implementação e no relatório. A equipe deve registrar o uso real, inclusive a assistência de ChatGPT/Codex nesta revisão e na documentação, além de outras ferramentas que tenha usado.
5. **Confirmar a interpretação da movimentação com a professora.** Depois do movimento obrigatório do ciclo 0, o código inclui uma fase de movimento para cada jogador nos ciclos seguintes, antes das ações; também permite usar `E` para ficar ou encerrar ações. O PDF exige que os jogadores possam se mover quando não houver inimigos, mas não especifica essa sequência nem esses comandos.
6. **Fazer uma partida completa de verificação**, incluindo setores ocultos e privados, cura, jogadores eliminados, vitória e derrota no limite de ciclos. A comparação acima foi estática e não confirma o comportamento durante uma partida.
7. **Confirmar que o grupo tem no máximo quatro integrantes** e planejar a preparação para a entrevista individual. O repositório não permite verificar o tamanho do grupo nem a preparação de cada pessoa.

O enunciado informa como prazo de entrega **14 de novembro de 2026**. A equipe também deve conferir as orientações da disciplina sobre o local de entrega.
