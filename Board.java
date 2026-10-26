import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Board extends Element {
    public static final int SIZE = 5;
    private final Sector[][] sectors = new Sector[SIZE][SIZE];
    private final Random random;
    private final List<Player> players;
    private final GridBox map;

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

    public String move(Player player, Direction direction) {
        Sector current = getSector(player);
        if (current.hasEnemies()) return "Elimine os inimigos antes de sair.";
        if (!current.hasDoor(direction)) return "Existe uma parede nessa direcao.";
        int x = player.getColuna() + direction.getDx();
        int y = player.getLinha() + direction.getDy();
        Sector destination = getSector(x, y);
        player.setColuna(x);
        player.setLinha(y);
        destination.visit(random);
        return "Entrou no setor [" + x + "," + y + "].";
    }

    public String search(Player player) {
        return getSector(player).search(player, random);
    }

    public String attack(Player player, int index) {
        return getSector(player).attack(player, index, random);
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
            String occupants = "";
            int occupantCount = 0;
            for (Player player : players)
                if (player.getColuna() == sector.getX() && player.getLinha() == sector.getY())
                {
                    occupants = player.getNome();
                    occupantCount++;
                }
            if (occupantCount > 1) occupants = "P";
            String label = occupants.isEmpty() ? marker : occupants;
            new TextElement(r, c - label.length() / 2, label).draw(renderer);
        }
    }
}
