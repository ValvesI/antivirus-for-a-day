import java.util.ArrayList;

public class Box extends Element {
    private int largura, altura;
    private ArrayList<Element> elements;

    public Box(int linha, int coluna) {
        super(linha, coluna);
        this.largura = 1;
        this.altura = 1;
    }

    // Pra caso a box seja quadrada
    public Box(int linha, int coluna, int tamanho) {
        this(linha, coluna, tamanho, tamanho);
    }

    public Box(int linha, int coluna, int altura, int largura) {
        super(linha, coluna);
        this.altura = altura;
        this.largura = largura;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public int getLargura() {
        return largura;
    }

    public void setLargura(int largura) {
        this.largura = largura;
    }

    @Override
    public void draw() {

        
        super.draw();
    }


    @Override 
    public void update(){
        
    }

}
