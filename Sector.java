import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class Sector {
    public enum Type { NORMAL, HIDDEN, PRIVATE }

    private final int x, y;
    private final Type type;
    private final EnumSet<Direction> doors = EnumSet.noneOf(Direction.class);
    private final List<Enemy> enemies = new ArrayList<>();
    private boolean visited;

    public Sector(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Type getType() { return type; }
    public boolean isVisited() { return visited; }
    public boolean hasDoor(Direction direction) { return doors.contains(direction); }
    void openDoor(Direction direction) { doors.add(direction); }
    public List<Enemy> getEnemies() { return Collections.unmodifiableList(enemies); }
    public boolean hasEnemies() { return !enemies.isEmpty(); }

    public void visit(Random random) {
        if (visited) return;
        visited = true;
        if (x == 3 && y == 3) return;
        int count = random.nextInt(3) + 1;
        for (int i = 0; i < count; i++) {
            int power = random.nextInt(3) + 1;
            enemies.add(new Enemy(y, x, power, power, 0, power));
        }
    }

    public String search(Player player, Random random) {
        if (player.getColuna() != x || player.getLinha() != y)
            return "O jogador nao esta neste setor.";
        if (player.getDefesa() <= 0) return "Jogador eliminado nao pode procurar.";
        if (type == Type.PRIVATE) return "Nao e permitido procurar em setor privado.";

        int roll = random.nextInt(6) + 1;
        String prefix = "Procura (dado " + roll + "): ";
        if (roll <= 3) return prefix + "nada encontrado.";
        if (roll <= 5) {
            int recovery = roll == 4 ? 1 : 2;
            player.setDefesa(player.getDefesa() + recovery);
            return prefix + player.getNome() + " ganhou " + recovery + " DEF.";
        }
        for (Enemy enemy : enemies)
            enemy.setDefesa(Math.max(0, enemy.getDefesa() - 1));
        enemies.removeIf(enemy -> enemy.getDefesa() == 0);
        return prefix + "todos os inimigos locais perderam 1 DEF.";
    }

    public String attack(Player player, int enemyIndex, Random random) {
        if (player.getColuna() != x || player.getLinha() != y)
            return "O jogador nao esta neste setor.";
        if (enemyIndex < 0 || enemyIndex >= enemies.size())
            return "Inimigo inexistente.";
        if (type == Type.HIDDEN && !random.nextBoolean())
            return "O ataque errou no setor oculto.";
        Enemy enemy = enemies.get(enemyIndex);
        enemy.setDefesa(Math.max(0, enemy.getDefesa() - player.getAtaque()));
        if (enemy.getDefesa() == 0) {
            enemies.remove(enemyIndex);
            return "Inimigo eliminado.";
        }
        return "Ataque realizado.";
    }
}
