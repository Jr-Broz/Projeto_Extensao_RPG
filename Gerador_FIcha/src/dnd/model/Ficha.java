package dnd.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Ficha {
    private final String nome;
    private final TipoFicha tipo;
    private final Raca raca;
    private final Classe classe;
    private final int nivel;
    private final EnumMap<Atributo, Integer> atributosBase;
    private final List<String> detalhesRolagem;

    public Ficha(String nome, TipoFicha tipo, Raca raca, Classe classe, int nivel,
                 Map<Atributo, Integer> atributosBase, List<String> detalhesRolagem) {
        this.nome = nome;
        this.tipo = tipo;
        this.raca = raca;
        this.classe = classe;
        this.nivel = nivel;
        this.atributosBase = new EnumMap<>(atributosBase);
        this.detalhesRolagem = List.copyOf(detalhesRolagem);
    }

    public int valorBase(Atributo a) { return atributosBase.get(a); }

    public int bonusRacial(Atributo a) { return raca.bonus().getOrDefault(a, 0); }

    public int valorFinal(Atributo a) {
        int v = valorBase(a) + bonusRacial(a);
        return Math.max(1, Math.min(30, v));
    }

    public int modificador(Atributo a) { return Atributo.modificador(valorFinal(a)); }

    public int bonusProficiencia() { return 2 + (nivel - 1) / 4; }

    /** Nível 1 = dado de vida cheio; depois, a média fixa do livro por nível. */
    public int pontosDeVida() {
        int con = modificador(Atributo.CONSTITUICAO);
        int pv = Math.max(1, classe.dadoVida() + con);
        int media = classe.dadoVida() / 2 + 1;
        for (int n = 2; n <= nivel; n++) {
            pv += Math.max(1, media + con);
        }
        return pv;
    }

    /** CA sem armadura, adicionar armaduras dps */
    public int classeArmadura() {
        int ca = 10 + modificador(Atributo.DESTREZA);
        if (classe.defesaSemArmadura() != null) {
            ca += modificador(classe.defesaSemArmadura());
        }
        return ca;
    }

    public String nome() { return nome; }
    public TipoFicha tipo() { return tipo; }
    public Raca raca() { return raca; }
    public Classe classe() { return classe; }
    public int nivel() { return nivel; }
    public List<String> detalhesRolagem() { return detalhesRolagem; }
}
