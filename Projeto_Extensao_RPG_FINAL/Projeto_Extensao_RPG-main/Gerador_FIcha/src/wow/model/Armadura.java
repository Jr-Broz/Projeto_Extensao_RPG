package wow.model;

// Os 4 tipos de armadura do WoW. Cada classe usa um tipo só, igual no jogo:
// placas pro guerreiro, tecido pro mago e por aí vai.
//
// A base e o limite de destreza sao adaptaçao de rpg de mesa, pq no WoW
// armadura é nivel de item, nao existe classe de armadura.
public enum Armadura {

    // o 3o numero é o limite de destreza q a armadura deixa usar.
    // 99 = sem limite (armadura leve), 0 = destreza nao conta (placas)
    TECIDO("tecido", 11, 99),
    COURO("couro", 12, 99),
    MALHA("malha", 14, 2),
    PLACAS("placas", 18, 0);

    private final String nome;
    private final int base;
    private final int limiteDeDestreza;

    Armadura(String nome, int base, int limiteDeDestreza) {
        this.nome = nome;
        this.base = base;
        this.limiteDeDestreza = limiteDeDestreza;
    }

    // quanto de classe de armadura essa armadura da, ja contando a destreza
    public int defesa(int modificadorDeDestreza) {
        if (limiteDeDestreza == 0) {
            return base; // placas sao tao pesadas q destreza nem conta
        }
        return base + Math.min(modificadorDeDestreza, limiteDeDestreza);
    }

    public String nome() { return nome; }

    @Override
    public String toString() { return nome; }
}
