public class Player extends Entity {
    private String nome;
    private int tipo;
    private int defesa;

    public Player(int linha, int coluna, int vida, int dano, String nome, int tipo) {
        super(linha, coluna, vida, dano);
        this.nome = nome;
        this.tipo = tipo;
    }

    public Player(String nome, int ataque, int defesa) {
        this(0, 0, 0, ataque, nome, 0);
        this.defesa = defesa;
    }

    public boolean isSupport() { return tipo == 1; }

    public String recoverDefense(Player target) {
        if (!isSupport()) return "Somente o suporte pode recuperar defesa.";
        if (defesa <= 0) return "Jogador eliminado nao pode recuperar defesa.";
        if (target == null || target.getDefesa() <= 0)
            return "Nao e possivel recuperar um jogador eliminado.";
        if (linha != target.getLinha() || coluna != target.getColuna())
            return "O alvo precisa estar no mesmo setor do suporte.";
        // O enunciado adiciona 2 a DEF, sem estabelecer um teto.
        target.setDefesa(target.getDefesa() + 2);
        return target.getNome() + " recuperou 2 DEF. DEF atual: " + target.getDefesa();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getAtaque() {
        return dano;
    }

    public void setAtaque(int ataque) {
        setDano(ataque);
    }

    public int getDefesa() {
        return defesa;
    }

    public void setDefesa(int defesa) {
        this.defesa = defesa;
    }
}
