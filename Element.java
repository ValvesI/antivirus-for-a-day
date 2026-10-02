public class Element {
    
    protected int linha, coluna;

    public Element(int linha, int coluna) {
        setLinha(linha);
        setColuna(coluna);  
    }

    public void setColuna(int coluna){
        if (coluna >= 0 && coluna < Renderer.NUM_COLUNAS)
            this.coluna = coluna;
    }

    public void setLinha(int linha){
        if (linha >= 0 && linha < Renderer.NUM_LINHAS)
            this.linha = linha;
    }

    public void update(){}

    public void draw(){}
}
