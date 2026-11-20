package dnd.service;

import java.util.List;
import java.util.Random;

public final class Dado {
    private static final Random RNG = new Random();

    private Dado() {}

    public static int rolar(int faces) { return RNG.nextInt(faces) + 1; }

    public static int[] rolar(int quantidade, int faces) {
        int[] resultado = new int[quantidade];
        for (int i = 0; i < quantidade; i++) resultado[i] = rolar(faces);
        return resultado;
    }

    public static <T> T escolher(List<T> lista) { return lista.get(RNG.nextInt(lista.size())); }

    public static <T> T escolher(T[] vetor) { return vetor[RNG.nextInt(vetor.length)]; }

    public static Random rng() { return RNG; }
}
