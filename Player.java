public class Player extends Entity {
    private String nome;
    private int tipo;

    public Player(int linha, int coluna, int vida, int dano, String nome, int tipo) {
        super(linha, coluna, vida, dano);
        this.nome = nome;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public int getTipo() {
        return tipo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}