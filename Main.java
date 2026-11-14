import java.util.Scanner;

/*
    GRR20245988 Iago Pereira Ramalho
    GRR20244503 Letícia de Oliveira Santos
    GRR20255200 Lucas Barroso Leal
    GRR20252276 Vinícius Henrique Alves de Souza
*/

public class Main {
    public static void main(String[] args) {
        Renderer renderer = new Renderer(90, 42);
        Board board = new Board(3, 1);
        Player jogadorDaVez = board.getPlayers().get(0);
        SectorView sectorView = new SectorView(4, 42, board, jogadorDaVez);
        TextElement status = new TextElement(30, 1,
                "Os dois jogadores iniciam no centro, com quatro portas.");
        TextElement selection = new TextElement(1, 1, "");

        renderer.addElement(new TextElement(0, 1,
                "Antivirus por um dia - exploracao de setores"));
        renderer.addElement(selection);
        renderer.addElement(board);
        renderer.addElement(sectorView);
        renderer.addElement(new TextElement(28, 1,
                "Mover: W/A/S/D | F + 1/2/3: atacar | B: procurar | Q: sair"));
        renderer.addElement(new TextElement(29, 1,
                "Mapa: vazio nao visitado | . visitado | C centro | X fonte | * porta | ATK/DEF"));
        renderer.addElement(status);
        renderer.addElement(new TextElement(27, 1,
                "C + 1/2: cura (substitui 2 acoes) | E: ficar / encerrar turno"));

        TextElement[] details = new TextElement[9];
        for (int i = 0; i < details.length; i++) {
            details[i] = new TextElement(32 + i, 1, "");
            renderer.addElement(details[i]);
        }

        String registroInimigos = "";
        boolean escolhendoInimigo = false;
        boolean escolhendoAlvoCura = false;
        Scanner entrada = new Scanner(System.in);

        while (true) {
            if (board.getActivePlayer() != null) {
                jogadorDaVez = board.getActivePlayer();
            }

            String fase = board.getPhase().toString();
            selection.setText("Ciclo " + board.getCycle() + "/25 | Fase: " + fase
                    + " | Acoes: " + (board.isMoving() ? "movimento" : board.getActionsLeft()));

            for (int i = 0; i < details.length; i++) {
                int inicio = i * 87;
                if (inicio < registroInimigos.length()) {
                    int fim = Math.min(inicio + 87, registroInimigos.length());
                    details[i].setText(registroInimigos.substring(inicio, fim));
                } else {
                    details[i].setText("");
                }
            }

            sectorView.setPlayer(jogadorDaVez);
            renderer.update();
            renderer.draw();
            renderer.present();

            if (board.isFinished()) {
                break;
            }
            if (board.getPhase() == Board.Phase.ENEMIES) {
                registroInimigos = board.resolveEnemyTurn();
                if (board.isFinished()) {
                    status.setText(board.getEnding());
                } else {
                    status.setText("Inimigos finalizaram. Novo ciclo.");
                }
                continue;
            }

            System.out.print("Comando: ");
            if (!entrada.hasNextLine()) {
                break;
            }

            String comando = entrada.nextLine().trim().toLowerCase();
            if (comando.isEmpty()) {
                continue;
            }

            char tecla = comando.charAt(0);
            if (tecla == 'q') {
                break;
            }

            if (escolhendoAlvoCura) {
                escolhendoAlvoCura = false;
                if (tecla == '1' || tecla == '2') {
                    status.setText(board.recoverDefense(jogadorDaVez,
                            board.getPlayers().get(tecla - '1')));
                    continue;
                }
            }

            if (escolhendoInimigo) {
                escolhendoInimigo = false;
                if (tecla >= '1' && tecla <= '3') {
                    status.setText(board.attack(jogadorDaVez, tecla - '1'));
                    continue;
                }
            }

            Direction direcao = null;
            switch (tecla) {
                case 'w':
                    direcao = Direction.NORTH;
                    break;
                case 's':
                    direcao = Direction.SOUTH;
                    break;
                case 'a':
                    direcao = Direction.WEST;
                    break;
                case 'd':
                    direcao = Direction.EAST;
                    break;
                case 'e':
                    if (board.isMoving()) {
                        status.setText(board.stay(jogadorDaVez));
                    } else {
                        status.setText(board.endTurn(jogadorDaVez));
                    }
                    break;
                case 'b':
                    status.setText(board.search(jogadorDaVez));
                    break;
                case 'c':
                    if (!jogadorDaVez.isSupport()) {
                        status.setText("Somente P2 (suporte) pode recuperar defesa.");
                    } else {
                        escolhendoAlvoCura = true;
                        status.setText("Recuperar 2 DEF de quem? 1: P1 no mesmo setor | 2: proprio P2.");
                    }
                    break;
                case 'f':
                    escolhendoInimigo = true;
                    status.setText("Escolha o inimigo: 1, 2 ou 3 (outra tecla cancela).");
                    break;
                default:
                    break;
            }

            if (direcao != null) {
                status.setText(jogadorDaVez.getNome() + ": "
                        + board.move(jogadorDaVez, direcao));
            }
        }

        System.out.println("Jogo encerrado.");
    }
}
