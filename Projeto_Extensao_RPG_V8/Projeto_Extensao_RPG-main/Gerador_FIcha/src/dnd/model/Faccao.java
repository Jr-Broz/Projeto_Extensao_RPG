package dnd.model;

// As 2 facçoes do WoW + o neutro.
// No jogo a raça ja nasce com um lado, tipo anao e sempre alianca e orc sempre horda.
// So o pandaren e o dracthyr q comecam neutro e escolhem dps.
public enum Faccao {
    ALIANCA("Aliança"),
    HORDA("Horda"),
    NEUTRO("Neutro");

    private final String rotulo;

    Faccao(String rotulo) {
        this.rotulo = rotulo;
    }

    // true so p quem ainda pode escolher o lado
    public boolean escolheOLado() {
        return this == NEUTRO;
    }

    @Override
    public String toString() { return rotulo; }
}
