package dnd.model;

public enum TipoFicha {
    ALIADO("Aliado"),
    INIMIGO("Inimigo");

    private final String rotulo;

    TipoFicha(String rotulo) { this.rotulo = rotulo; }

    @Override
    public String toString() { return rotulo; }
}
