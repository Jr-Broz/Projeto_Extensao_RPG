package dnd.model;

import java.util.List;

/**
 * @param defesaSemArmadura atributo extra somado na CA sem armadura
 *                          (Bárbaro usa CON, Monge usa SAB); null se não tiver.
 */
public record Classe(
        String nome,
        int dadoVida,
        Atributo atributoPrincipal,
        List<Atributo> salvaguardas,
        Atributo defesaSemArmadura
) {
    @Override
    public String toString() { return nome; }
}
