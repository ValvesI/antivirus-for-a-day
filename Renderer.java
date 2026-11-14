// Como o jogo é feito no terminal, o renderer usa uma grade de caracteres
// para compor a tela antes de imprimi-la.

import java.util.ArrayList;

public class Renderer {
    public static final int NUM_LINHAS = 10;
    public static final int NUM_COLUNAS = 10;

    private final int largura;
    private final int altura;
    private final char[][] grade;
    private final ArrayList<Element> elements;

    public Renderer() {
        this(NUM_COLUNAS, NUM_LINHAS);
    }

    public Renderer(int largura, int altura) {
        if (largura <= 0 || altura <= 0) {
            throw new IllegalArgumentException("As dimensões do renderer devem ser positivas.");
        }

        this.largura = largura;
        this.altura = altura;
        this.grade = new char[altura][largura];
        this.elements = new ArrayList<>();
        for (char[] linha : grade) {
            for (int coluna = 0; coluna < linha.length; coluna++) {
                linha[coluna] = ' ';
            }
        }
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public char[][] getGrade() {
        char[][] copia = new char[altura][largura];
        for (int linha = 0; linha < altura; linha++) {
            for (int coluna = 0; coluna < largura; coluna++) {
                copia[linha][coluna] = grade[linha][coluna];
            }
        }
        return copia;
    }

    public boolean setChar(int linha, int coluna, char caractere) {
        if (linha < 0 || linha >= altura || coluna < 0 || coluna >= largura) {
            return false;
        }

        grade[linha][coluna] = caractere;
        return true;
    }

    public void addElement(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("O elemento não pode ser nulo.");
        }
        elements.add(element);
    }

    public void removeElement(Element element) {
        elements.remove(element);
    }

    public void clear() {
        for (char[] linha : grade) {
            for (int coluna = 0; coluna < linha.length; coluna++) {
                linha[coluna] = ' ';
            }
        }
    }

    // O jogo é baseado em turnos, mas o método permanece disponível para
    // atualizar todos os elementos quando uma ação acontecer.
    public void update() {
        for (Element element : elements) {
            element.update();
        }
    }

    public void draw() {
        clear();
        for (Element element : elements) {
            element.draw(this);
        }
    }

    public String render() {
        StringBuilder frame = new StringBuilder((largura + 1) * altura);

        for (int linha = 0; linha < altura; linha++) {
            frame.append(grade[linha]);
            if (linha < altura - 1) {
                frame.append(System.lineSeparator());
            }
        }

        return frame.toString();
    }

    public void present() {
        // Limpa a tela e reposiciona o cursor antes de apresentar o quadro.
        System.out.print("\033[2J\033[H" + render() + System.lineSeparator());
        System.out.flush();
    }
}
