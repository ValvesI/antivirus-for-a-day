public class Element {
    protected int linha, coluna;

    public Element(int linha, int coluna) {
        if (linha < 0 || coluna < 0) {
            throw new IllegalArgumentException("A posição do elemento não pode ser negativa.");
        }
        this.linha = linha;
        this.coluna = coluna;
    }

    public void setColuna(int coluna) {
        if (coluna < 0) {
            throw new IllegalArgumentException("A coluna não pode ser negativa.");
        }
        this.coluna = coluna;
    }

    public void setLinha(int linha) {
        if (linha < 0) {
            throw new IllegalArgumentException("A linha não pode ser negativa.");
        }
        this.linha = linha;
    }

    public void update() {}

    public void draw(Renderer renderer) {}

    public Pair<Integer, Integer> getSize() {
        return new Pair<>(1, 1);
    }
}
