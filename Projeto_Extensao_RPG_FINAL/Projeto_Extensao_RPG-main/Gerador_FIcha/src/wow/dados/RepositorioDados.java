package wow.dados;
import wow.model.Classe;
import wow.model.Raca;
import java.util.List;
import java.util.Optional;

/**
 * Ponto único de "request" de raças e classes.
 * Hoje a implementação é local (dicionário em memória); amanhã pode ser
 * um JSON em disco ou uma API de dados do WoW sem mexer na interface gráfica.
 */

public interface RepositorioDados {
    List<Raca> listarRacas();
    List<Classe> listarClasses();
    Optional<Raca> buscarRaca(String nome);
    Optional<Classe> buscarClasse(String nome);
}
