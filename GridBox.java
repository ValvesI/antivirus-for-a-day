public class GridBox extends Box {
    private int gridSize;

    public GridBox(int linha, int coluna, int tamanho, int gridSize) {
        this(linha, coluna, tamanho, tamanho, gridSize);
    }

    public GridBox(int linha, int coluna, int altura, int largura, int gridSize) {
        super(linha, coluna, altura, largura);
        validarGridSize(gridSize, altura, largura);
        this.gridSize = gridSize;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        validarGridSize(gridSize, getAltura(), getLargura());
        this.gridSize = gridSize;
    }

    public Pair<Integer, Integer> getCellCenter(int x, int y) {
        if (x < 1 || x > gridSize || y < 1 || y > gridSize) {
            throw new IllegalArgumentException("A posição deve estar dentro da grade.");
        }

        int inicioLinha = calcularPosicao(linha, getAltura(), y - 1);
        int fimLinha = calcularPosicao(linha, getAltura(), y);
        int inicioColuna = calcularPosicao(coluna, getLargura(), x - 1);
        int fimColuna = calcularPosicao(coluna, getLargura(), x);

        int centroLinha = (inicioLinha + fimLinha) / 2;
        int centroColuna = (inicioColuna + fimColuna) / 2;
        return new Pair<>(centroLinha, centroColuna);
    }

    @Override
    public void setAltura(int altura) {
        if (gridSize > 0 && altura <= gridSize) {
            throw new IllegalArgumentException(
                "A altura da box deve ser maior que o tamanho da grade."
            );
        }
        super.setAltura(altura);
    }

    @Override
    public void setLargura(int largura) {
        if (gridSize > 0 && largura <= gridSize) {
            throw new IllegalArgumentException(
                "A largura da box deve ser maior que o tamanho da grade."
            );
        }
        super.setLargura(largura);
    }

    @Override
    public void draw(Renderer renderer) {
        drawBorder(renderer);

        int ultimaLinha = linha + getAltura() - 1;
        int ultimaColuna = coluna + getLargura() - 1;

        for (int divisao = 1; divisao < gridSize; divisao++) {
            int linhaDivisoria = calcularPosicao(linha, getAltura(), divisao);
            int colunaDivisoria = calcularPosicao(coluna, getLargura(), divisao);

            for (int colunaAtual = coluna; colunaAtual <= ultimaColuna; colunaAtual++) {
                renderer.setChar(linhaDivisoria, colunaAtual, '-');
            }

            for (int linhaAtual = linha; linhaAtual <= ultimaLinha; linhaAtual++) {
                renderer.setChar(linhaAtual, colunaDivisoria, '|');
            }
        }

        for (int divisaoLinha = 0; divisaoLinha <= gridSize; divisaoLinha++) {
            int linhaIntersecao = calcularPosicao(linha, getAltura(), divisaoLinha);

            for (int divisaoColuna = 0; divisaoColuna <= gridSize; divisaoColuna++) {
                int colunaIntersecao = calcularPosicao(coluna, getLargura(), divisaoColuna);
                renderer.setChar(linhaIntersecao, colunaIntersecao, '+');
            }
        }

        drawElements(renderer);
    }

    private int calcularPosicao(int inicio, int tamanho, int divisao) {
        double proporcao = (double) divisao / gridSize;
        return inicio + (int) Math.round((tamanho - 1) * proporcao);
    }

    private static void validarGridSize(int gridSize, int altura, int largura) {
        if (gridSize <= 0) {
            throw new IllegalArgumentException("O tamanho da grade deve ser positivo.");
        }
        if (gridSize >= altura || gridSize >= largura) {
            throw new IllegalArgumentException(
                "A box precisa ter espaço para todas as células da grade."
            );
        }
    }
}
