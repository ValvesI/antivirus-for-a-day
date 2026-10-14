<<<<<<< HEAD
public class Player extends Entity {
    private String nome;
    private int tipo;

    public Player(int linha, int coluna, int vida, int dano, String nome, int tipo) {
        super(linha, coluna, vida, dano);
        this.nome = nome;
        this.tipo = tipo;
=======
public class Player {
    private String nome;
    private int ataque;
    private int defesa;

    public Player(String nome, int ataque, int defesa) {
        this.nome = nome;
        this.ataque = ataque;
        this.defesa = defesa;
>>>>>>> c1831ec492da7756f442e1ee69c2d4a3d885baca
    }

    public String getNome() {
        return nome;
    }

<<<<<<< HEAD
    public int getTipo() {
        return tipo;
    }

=======
>>>>>>> c1831ec492da7756f442e1ee69c2d4a3d885baca
    public void setNome(String nome) {
        this.nome = nome;
    }

<<<<<<< HEAD
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
=======
    public int getAtaque() {
        return ataque;
    }

    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    public int getDefesa() {
        return defesa;
    }

    public void setDefesa(int defesa) {
        this.defesa = defesa;
    }
}
>>>>>>> c1831ec492da7756f442e1ee69c2d4a3d885baca
