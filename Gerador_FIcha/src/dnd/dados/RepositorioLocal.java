package dnd.dados;

import dnd.model.Atributo;
import dnd.model.Classe;
import dnd.model.Raca;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dnd.model.Atributo.*;

/**
 * Dicionário chave-valor em memória: o nome da raça/classe é a chave.
 * Baseado no SRD do 5e (e raças de monstro clássicas para inimigos).
 */
public class RepositorioLocal implements RepositorioDados {

    private static final String PORTE_PEQUENO =
            "Porte pequeno: desvantagem em ataques com armas pesadas";

    private final Map<String, Raca> racas = new LinkedHashMap<>();
    private final Map<String, Classe> classes = new LinkedHashMap<>();

    public RepositorioLocal() {
        carregarRacas();
        carregarClasses();
    }

    private void carregarRacas() {
        raca(new Raca("Humano",
                Map.of(FORCA, 1, DESTREZA, 1, CONSTITUICAO, 1, INTELIGENCIA, 1, SABEDORIA, 1, CARISMA, 1),
                "Médio", 9,
                List.of("Versátil: +1 em todos os atributos", "Idioma extra à escolha"),
                List.of(), false));
        raca(new Raca("Anão", Map.of(CONSTITUICAO, 2), "Médio", 7.5,
                List.of("Visão no escuro (18 m)", "Resiliência anã: vantagem em salvaguardas contra veneno",
                        "Resistência a dano de veneno", "Não perde deslocamento com armadura pesada"),
                List.of("Deslocamento reduzido"), false));
        raca(new Raca("Elfo", Map.of(DESTREZA, 2), "Médio", 9,
                List.of("Visão no escuro (18 m)", "Ancestral feérico: vantagem contra ser enfeitiçado; magia não o faz dormir",
                        "Transe: descansa 4 horas", "Proficiência em Percepção"),
                List.of(), false));
        raca(new Raca("Halfling", Map.of(DESTREZA, 2), "Pequeno", 7.5,
                List.of("Sortudo: rola de novo um 1 natural", "Bravura: vantagem contra ficar amedrontado",
                        "Agilidade halfling: atravessa o espaço de criaturas maiores"),
                List.of(PORTE_PEQUENO, "Deslocamento reduzido"), false));
        raca(new Raca("Draconato", Map.of(FORCA, 2, CARISMA, 1), "Médio", 9,
                List.of("Arma de sopro (dano elemental em área)", "Resistência ao dano do seu ancestral dracônico"),
                List.of("Sem visão no escuro"), false));
        raca(new Raca("Gnomo", Map.of(INTELIGENCIA, 2), "Pequeno", 7.5,
                List.of("Visão no escuro (18 m)", "Esperteza gnômica: vantagem em salvaguardas de INT, SAB e CAR contra magia"),
                List.of(PORTE_PEQUENO, "Deslocamento reduzido"), false));
        raca(new Raca("Meio-Elfo", Map.of(CARISMA, 2, DESTREZA, 1, SABEDORIA, 1), "Médio", 9,
                List.of("Visão no escuro (18 m)", "Ancestral feérico: vantagem contra ser enfeitiçado",
                        "Proficiência em duas perícias"),
                List.of(), false));
        raca(new Raca("Meio-Orc", Map.of(FORCA, 2, CONSTITUICAO, 1), "Médio", 9,
                List.of("Visão no escuro (18 m)", "Resistência implacável: fica com 1 PV em vez de cair (1x por descanso longo)",
                        "Ataques selvagens: dado extra em acertos críticos"),
                List.of("Desconfiança social em muitas cidades (narrativo)"), false));
        raca(new Raca("Tiefling", Map.of(CARISMA, 2, INTELIGENCIA, 1), "Médio", 9,
                List.of("Visão no escuro (18 m)", "Resistência a dano de fogo", "Legado infernal: magias inatas"),
                List.of("Preconceito por aparência infernal (narrativo)"), false));

        // Raças de monstro: ótimas para inimigos
        raca(new Raca("Goblin", Map.of(DESTREZA, 2, CONSTITUICAO, 1), "Pequeno", 9,
                List.of("Visão no escuro (18 m)", "Fúria dos pequenos: dano extra contra criaturas maiores",
                        "Fuga ágil: Desengajar ou Esconder como ação bônus"),
                List.of(PORTE_PEQUENO), true));
        raca(new Raca("Orc", Map.of(FORCA, 2, CONSTITUICAO, 1, INTELIGENCIA, -2), "Médio", 9,
                List.of("Visão no escuro (18 m)", "Agressivo: move-se até o inimigo como ação bônus",
                        "Constituição poderosa: conta como tamanho maior para carga"),
                List.of("-2 em Inteligência"), true));
        raca(new Raca("Kobold", Map.of(DESTREZA, 2, FORCA, -2), "Pequeno", 9,
                List.of("Visão no escuro (18 m)", "Táticas de matilha: vantagem se um aliado estiver perto do alvo"),
                List.of("Sensibilidade à luz solar: desvantagem em ataques e Percepção sob o sol",
                        "-2 em Força", PORTE_PEQUENO), true));
        raca(new Raca("Hobgoblin", Map.of(CONSTITUICAO, 2, INTELIGENCIA, 1), "Médio", 9,
                List.of("Visão no escuro (18 m)", "Treino marcial: proficiência em armadura leve e duas armas",
                        "Salvar as aparências: bônus em teste que falhou, conforme aliados por perto"),
                List.of(), true));
    }

    private void carregarClasses() {
        classe(new Classe("Bárbaro", 12, FORCA, List.of(FORCA, CONSTITUICAO), CONSTITUICAO));
        classe(new Classe("Bardo", 8, CARISMA, List.of(DESTREZA, CARISMA), null));
        classe(new Classe("Bruxo", 8, CARISMA, List.of(SABEDORIA, CARISMA), null));
        classe(new Classe("Clérigo", 8, SABEDORIA, List.of(SABEDORIA, CARISMA), null));
        classe(new Classe("Druida", 8, SABEDORIA, List.of(INTELIGENCIA, SABEDORIA), null));
        classe(new Classe("Feiticeiro", 6, CARISMA, List.of(CONSTITUICAO, CARISMA), null));
        classe(new Classe("Guerreiro", 10, FORCA, List.of(FORCA, CONSTITUICAO), null));
        classe(new Classe("Ladino", 8, DESTREZA, List.of(DESTREZA, INTELIGENCIA), null));
        classe(new Classe("Mago", 6, INTELIGENCIA, List.of(INTELIGENCIA, SABEDORIA), null));
        classe(new Classe("Monge", 8, DESTREZA, List.of(FORCA, DESTREZA), SABEDORIA));
        classe(new Classe("Paladino", 10, FORCA, List.of(SABEDORIA, CARISMA), null));
        classe(new Classe("Patrulheiro", 10, DESTREZA, List.of(FORCA, DESTREZA), null));
    }

    private void raca(Raca r) { racas.put(r.nome(), r); }
    private void classe(Classe c) { classes.put(c.nome(), c); }

    @Override public List<Raca> listarRacas() { return new ArrayList<>(racas.values()); }
    @Override public List<Classe> listarClasses() { return new ArrayList<>(classes.values()); }
    @Override public Optional<Raca> buscarRaca(String nome) { return Optional.ofNullable(racas.get(nome)); }
    @Override public Optional<Classe> buscarClasse(String nome) { return Optional.ofNullable(classes.get(nome)); }
}