public class PlayerView extends Box {
    private final Player player;
    private final TextElement nomeText;
    private final TextElement ataqueText;
    private final TextElement defesaText;

    @SuppressWarnings("this-escape")
    public PlayerView(int linha, int coluna, Player player) {
        super(linha, coluna, 5, 20);

        this.player = player;
        this.nomeText = new TextElement(linha + 1, coluna + 1, "Nome: " + player.getNome());
        this.ataqueText = new TextElement(linha + 2, coluna + 1, "ATK: " + player.getAtaque());
        this.defesaText = new TextElement(linha + 3, coluna + 1, "DEF: " + player.getDefesa());

        addElement(nomeText);
        addElement(ataqueText);
        addElement(defesaText);
    }

    @Override
    public void update() {
        nomeText.setText("Nome: " + player.getNome());
        ataqueText.setText("ATK: " + player.getAtaque());
        defesaText.setText("DEF: " + player.getDefesa());
        super.update();
    }
}
