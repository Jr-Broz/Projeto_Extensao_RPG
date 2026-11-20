package dnd.dados;

import dnd.model.Classe;
import dnd.model.Raca;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Quais classes cada raça pode ser, igual no WoW.
// A maioria das classe qualquer raça pode (guerreiro, caçador, mago, sacerdote,
// ladino e bruxo), entao so botei aqui as que tem restriçao, separado em 2 mapas:
// SOMENTE_ESTAS -> classe de pouca raça, tipo conjurante q so o dracthyr pode
// MENOS_ESTAS   -> classe q quase todo mundo pode, ai fica mais curto listar quem
//                  NAO pode, tipo o monge q so o dracthyr nao faz
public class ClassesPorRaca {

    private static final Map<String, List<String>> SOMENTE_ESTAS = new LinkedHashMap<>();
    private static final Map<String, List<String>> MENOS_ESTAS = new LinkedHashMap<>();

    static {
        SOMENTE_ESTAS.put("Paladino", List.of("Humano", "Anão", "Draenei", "Anão Ferro Negro",
                "Elfo Sangrento", "Tauren", "Troll Zandalari"));

        SOMENTE_ESTAS.put("Caçador de Demônios", List.of("Elfo Noturno", "Elfo Sangrento"));

        SOMENTE_ESTAS.put("Druida", List.of("Elfo Noturno", "Worgen", "Tauren", "Troll",
                "Tauren Altomontês", "Troll Zandalari"));

        SOMENTE_ESTAS.put("Xamã", List.of("Anão", "Draenei", "Anão Ferro Negro", "Pandaren",
                "Orc", "Tauren", "Troll", "Goblin", "Orc Mag'har", "Tauren Altomontês",
                "Troll Zandalari", "Vulpera"));

        SOMENTE_ESTAS.put("Conjurante", List.of("Dracthyr"));

        MENOS_ESTAS.put("Cavaleiro da Morte", List.of("Dracthyr"));
        MENOS_ESTAS.put("Monge", List.of("Dracthyr"));
    }

    private ClassesPorRaca() {}

    // ve se essa raça pode ser essa classe
    public static boolean permite(Raca raca, Classe classe) {
        List<String> somente = SOMENTE_ESTAS.get(classe.nome());
        if (somente != null) {
            return somente.contains(raca.nome());
        }
        List<String> menos = MENOS_ESTAS.get(classe.nome());
        return menos == null || !menos.contains(raca.nome());
    }

    // as classes q essa raça pode ser, na ordem do catalogo
    public static List<Classe> daRaca(Raca raca, List<Classe> todas) {
        List<Classe> permitidas = new ArrayList<>();
        for (Classe classe : todas) {
            if (permite(raca, classe)) {
                permitidas.add(classe);
            }
        }
        return permitidas;
    }

    // as raças q podem ser essa classe
    public static List<Raca> daClasse(Classe classe, List<Raca> todas) {
        List<Raca> permitidas = new ArrayList<>();
        for (Raca raca : todas) {
            if (permite(raca, classe)) {
                permitidas.add(raca);
            }
        }
        return permitidas;
    }
}
