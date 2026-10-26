public abstract class Entity {
    protected int linha, coluna, vida, dano;

    public Entity(int linha, int coluna, int vida, int dano) {
        setLinha(linha);
        setColuna(coluna);
        setVida(vida);
        setDano(dano);  
    }

    public int getLinha() { return linha; }
    public int getColuna() { return coluna; }
    public int getDano() { return dano; }

    public void setColuna(int coluna){
        if (coluna >= 0 && coluna < Renderer.NUM_COLUNAS)
            this.coluna = coluna;
    }

    public void setLinha(int linha){
        if (linha >= 0 && linha < Renderer.NUM_LINHAS)
            this.linha = linha;
    }

    public void setVida(int vida){
        if (vida >= 0)
            this.vida = vida;
    }

    public void setDano(int dano){
        if (dano < 1 || dano > 3) {
            throw new IllegalArgumentException("O ataque deve estar entre 1 e 3.");
        }
        this.dano = dano;
    }

    
}
