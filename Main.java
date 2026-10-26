import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Renderer renderer = new Renderer(60, 16);

        TextElement titulo = new TextElement(
            0, 1, "Antivirus for a Day - teste"
        );

        GridBox tabuleiro = new GridBox(2, 1, 11, 21, 5);

        // As células usam coordenadas de 1 a 5.
        int setorX = 3;
        int setorY = 3;
        Pair<Integer, Integer> centro = tabuleiro.getCellCenter(setorX, setorY);
        TextElement jogador = new TextElement(
            centro.getFirst(), centro.getSecond(), "P"
        );
        tabuleiro.addElement(jogador);

        // A entidade guarda a posição no tabuleiro; o texto usa a posição na tela.
        int inimigoX = 4;
        int inimigoY = 3;
        int ataqueInimigo = 2;
        Enemy inimigo = new Enemy(inimigoY, inimigoX, 2, ataqueInimigo, 0, 2);
        Pair<Integer, Integer> centroInimigo = tabuleiro.getCellCenter(inimigoX, inimigoY);
        TextElement inimigoVisual = new TextElement(
            centroInimigo.getFirst(), centroInimigo.getSecond(), "E"
        );
        tabuleiro.addElement(inimigoVisual);

        Box painel = new Box(2, 25, 8, 25);
        painel.addElement(new TextElement(3, 27, "Jogador: P"));
        TextElement setor = new TextElement(4, 27, "");
        painel.addElement(setor);
        painel.addElement(new TextElement(5, 27, "Inimigo: E"));
        painel.addElement(new TextElement(6, 27, "Setor inimigo: " + inimigoX + ", " + inimigoY));
        TextElement atributosInimigo = new TextElement(7, 27, "");
        painel.addElement(atributosInimigo);
        renderer.addElement(new TextElement(14, 1, "WASD + Enter: mover | Q + Enter: sair"));

        renderer.addElement(titulo);
        renderer.addElement(tabuleiro);
        renderer.addElement(painel);

        try (Scanner entrada = new Scanner(System.in)) {
            while (true) {
                centro = tabuleiro.getCellCenter(setorX, setorY);
                jogador.setLinha(centro.getFirst());
                jogador.setColuna(centro.getSecond());
                setor.setText("Setor: " + setorX + ", " + setorY);
                atributosInimigo.setText("ATK: " + ataqueInimigo + " DEF: " + inimigo.getDefesa());
                // Exibe os dois símbolos quando ocupam o mesmo setor.
                inimigoVisual.setText(setorX == inimigoX && setorY == inimigoY ? "PE" : "E");

                renderer.update();
                renderer.draw();
                // Limpa a tela em terminais com suporte a ANSI.
                if (System.console() != null) {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                }
                renderer.present();
                System.out.print("Comando: ");
                System.out.flush();

                if (!entrada.hasNextLine()) {
                    break;
                }
                String comando = entrada.nextLine().trim();
                if (comando.equalsIgnoreCase("q")) {
                    break;
                }

                // Mantém o jogador dentro dos setores de 1 a gridSize.
                switch (comando.toLowerCase(java.util.Locale.ROOT)) {
                    case "w": setorY = Math.max(1, setorY - 1); break;
                    case "s": setorY = Math.min(tabuleiro.getGridSize(), setorY + 1); break;
                    case "a": setorX = Math.max(1, setorX - 1); break;
                    case "d": setorX = Math.min(tabuleiro.getGridSize(), setorX + 1); break;
                    default: System.out.println("Comando inválido. Use W, A, S, D ou Q.");
                }
            }
        }
        System.out.println("Jogo encerrado.");
    }
}
