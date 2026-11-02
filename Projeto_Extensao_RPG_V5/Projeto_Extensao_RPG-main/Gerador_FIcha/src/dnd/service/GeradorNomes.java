package dnd.service;

import dnd.model.Raca;

public final class GeradorNomes {
    private static final String[] INICIO = {"Ar", "Bel", "Cae", "Dor", "El", "Fen", "Gar", "Hal",
            "Ira", "Kel", "Lor", "Mir", "Nor", "Syl", "Tor", "Val", "Ys", "Bre"};
    private static final String[] FIM = {"an", "wen", "dor", "ric", "iel", "wyn", "mar", "thas",
            "ra", "lis", "gar", "eth", "ion", "ara"};
    private static final String[] INICIO_MONSTRO = {"Gru", "Snik", "Krag", "Zug", "Mok", "Grim",
            "Rax", "Skar", "Bol", "Nag", "Vrek", "Uth"};
    private static final String[] FIM_MONSTRO = {"nak", "gash", "zik", "tuk", "rok", "mug",
            "lob", "grak", "ik", "ush", "az"};

    private GeradorNomes() {}

    public static String gerar(Raca raca) {
        if (raca != null && raca.monstruosa()) {
            return Dado.escolher(INICIO_MONSTRO) + Dado.escolher(FIM_MONSTRO);
        }
        return Dado.escolher(INICIO) + Dado.escolher(FIM);
    }
}
