package dnd.ui;

/** Item de combo com rótulo; valor null significa "sortear". */
public record Opcao<T>(String rotulo, T valor) {
    @Override
    public String toString() { return rotulo; }
}
