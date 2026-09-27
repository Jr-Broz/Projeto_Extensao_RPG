package dnd.service;

import dnd.model.Atributo;
import dnd.model.Classe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

public class GeradorAtributos {

    public enum Metodo {
        ROLAGEM_4D6("4d6, descarta o menor"),
        ARRAY_PADRAO("Array padrão (15 a 8)");

        private final String descricao;
        Metodo(String descricao) { this.descricao = descricao; }
        @Override public String toString() { return descricao; }
    }

    public record Resultado(EnumMap<Atributo, Integer> valores, List<String> detalhes) {}

    private static final int[] ARRAY_PADRAO = {15, 14, 13, 12, 10, 8};

    public Resultado gerar(Metodo metodo, Classe classe, boolean priorizarClasse) {
        List<Integer> valores = new ArrayList<>();
        List<String> detalhes = new ArrayList<>();

        if (metodo == Metodo.ROLAGEM_4D6) {
            for (int i = 1; i <= 6; i++) {
                int[] d = Dado.rolar(4, 6);
                Arrays.sort(d); // d[0] é o menor
                int total = d[1] + d[2] + d[3];
                valores.add(total);
                detalhes.add(String.format("Rolagem %d: %d, %d, %d, %d  (descarta %d)  = %d",
                        i, d[3], d[2], d[1], d[0], d[0], total));
            }
        } else {
            for (int v : ARRAY_PADRAO) valores.add(v);
            detalhes.add("Array padrão: 15, 14, 13, 12, 10, 8");
        }

        return new Resultado(distribuir(valores, classe, priorizarClasse, detalhes), detalhes);
    }

    /**
     * Com prioridade: maior valor no atributo principal da classe,
     * segundo maior em Constituição, o resto sorteado.
     */
    private EnumMap<Atributo, Integer> distribuir(List<Integer> valores, Classe classe,
                                                  boolean priorizar, List<String> detalhes) {
        List<Atributo> ordem = new ArrayList<>(List.of(Atributo.values()));

        if (priorizar && classe != null) {
            valores.sort(Comparator.reverseOrder());
            Atributo principal = classe.atributoPrincipal();
            ordem.remove(principal);
            ordem.remove(Atributo.CONSTITUICAO);
            Collections.shuffle(ordem, Dado.rng());
            ordem.add(0, principal);
            if (principal != Atributo.CONSTITUICAO) ordem.add(1, Atributo.CONSTITUICAO);
            detalhes.add("Maior valor em " + principal.nome() + " (atributo principal de "
                    + classe.nome() + "), segundo em Constituição.");
        } else {
            Collections.shuffle(ordem, Dado.rng());
            detalhes.add("Valores distribuídos aleatoriamente.");
        }

        EnumMap<Atributo, Integer> mapa = new EnumMap<>(Atributo.class);
        for (int i = 0; i < ordem.size(); i++) mapa.put(ordem.get(i), valores.get(i));
        return mapa;
    }
}
