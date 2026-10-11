package dnd.dados;
import dnd.model.Classe;
import dnd.model.Raca;
import java.util.List;
import java.util.Optional;


public interface RepositorioDados {
    List<Raca> listarRacas();
    List<Classe> listarClasses();
    Optional<Raca> buscarRaca(String nome);
    Optional<Classe> buscarClasse(String nome);
}
