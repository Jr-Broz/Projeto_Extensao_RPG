package dnd.service;

import dnd.dados.ClassesPorRaca;
import dnd.dados.RepositorioDados;
import dnd.model.Classe;
import dnd.model.Ficha;
import dnd.model.Raca;
import dnd.model.Faccao;

public class GeradorFicha {

    /** raca e classe podem ser null */
    public record Parametros(
            String nome,
            Raca raca,
            Classe classe,
            int nivel,
            Faccao faccaoEscolhida,
            GeradorAtributos.Metodo metodo,
            boolean priorizarClasse
    ) {}

    private final RepositorioDados repositorio;
    private final GeradorAtributos geradorAtributos = new GeradorAtributos();

    public GeradorFicha(RepositorioDados repositorio) {
        this.repositorio = repositorio;
    }

    public Ficha gerar(Parametros p) {
        Raca raca = p.raca();
        Classe classe = p.classe();

        // o sorteio tem q respeitar as combinaçao do jogo. se marcar conjurante e
        // deixar a raça no sorteio so pode sair dracthyr, e se marcar goblin a
        // classe sorteada nunca pode sair paladino
        if (raca == null && classe == null) {
            raca = Dado.escolher(repositorio.listarRacas());
            classe = Dado.escolher(ClassesPorRaca.daRaca(raca, repositorio.listarClasses()));
        } else if (raca == null) {
            raca = Dado.escolher(ClassesPorRaca.daClasse(classe, repositorio.listarRacas()));
        } else if (classe == null) {
            classe = Dado.escolher(ClassesPorRaca.daRaca(raca, repositorio.listarClasses()));
        }

        String nome = (p.nome() == null || p.nome().isBlank()) ? GeradorNomes.gerar(raca) : p.nome().trim();

        GeradorAtributos.Resultado atributos =
                geradorAtributos.gerar(p.metodo(), classe, p.priorizarClasse());

        return new Ficha(nome, definirFaccao(raca, p.faccaoEscolhida()), raca, classe, p.nivel(),
                atributos.valores(), atributos.detalhes());
    }

    // quem manda na facçao e a raça: anao sempre alianca, orc sempre horda.
    // so a raça neutra q segue o q foi marcado na tela
    private Faccao definirFaccao(Raca raca, Faccao escolhida) {
        if (!raca.faccao().escolheOLado()) {
            return raca.faccao();
        }
        return escolhida == null ? Faccao.NEUTRO : escolhida;
    }
}
