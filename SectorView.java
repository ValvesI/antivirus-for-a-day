public class SectorView extends Element {
    private final Board board;
    private Player player;

    public SectorView(int linha, int coluna, Board board, Player player) {
        super(linha, coluna);
        this.board = board;
        this.player = player;
    }

    public void setPlayer(Player player) { this.player = player; }

    @Override
    public void draw(Renderer renderer) {
        Sector sector = board.getSector(player);
        new TextElement(linha, coluna, "Setor [" + sector.getX() + "," + sector.getY() + "]").draw(renderer);
        String type;
        switch (sector.getType()) {
            case HIDDEN: type = "Oculto"; break;
            case PRIVATE: type = "Privado (sem procura)"; break;
            default: type = "Normal";
        }
        new TextElement(linha + 1, coluna, type).draw(renderer);
        new Box(linha + 3, coluna, 11, 33, false).draw(renderer);
        for (Direction direction : Direction.values()) {
            if (sector.hasDoor(direction))
                renderer.setChar(linha + 8 + direction.getDy() * 5,
                    coluna + 16 + direction.getDx() * 16, '*');
        }
        new TextElement(linha + 4, coluna + 2, "Inimigos (ATK/DEF)").draw(renderer);
        for (int i = 0; i < sector.getEnemies().size(); i++) {
            Enemy enemy = sector.getEnemies().get(i);
            new TextElement(linha + 6, coluna + 2 + i * 10,
                (i + 1) + ":" + enemy.getDano() + "/" + enemy.getDefesa()).draw(renderer);
        }
        if (!sector.hasEnemies())
            new TextElement(linha + 6, coluna + 2, "Sem inimigos").draw(renderer);
        int offset = 3;
        for (Player occupant : board.getPlayers()) {
            if (occupant.getLinha() == sector.getY() && occupant.getColuna() == sector.getX()) {
                new TextElement(linha + 9, coluna + offset, occupant.getNome()
                    + (occupant == player ? " <" : "")).draw(renderer);
                new TextElement(linha + 10, coluna + offset,
                    occupant.getAtaque() + "/" + occupant.getDefesa()).draw(renderer);
                offset += 14;
            }
        }
    }
}
