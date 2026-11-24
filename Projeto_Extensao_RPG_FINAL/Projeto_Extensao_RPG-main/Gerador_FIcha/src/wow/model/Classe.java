package wow.model;

import java.util.List;

// armadura = o tipo q a classe usa no jogo
// atributoDeDefesa = atributo extra q entra na CA alem da destreza. so o
//   monge (sabedoria) e o druida (constituiçao, forma de urso) tem. nas
//   outras vai null
public record Classe(
        String nome,
        int dadoVida,
        Atributo atributoPrincipal,
        List<Atributo> salvaguardas,
        Armadura armadura,
        Atributo atributoDeDefesa
) {
    @Override
    public String toString() { return nome; }
}
