package dnd.service;

import dnd.model.Raca;

/**
 * Monta o nome da personagem juntando sílabas da tabela do povo dela.
 *
 * O nome é começo + meio (nem sempre) + fim, e na maioria das vezes ainda
 * ganha um sobrenome. Como cada parte é sorteada separada, um anão nunca sai
 * com nome de elfo.
 */
public final class GeradorNomes {

    /** De cada 100 nomes, quantos ganham uma sílaba no meio. */
    private static final int CHANCE_DE_MEIO = 45;

    /** De cada 100 nomes, quantos ganham sobrenome. */
    private static final int CHANCE_DE_SOBRENOME = 65;

    private GeradorNomes() {}

    public static String gerar(Raca raca) {
        TabelaNomes tabela = TabelaNomes.daRaca(raca == null ? null : raca.nome());

        String nome = Dado.escolher(tabela.comecos());
        if (sorteou(CHANCE_DE_MEIO)) {
            nome = nome + Dado.escolher(tabela.meios());
        }
        nome = nome + Dado.escolher(tabela.fins());

        if (sorteou(CHANCE_DE_SOBRENOME)) {
            nome = nome + " " + Dado.escolher(tabela.sobrenomes());
        }
        return nome;
    }

    /** Verdadeiro em "porcentagem" de cada 100 vezes. */
    private static boolean sorteou(int porcentagem) {
        return Dado.rolar(100) <= porcentagem;
    }
}
