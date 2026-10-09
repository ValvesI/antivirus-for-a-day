public class TextElement extends Element {
    private String text;

    public TextElement(int linha, int coluna, String text) {
        super(linha, coluna);
        if (text == null) {
            throw new IllegalArgumentException("O texto não pode ser nulo.");
        }
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("O texto não pode ser nulo.");
        }
        this.text = text;
    }

    @Override
    public void draw(Renderer renderer) {
        for (int i = 0; i < text.length(); i++) {
            renderer.setChar(linha, coluna + i, text.charAt(i));
        }
    }

    @Override
    public Pair<Integer, Integer> getSize() {
        return new Pair<>(1, text.length());
    }
}
