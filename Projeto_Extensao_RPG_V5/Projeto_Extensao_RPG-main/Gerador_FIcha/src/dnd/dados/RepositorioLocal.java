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
 * As raças agora são as do World of Warcraft, com as habilidades raciais
 * do jogo adaptadas para o formato de vantagem/desvantagem da ficha.
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
        carregarRacasDaAlianca();
        carregarRacasDaHorda();
        carregarRacasAliadas();
    }

    // ---------- Aliança ----------

    private void carregarRacasDaAlianca() {
        raca(new Raca("Humano",
                Map.of(FORCA, 1, DESTREZA, 1, CONSTITUICAO, 1, INTELIGENCIA, 1, SABEDORIA, 1, CARISMA, 1),
                "Médio", 9,
                List.of("Espírito Humano: +1 em todos os atributos",
                        "Vontade de Sobreviver: sai de um efeito de atordoamento",
                        "Diplomacia: vantagem em testes de negociação"),
                List.of(), false));

        raca(new Raca("Anão", Map.of(CONSTITUICAO, 2, FORCA, 1), "Médio", 7.5,
                List.of("Forma de Pedra: remove veneno, doença e sangramento, e reduz o dano sofrido",
                        "Resistência ao Gelo: resistência a dano de frio",
                        "Força da Montanha: dano extra em acertos críticos",
                        "Explorador: acha tesouro e sítio arqueológico mais rápido"),
                List.of("Deslocamento reduzido"), false));

        raca(new Raca("Elfo Noturno", Map.of(DESTREZA, 2, SABEDORIA, 1), "Médio", 9,
                List.of("Dissimulação: fica escondido ao ficar parado nas sombras",
                        "Rapidez: inimigos têm desvantagem para acertá-lo corpo a corpo",
                        "Resistência à Natureza: resistência a dano de natureza",
                        "Toque de Elune: mais rápido à noite, mais preciso de dia"),
                List.of(), false));

        raca(new Raca("Gnomo", Map.of(INTELIGENCIA, 2, DESTREZA, 1), "Pequeno", 7.5,
                List.of("Fuga Artística: escapa de efeitos que prendem ou reduzem movimento",
                        "Mente Expansiva: reserva de mana e energia maior",
                        "Especialização em Engenharia: vantagem em testes com engenhocas",
                        "Resistência Arcana: resistência a dano arcano"),
                List.of(PORTE_PEQUENO, "Deslocamento reduzido"), false));

        raca(new Raca("Draenei", Map.of(CARISMA, 2, CONSTITUICAO, 1), "Médio", 9,
                List.of("Dádiva dos Naaru: cura um aliado ao longo de alguns turnos",
                        "Presença Heroica: aliados por perto ficam mais precisos",
                        "Lapidação: vantagem em testes de joalheria",
                        "Resistência às Sombras: resistência a dano necrótico"),
                List.of(), false));

        raca(new Raca("Worgen", Map.of(FORCA, 2, DESTREZA, 1), "Médio", 10.5,
                List.of("Voo Sombrio: dobra o deslocamento por um turno",
                        "Ferocidade: dano extra em acertos críticos",
                        "Duas Formas: alterna entre a forma humana e a de lobo",
                        "Correr Livremente: corre em quatro patas quando está sozinho"),
                List.of("A forma de lobo assusta aldeões e fecha portas de cidade"), true));

        raca(new Raca("Pandaren", Map.of(CONSTITUICAO, 2, SABEDORIA, 1), "Médio", 9,
                List.of("Palma Trêmula: atordoa um alvo por um turno",
                        "Epicurista: dobra o efeito de comida e bebida",
                        "Saltitante: metade do dano de queda",
                        "Paz Interior: aprende mais rápido com o descanso"),
                List.of(), false));

        raca(new Raca("Dracthyr", Map.of(CARISMA, 2, INTELIGENCIA, 1), "Médio", 9,
                List.of("Planar: desce de grandes alturas sem sofrer dano",
                        "Golpe de Asa: empurra os inimigos à sua frente",
                        "Semblante: assume uma forma humanoide para passar despercebido",
                        "Olhar Perspicaz: vantagem em testes de Percepção"),
                List.of("A forma dracônica chama atenção em qualquer vilarejo"), false));
    }

    // ---------- Horda ----------

    private void carregarRacasDaHorda() {
        raca(new Raca("Orc", Map.of(FORCA, 2, CONSTITUICAO, 1), "Médio", 9,
                List.of("Fúria Sanguínea: aumenta o dano por alguns turnos",
                        "Robustez: reduz pela metade a duração de atordoamentos",
                        "Comando: companheiros e invocações causam mais dano"),
                List.of("A Fúria Sanguínea cobra o preço: cura recebida reduzida enquanto dura"), true));

        raca(new Raca("Morto-vivo", Map.of(CONSTITUICAO, 2, INTELIGENCIA, 1), "Médio", 9,
                List.of("Vontade dos Renegados: imune a medo, sono e encantamento",
                        "Canibalizar: recupera vida devorando um cadáver próximo",
                        "Toque do Túmulo: causa dano necrótico e cura o mesmo tanto",
                        "Resistência às Sombras: resistência a dano necrótico"),
                List.of("Aparência repulsiva: desvantagem em interações sociais com os vivos"), true));

        raca(new Raca("Tauren", Map.of(FORCA, 2, CONSTITUICAO, 1), "Grande", 9,
                List.of("Pisada de Guerra: atordoa todos os inimigos ao redor",
                        "Resistência: pontos de vida máximos aumentados",
                        "Força Bruta: dano extra em acertos críticos",
                        "Cultivo: vantagem em testes de herbalismo"),
                List.of("Porte grande: não passa por passagens estreitas"), true));

        raca(new Raca("Troll", Map.of(DESTREZA, 2, CONSTITUICAO, 1), "Médio", 9,
                List.of("Insanidade: ataca mais rápido por alguns turnos",
                        "Regeneração: recupera vida mesmo durante o combate",
                        "Dança Vodu: reduz o tempo de efeitos que prendem",
                        "Matador de Feras: dano extra contra bestas"),
                List.of(), true));

        raca(new Raca("Elfo Sangrento", Map.of(INTELIGENCIA, 2, CARISMA, 1), "Médio", 9,
                List.of("Torrente Arcana: silencia os inimigos ao redor e recupera mana",
                        "Afinidade Arcana: vantagem em testes de encantamento",
                        "Acuidade Arcana: mais chance de acerto crítico",
                        "Resistência Arcana: resistência a dano arcano"),
                List.of("Sede arcana: precisa de magia por perto ou fica debilitado"), false));

        raca(new Raca("Goblin", Map.of(INTELIGENCIA, 2, DESTREZA, 1), "Pequeno", 7.5,
                List.of("Salto Foguete: avança uma grande distância em um turno",
                        "Barragem de Foguetes: dispara foguetes em linha reta",
                        "Tempo é Dinheiro: age mais rápido na ordem de iniciativa",
                        "Melhores Ofertas: sempre compra pelo menor preço"),
                List.of(PORTE_PEQUENO, "Ganância: não resiste a um bom negócio, mesmo arriscado"), true));
    }

    // ---------- Raças aliadas ----------

    private void carregarRacasAliadas() {
        raca(new Raca("Anão Ferro Negro", Map.of(CONSTITUICAO, 2, FORCA, 1), "Médio", 7.5,
                List.of("Sangue de Fogo: queima venenos e doenças e aumenta um atributo",
                        "Resistência ao Fogo: resistência a dano de fogo",
                        "Mãos de Ferreiro: vantagem em testes de forja"),
                List.of("Deslocamento reduzido"), false));

        raca(new Raca("Elfo do Vazio", Map.of(INTELIGENCIA, 2, CARISMA, 1), "Médio", 9,
                List.of("Fenda Espacial: abre um portal curto e se teleporta por ele",
                        "Entrelaçar o Vazio: recupera mana ao conjurar",
                        "Resistência às Sombras: resistência a dano necrótico"),
                List.of("Sussurros do Vazio: desvantagem contra efeitos de medo"), false));

        raca(new Raca("Orc Mag'har", Map.of(FORCA, 2, SABEDORIA, 1), "Médio", 9,
                List.of("Poder Ancestral: invoca a força dos antepassados por alguns turnos",
                        "Resistência Espiritual: resistência a dano psíquico",
                        "Robustez: reduz pela metade a duração de atordoamentos"),
                List.of(), true));

        raca(new Raca("Troll Zandalari", Map.of(CONSTITUICAO, 2, FORCA, 1), "Médio", 9,
                List.of("Bênção dos Loas: escolhe um loa e ganha a bênção dele",
                        "Pele de Pedra Régia: reduz o dano sofrido por alguns turnos",
                        "Pisada do Pterrordax: faz os inimigos ao redor fugirem"),
                List.of("Orgulho zandalari: nunca recua de um desafio direto"), true));

        raca(new Raca("Tauren Altomontês", Map.of(FORCA, 2, CONSTITUICAO, 1), "Grande", 9,
                List.of("Investida Bruta: avança derrubando quem estiver no caminho",
                        "Pele de Pedra: reduz o dano sofrido por alguns turnos",
                        "Resistência à Natureza: resistência a dano de natureza",
                        "Coração Montês: come e bebe mais rápido para se recuperar"),
                List.of("Porte grande: não passa por passagens estreitas"), true));

        raca(new Raca("Vulpera", Map.of(DESTREZA, 2, CARISMA, 1), "Pequeno", 7.5,
                List.of("Acampamento Portátil: monta um acampamento completo em qualquer lugar",
                        "Bolsa de Truques: joga um item surpresa no inimigo",
                        "Astuto e Engenhoso: vantagem em testes de Enganação",
                        "Resistência ao Calor: resistência a dano de fogo"),
                List.of(PORTE_PEQUENO, "Deslocamento reduzido"), true));
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
