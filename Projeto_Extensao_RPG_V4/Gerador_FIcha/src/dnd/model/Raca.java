package dnd.model;
import java.util.List;
import java.util.Map;


// A raça é chave do dicionário: ela carrega bônus de atributo, entao carrega os bonus de atributos, vntgs e desvntgs
public record Raca(
        String nome,
        Map<Atributo, Integer> bonus,
        String tamanho,
        double deslocamento, // em metros
        List<String> vantagens,
        List<String> desvantagens,
        boolean monstruosa
) {
    @Override
    public String toString() { return nome; }
}