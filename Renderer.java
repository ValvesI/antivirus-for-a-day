// Plano, como o jogo é todo é feito no terminal meu plano é criar um sistema de
// grade n x n que é nada mais que uma matriz de chars pra poder renderizar as coisas
// de um jeito mais elegante que só usar um print

import java.util.ArrayList;

public class Renderer {
    // Valores placeholder (definir depois)
    public static final int NUM_LINHAS = 10;
    public static final int NUM_COLUNAS = 10;

    private int largura, altura;
    private char[][] grade;
    private ArrayList<Element> elements;

    public Renderer(int largura, int altura) {
        this.largura = largura;
        this.altura = altura;
        this.grade = new char[largura][altura];
        this.elements = new ArrayList<>();

        for (int linha=0; largura < this.largura; linha++)
            for(int coluna=0; coluna < this.altura; coluna++){
                this.grade[linha][coluna] = ' ';
            }
    }

    public int getLargura() {
        return largura;
    }

    public void setLargura(int largura) {
        this.largura = largura;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public char[][] getGrade() {
        return grade;
    }

    public void setGrade(char[][] grade) {
        this.grade = grade;
    }

    // NOTA: eu não sei se vai precisar dessa função aqui
    // considerando que o jogo não tem atualizações baseadas em frame
    // vou deixar aí, se acharem desnecessário é só apagar
    public void update() {
        for (Element element : this.elements) {
            element.update();
        }
    }

    public void draw() {
        for (Element element : this.elements) {
            element.draw();
        }
    }

   
















}
