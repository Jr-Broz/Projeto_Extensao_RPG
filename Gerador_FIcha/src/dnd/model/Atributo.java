package dnd.model;

public enum Atributo {
    FORCA("FOR", "Força"),
    DESTREZA("DES", "Destreza"),
    CONSTITUICAO("CON", "Constituição"),
    INTELIGENCIA("INT", "Inteligência"),
    SABEDORIA("SAB", "Sabedoria"),
    CARISMA("CAR", "Carisma");
    
    private final String sigla;
    private final String nome;

    Atributo(String sigla, String nome) {
        this.sigla = sigla;
        this.nome = nome;
    }

    public String sigla() { return sigla; }
    public String nome() { return nome; }

    /** Regra do 5e: (valor - 10) / 2, faz arredondamento p baixo */
    public static int modificador(int valor) {
        return Math.floorDiv(valor - 10, 2);
    }
}
