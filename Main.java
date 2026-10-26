import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Renderer renderer = new Renderer(90, 33);
        Board board = new Board(3, 1);
        Player selected = board.getPlayers().get(0);
        SectorView sectorView = new SectorView(4, 42, board, selected);
        TextElement status = new TextElement(30, 1, "Os dois jogadores iniciam no centro, com quatro portas.");
        TextElement selection = new TextElement(1, 1, "");
        renderer.addElement(new TextElement(0, 1, "Antivirus por um dia - exploracao de setores"));
        renderer.addElement(selection);
        renderer.addElement(board);
        renderer.addElement(sectorView);
        renderer.addElement(new TextElement(28, 1, "P1: WASD | P2: setas | 1/2: ver jogador | F + 1/2/3: atacar | B: procurar | Q: sair"));
        renderer.addElement(new TextElement(29, 1, "Mapa: vazio nao visitado | . visitado | C centro | * porta | painel: ATK/DEF"));
        renderer.addElement(status);
        renderer.addElement(new TextElement(31, 1, "Suporte selecionado: C + 1/2 para recuperar 2 DEF do jogador escolhido."));

        boolean choosingEnemy = false;
        boolean choosingHealTarget = false;
        try (TerminalInput input = new TerminalInput()) {
            while (true) {
                selection.setText("Jogador selecionado: " + selected.getNome());
                sectorView.setPlayer(selected);
                renderer.update();
                renderer.draw();
                renderer.present();
                int key = input.readKey();
                if (key == -1 || key == 'q' || key == 'Q') break;
                if (key >= 'A' && key <= 'Z') key = Character.toLowerCase((char) key);
                if (choosingHealTarget) {
                    choosingHealTarget = false;
                    if (key == '1' || key == '2') {
                        status.setText(selected.recoverDefense(board.getPlayers().get(key - '1')));
                        continue;
                    }
                }
                if (choosingEnemy) {
                    choosingEnemy = false;
                    if (key >= '1' && key <= '3') {
                        status.setText(board.attack(selected, key - '1'));
                        continue;
                    }
                }
                Direction direction = null;
                switch (key) {
                    case 'w': direction = Direction.NORTH; break;
                    case 's': direction = Direction.SOUTH; break;
                    case 'a': direction = Direction.WEST; break;
                    case 'd': direction = Direction.EAST; break;
                    case TerminalInput.UP: direction = Direction.NORTH; break;
                    case TerminalInput.DOWN: direction = Direction.SOUTH; break;
                    case TerminalInput.LEFT: direction = Direction.WEST; break;
                    case TerminalInput.RIGHT: direction = Direction.EAST; break;
                    case '1': case '2':
                        selected = board.getPlayers().get(key - '1');
                        status.setText("Jogador selecionado: " + selected.getNome());
                        break;
                    case 'b':
                        status.setText(board.search(selected));
                        break;
                    case 'c':
                        if (!selected.isSupport()) {
                            status.setText("Somente P2 (suporte) pode recuperar defesa. Selecione com 2.");
                        } else {
                            choosingHealTarget = true;
                            status.setText("Recuperar 2 DEF de quem? 1: P1 no mesmo setor | 2: proprio P2.");
                        }
                        break;
                    case 'f':
                        choosingEnemy = true;
                        status.setText("Escolha o inimigo: 1, 2 ou 3 (outra tecla cancela).");
                        break;
                    default: break;
                }
                if (direction != null) {
                    selected = board.getPlayers().get(key >= TerminalInput.UP ? 1 : 0);
                    status.setText(selected.getNome() + ": " + board.move(selected, direction));
                }
            }
        }
        catch (IOException exception) {
            System.err.println("Execute em um terminal Linux/macOS (nao no console da IDE): "
                + exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Jogo encerrado.");
    }
}
