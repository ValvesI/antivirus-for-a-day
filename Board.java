import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Board extends Element {
    public static final int SIZE = 5;
    private final Sector[][] sectors = new Sector[SIZE][SIZE];
    private final Random random;
    private final List<Player> players;
    private final GridBox map;
    private final Sector infectionSource;
    private Player winner;
    public enum Phase { MOVE_P1, MOVE_P2, P1, P2, ENEMIES, FINISHED }
    private Phase phase = Phase.MOVE_P1;
    private int cycle;
    private int actionsLeft = 2;
    private String ending = "";

    public Phase getPhase() { return phase; }
    public int getCycle() { return cycle; }
    public int getActionsLeft() { return actionsLeft; }
    public String getEnding() { return ending; }
    public boolean isMoving() { return phase == Phase.MOVE_P1 || phase == Phase.MOVE_P2; }
    public Player getActivePlayer() {
        if (phase == Phase.MOVE_P1 || phase == Phase.P1) return players.get(0);
        if (phase == Phase.MOVE_P2 || phase == Phase.P2) return players.get(1);
        return null;
    }

    private String checkTurn(Player player, boolean movement) {
        if (isFinished()) return "A partida ja terminou.";
        if (player != getActivePlayer()) return "Aguarde o turno do seu jogador.";
        if (player.getDefesa() <= 0) return "Jogador eliminado.";
        if (isMoving() != movement)
            return movement ? "Movimente-se na fase de movimento." : "Escolha o movimento primeiro (E para ficar).";
        return null;
    }

    private boolean hasAvailableAction() {
        Player player = getActivePlayer();
        if (player == null || player.getDefesa() <= 0) return false;
        Sector sector = getSector(player);
        return sector.hasEnemies() || sector.getType() != Sector.Type.PRIVATE
            || (player.isSupport() && actionsLeft == 2);
    }

    private void advancePhase() {
        switch (phase) {
            case MOVE_P1: phase = Phase.MOVE_P2; break;
            case MOVE_P2:
                if (cycle == 0) cycle = 1;
                phase = Phase.P1;
                break;
            case P1: phase = Phase.P2; break;
            case P2: phase = Phase.ENEMIES; break;
            default: return;
        }
        actionsLeft = 2;
        if (getActivePlayer() != null && (getActivePlayer().getDefesa() <= 0
                || (!isMoving() && !hasAvailableAction()))) advancePhase();
    }

    private void consumeAction() {
        if (--actionsLeft == 0 || !hasAvailableAction()) advancePhase();
    }

    public String endTurn(Player player) {
        String error = checkTurn(player, false);
        if (error != null) return error;
        advancePhase();
        return player.getNome() + " encerrou seu turno.";
    }

    public String stay(Player player) {
        String error = checkTurn(player, true);
        if (error != null) return error;
        if (cycle == 0) return "No turno inicial, escolha uma porta para sair do centro.";
        advancePhase();
        return player.getNome() + " ficou no setor.";
    }

    public String recoverDefense(Player player, Player target) {
        String error = checkTurn(player, false);
        if (error != null) return error;
        if (!player.isSupport()) return "Somente o suporte pode recuperar defesa.";
        if (!players.contains(target) || target.getDefesa() <= 0)
            return "Alvo invalido ou eliminado.";
        if (getSector(player) != getSector(target)) return "O alvo precisa estar no mesmo setor.";
        if (actionsLeft != 2) return "A cura substitui as duas acoes: escolha no inicio do turno.";
        String result = player.recoverDefense(target);
        advancePhase();
        return result;
    }

    public String resolveEnemyTurn() {
        if (phase != Phase.ENEMIES) return "Nao e o turno dos inimigos.";
        StringBuilder log = new StringBuilder("Inimigos: ");
        int attacks = 0;
        for (Sector[] row : sectors) for (Sector sector : row) {
            for (Enemy enemy : sector.getEnemies()) {
                if (enemy.getDefesa() <= 0) continue;
                for (Player player : players) {
                    if (player.getDefesa() <= 0 || getSector(player) != sector) continue;
                    int roll = random.nextInt(6) + 1;
                    boolean hit = roll % 2 == 0;
                    if (hit) player.setDefesa(Math.max(0, player.getDefesa() - enemy.getDano()));
                    log.append(player.getNome()).append(" dado ").append(roll)
                        .append(hit ? " -" + enemy.getDano() + " DEF; " : " errou; ");
                    attacks++;
                }
            }
        }
        if (attacks == 0) log.append("nenhum ataque. ");
        if (players.stream().allMatch(player -> player.getDefesa() <= 0)) {
            ending = "Derrota: todos os jogadores foram eliminados.";
            phase = Phase.FINISHED;
        } else if (cycle == 25) {
            ending = "Derrota: limite de 25 ciclos atingido.";
            phase = Phase.FINISHED;
        } else {
            cycle++;
            phase = Phase.MOVE_P1;
            actionsLeft = 2;
            if (getActivePlayer().getDefesa() <= 0) advancePhase();
        }
        return log.toString() + ending;
    }

    // Cada bit representa uma porta: norte=1, leste=2, sul=4, oeste=8.
    // As ligacoes sao fixas e reciprocas; todos os setores sao acessiveis.
    private static final int[][] DOORS = {
        {6, 14, 12, 6, 12},
        {5, 3, 15, 9, 5},
        {7, 14, 15, 14, 13},
        {5, 3, 15, 9, 5},
        {3, 10, 11, 10, 9}
    };

    public Board(int linha, int coluna) { this(linha, coluna, new Random()); }

    public Board(int linha, int coluna, Random random) {
        super(linha, coluna);
        this.random = random;
        map = new GridBox(linha + 2, coluna + 3, 21, 31, SIZE);
        for (int y = 1; y <= SIZE; y++) {
            for (int x = 1; x <= SIZE; x++) {
                Sector.Type type = (x == 3 && y == 3) ? Sector.Type.NORMAL
                    : Sector.Type.values()[(x + y) % 3];
                Sector sector = new Sector(x, y, type);
                for (Direction direction : Direction.values()) {
                    if ((DOORS[y - 1][x - 1] & (1 << direction.ordinal())) != 0)
                        sector.openDoor(direction);
                }
                sectors[y - 1][x - 1] = sector;
            }
        }
        // Sorteia uma das 24 posicoes nao centrais com a mesma probabilidade.
        int sourceIndex = random.nextInt(SIZE * SIZE - 1);
        int centerIndex = (SIZE * SIZE) / 2;
        if (sourceIndex >= centerIndex) sourceIndex++;
        infectionSource = getSector(sourceIndex % SIZE + 1, sourceIndex / SIZE + 1);
        Player p1 = new Player("P1", 2, 6);
        Player p2 = new Player("P2", 1, 7);
        p2.setTipo(1);
        players = Arrays.asList(p1, p2);
        for (Player player : players) { player.setLinha(3); player.setColuna(3); }
        getSector(3, 3).visit(random);
    }

    public Sector getSector(int x, int y) {
        if (x < 1 || x > SIZE || y < 1 || y > SIZE)
            throw new IllegalArgumentException("Setor fora do tabuleiro.");
        return sectors[y - 1][x - 1];
    }
    public List<Player> getPlayers() { return java.util.Collections.unmodifiableList(players); }
    public Sector getSector(Player player) { return getSector(player.getColuna(), player.getLinha()); }

    public Sector getInfectionSource() { return infectionSource; }
    public Player getWinner() { return winner; }
    public boolean isFinished() { return phase == Phase.FINISHED; }

    public String move(Player player, Direction direction) {
        String error = checkTurn(player, true);
        if (error != null) return error;
        Sector current = getSector(player);
        if (current.hasEnemies()) return "Elimine os inimigos antes de sair.";
        if (!current.hasDoor(direction)) return "Existe uma parede nessa direcao.";
        int x = player.getColuna() + direction.getDx();
        int y = player.getLinha() + direction.getDy();
        Sector destination = getSector(x, y);
        player.setColuna(x);
        player.setLinha(y);
        destination.visit(random);
        if (destination == infectionSource) {
            winner = player;
            phase = Phase.FINISHED;
            ending = player.getNome() + " encontrou a fonte de infeccao! Vitoria!";
            return player.getNome() + " encontrou a fonte de infeccao! Vitoria!";
        }
        advancePhase();
        return "Entrou no setor [" + x + "," + y + "].";
    }

    public String search(Player player) {
        String error = checkTurn(player, false);
        if (error != null) return error;
        if (getSector(player).getType() == Sector.Type.PRIVATE)
            return "Nao e permitido procurar em setor privado.";
        String result = getSector(player).search(player, random);
        consumeAction();
        return result;
    }

    public String attack(Player player, int index) {
        String error = checkTurn(player, false);
        if (error != null) return error;
        if (index < 0 || index >= getSector(player).getEnemies().size())
            return "Inimigo inexistente.";
        String result = getSector(player).attack(player, index, random);
        consumeAction();
        return result;
    }

    @Override
    public void draw(Renderer renderer) {
        map.draw(renderer);
        for (int n = 1; n <= SIZE; n++) {
            Pair<Integer, Integer> center = map.getCellCenter(n, n);
            new TextElement(linha, center.getSecond(), String.valueOf(n)).draw(renderer);
            new TextElement(center.getFirst(), coluna, String.valueOf(n)).draw(renderer);
        }
        for (Sector[] row : sectors) for (Sector sector : row) {
            Pair<Integer, Integer> center = map.getCellCenter(sector.getX(), sector.getY());
            int r = center.getFirst(), c = center.getSecond();
            if (sector.isVisited()) {
                for (Direction direction : Direction.values()) {
                    if (sector.hasDoor(direction))
                        renderer.setChar(r + direction.getDy() * 2,
                            c + direction.getDx() * 3, '*');
                }
            }
            String marker = sector.isVisited() ? "." : " ";
            if (sector.getX() == 3 && sector.getY() == 3) marker = "C";
            if (sector == infectionSource) marker = "X";
            String occupants = "";
            int occupantCount = 0;
            for (Player player : players)
                if (player.getDefesa() > 0 && player.getColuna() == sector.getX() && player.getLinha() == sector.getY())
                {
                    occupants = player.getNome();
                    occupantCount++;
                }
            if (occupantCount > 1) occupants = "P";
            String label = occupants.isEmpty() ? marker : occupants;
            new TextElement(r, c - label.length() / 2, label).draw(renderer);
            if (sector == infectionSource && occupantCount > 0)
                renderer.setChar(r + 1, c, 'X');
        }
    }
}
