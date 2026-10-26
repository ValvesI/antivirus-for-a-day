public class Enemy extends Entity {
    protected int tipo, defesa;    

    public Enemy(int linha, int coluna, int vida, int dano, int tipo, int defesa) {
        super(linha, coluna, vida, dano);
        this.tipo = tipo;
        setDefesa(defesa);
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getDefesa() {
        return defesa;
    }

    public void setDefesa(int defesa) {
        if (defesa < 1 || defesa > 3) {
            throw new IllegalArgumentException("A defesa do inimigo deve estar entre 1 e 3.");
        }
        this.defesa = defesa;
    }

}


