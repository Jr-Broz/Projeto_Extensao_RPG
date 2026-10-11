package dnd.service;

import dnd.dados.RepositorioDados;
import dnd.model.Classe;
import dnd.model.Ficha;
import dnd.model.Raca;
import dnd.model.TipoFicha;

public class GeradorFicha {

    /** raca e classe podem ser null */
    public record Parametros(
            String nome,
            Raca raca,
            Classe classe,
            int nivel,
            TipoFicha tipo,
            GeradorAtributos.Metodo metodo,
            boolean priorizarClasse
    ) {}

    private final RepositorioDados repositorio;
    private final GeradorAtributos geradorAtributos = new GeradorAtributos();

    public GeradorFicha(RepositorioDados repositorio) {
        this.repositorio = repositorio;
    }

    public Ficha gerar(Parametros p) {
        Raca raca = p.raca() != null ? p.raca() : Dado.escolher(repositorio.listarRacas());
        Classe classe = p.classe() != null ? p.classe() : Dado.escolher(repositorio.listarClasses());
        String nome = (p.nome() == null || p.nome().isBlank()) ? GeradorNomes.gerar(raca) : p.nome().trim();

        GeradorAtributos.Resultado atributos =
                geradorAtributos.gerar(p.metodo(), classe, p.priorizarClasse());

        return new Ficha(nome, p.tipo(), raca, classe, p.nivel(),
                atributos.valores(), atributos.detalhes());
    }
}
