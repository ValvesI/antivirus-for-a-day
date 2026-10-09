import java.util.ArrayList;

public class Box extends Element {
    private int largura, altura;
    private boolean isAdjustable;
    private final ArrayList<Element> elements;

    public Box(int linha, int coluna) {
        this(linha, coluna, 1, 1, true);
    }

    // Para o caso de a box ser quadrada.
    public Box(int linha, int coluna, int tamanho) {
        this(linha, coluna, tamanho, tamanho, true);
    }

    public Box(int linha, int coluna, int altura, int largura) {
        this(linha, coluna, altura, largura, true);
    }

    public Box(int linha, int coluna, int altura, int largura, boolean isAdjustable) {
        super(linha, coluna);
        if (altura <= 0 || largura <= 0) {
            throw new IllegalArgumentException("As dimensões da box devem ser positivas.");
        }
        this.elements = new ArrayList<>();
        this.altura = altura;
        this.largura = largura;
        this.isAdjustable = isAdjustable;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        if (altura <= 0) {
            throw new IllegalArgumentException("A altura da box deve ser positiva.");
        }
        this.altura = altura;
    }

    public int getLargura() {
        return largura;
    }

    public void setLargura(int largura) {
        if (largura <= 0) {
            throw new IllegalArgumentException("A largura da box deve ser positiva.");
        }
        this.largura = largura;
    }

    public final void addElement(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("O elemento não pode ser nulo.");
        }

        if (isAdjustable) {
            Pair<Integer, Integer> tamanho = element.getSize();
            altura = Math.max(altura, tamanho.getFirst());
            largura = Math.max(largura, tamanho.getSecond());
        }

        elements.add(element);
    }

    public void removeElement(Element element) {
        elements.remove(element);
    }

    public boolean isAdjustable() {
        return isAdjustable;
    }

    public void setAdjustable(boolean adjustable) {
        isAdjustable = adjustable;
    }

    @Override
    public Pair<Integer, Integer> getSize() {
        return new Pair<>(altura, largura);
    }

    @Override
    public void draw(Renderer renderer) {
        drawBorder(renderer);
        drawElements(renderer);
    }

    protected void drawBorder(Renderer renderer) {
        int ultimaLinha = linha + altura - 1;
        int ultimaColuna = coluna + largura - 1;

        for (int colunaAtual = coluna; colunaAtual <= ultimaColuna; colunaAtual++) {
            renderer.setChar(linha, colunaAtual, '-');
            renderer.setChar(ultimaLinha, colunaAtual, '-');
        }

        for (int linhaAtual = linha; linhaAtual <= ultimaLinha; linhaAtual++) {
            renderer.setChar(linhaAtual, coluna, '|');
            renderer.setChar(linhaAtual, ultimaColuna, '|');
        }

        renderer.setChar(linha, coluna, '+');
        renderer.setChar(linha, ultimaColuna, '+');
        renderer.setChar(ultimaLinha, coluna, '+');
        renderer.setChar(ultimaLinha, ultimaColuna, '+');
    }

    protected void drawElements(Renderer renderer) {
        for (Element element : elements) {
            element.draw(renderer);
        }
    }

    @Override
    public void update() {
        for (Element element : elements) {
            element.update();
        }
    }
}
