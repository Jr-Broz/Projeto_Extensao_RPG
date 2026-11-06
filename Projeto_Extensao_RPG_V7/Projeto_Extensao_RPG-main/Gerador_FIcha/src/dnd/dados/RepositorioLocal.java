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
 *
 * As raças e as classes são as do World of Warcraft. As habilidades raciais
 * foram conferidas uma a uma na lista de habilidades raciais do jogo, e não
 * escritas de memória, porque a primeira versão deste arquivo trazia efeito
 * errado em nove raças.
 *
 * Três coisas da ficha não existem no WoW e precisaram de adaptação:
 *
 * 1. Bônus de atributo. O jogo não dá mais bônus de atributo por raça, então
 *    cada bônus aqui foi tirado de uma habilidade racial de verdade: o Tauren
 *    ganha Constituição porque tem Resistência (+Vigor), o Draenei ganha
 *    Força, Destreza e Inteligência porque é isso que a Presença Heroica dá,
 *    e o Humano ganha +1 em tudo porque o Espírito Humano aumenta todos os
 *    atributos secundários.
 *
 * 2. Deslocamento. No WoW todas as raças correm na mesma velocidade; o que
 *    muda são as habilidades. Por isso todas ficam com 9 m, e quem corre mais
 *    aparece na lista de vantagens (Rapidez, Voo Sombrio, Salto Foguete).
 *
 * 3. Desvantagens. O jogo não tem desvantagem racial. O campo foi preenchido
 *    com as limitações reais das próprias habilidades, como o Canibalizar que
 *    é cancelado ao se mover, e fica vazio em quem não tem nenhuma.
 */
public class RepositorioLocal implements RepositorioDados {

    /** No WoW toda raça corre na mesma velocidade; as diferenças são raciais. */
    private static final double DESLOCAMENTO_PADRAO = 9;

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
                "Médio", DESLOCAMENTO_PADRAO,
                List.of("Espírito Humano: aumenta todos os atributos secundários",
                        "Vontade de Sobreviver: remove qualquer efeito de atordoamento",
                        "Não Há Lugar Como o Lar: a pedra de regresso recarrega mais rápido e ganha um uso extra"),
                List.of(), false));

        raca(new Raca("Anão", Map.of(CONSTITUICAO, 2, FORCA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Forma de Pedra: remove veneno, doença, maldição e sangramento, e reduz o dano físico por 8 segundos",
                        "Resistência ao Gelo: reduz o dano de gelo sofrido",
                        "Poder da Montanha: aumenta o dano dos acertos críticos",
                        "Explorador: escava mais rápido e acha mais fragmentos em sítios arqueológicos"),
                List.of(), false));

        raca(new Raca("Elfo Noturno", Map.of(DESTREZA, 2, SABEDORIA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Dissimulação: fica difícil de ser notado até se mover ou cancelar",
                        "Rapidez: aumenta a esquiva e a velocidade de movimento",
                        "Toque de Elune: mais rápido à noite, mais preciso de dia",
                        "Espírito Vagalume: ao morrer, vira um espírito bem mais veloz"),
                List.of("Ao cancelar a Dissimulação em combate, toda a ameaça volta de uma vez"), false));

        raca(new Raca("Gnomo", Map.of(INTELIGENCIA, 2, DESTREZA, 1), "Pequeno", DESLOCAMENTO_PADRAO,
                List.of("Fuga Artística: escapa de efeitos que prendem ou reduzem a velocidade",
                        "Mente Expansiva: aumenta a reserva máxima de mana, energia ou fúria",
                        "Dedos Ágeis: aumenta a velocidade de conjuração e de ataque",
                        "Especialização em Engenharia: vantagem em testes de engenharia"),
                List.of(), false));

        raca(new Raca("Draenei", Map.of(FORCA, 1, DESTREZA, 1, INTELIGENCIA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Presença Heroica: aumenta Força, Destreza e Inteligência",
                        "Dádiva dos Naaru: cura o alvo aos poucos ao longo de 5 segundos",
                        "Resistência às Sombras: reduz o dano necrótico sofrido",
                        "Lapidação: vantagem em testes de joalheria"),
                List.of(), false));

        raca(new Raca("Worgen", Map.of(DESTREZA, 2, FORCA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Voo Sombrio: aumenta muito a velocidade por 10 segundos",
                        "Ferocidade: aumenta a chance de acerto crítico",
                        "Duas Formas: alterna entre a forma humana e a de worgen",
                        "Correr Livremente: corre em quatro patas, na velocidade de um animal"),
                List.of("O Voo Sombrio só funciona na forma de worgen"), false));

        raca(new Raca("Pandaren", Map.of(CONSTITUICAO, 2, SABEDORIA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Palma Trêmula: incapacita o alvo por 4 segundos",
                        "Saltitante: sofre metade do dano de queda",
                        "Epicurista: dobra os atributos ganhos com comida",
                        "Paz Interior: aprende em dobro com o descanso"),
                List.of(), false));

        raca(new Raca("Dracthyr", Map.of(INTELIGENCIA, 2, SABEDORIA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Alçar Voo: lança-se ao ar e voa por conta própria",
                        "Planar: reduz a velocidade de queda",
                        "Golpe de Asa: empurra para trás os inimigos à sua frente",
                        "Semblante: assume uma forma humanoide, que acelera a recuperação do grupo fora de combate"),
                List.of(), false));
    }

    // ---------- Horda ----------

    private void carregarRacasDaHorda() {
        raca(new Raca("Orc", Map.of(FORCA, 2, CONSTITUICAO, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Fúria Sanguínea: aumenta o poder de ataque por 15 segundos",
                        "Robustez: reduz a duração dos atordoamentos sofridos",
                        "Comando: companheiros e invocações causam mais dano"),
                List.of(), true));

        raca(new Raca("Morto-vivo", Map.of(CONSTITUICAO, 2, INTELIGENCIA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Vontade dos Renegados: remove efeitos de medo, sono e encantamento",
                        "Canibalizar: devora um cadáver próximo e recupera vida aos poucos",
                        "Toque do Túmulo: os ataques podem drenar vida com dano necrótico",
                        "Resistência às Sombras: reduz o dano necrótico sofrido"),
                List.of("O Canibalizar é cancelado se você se mover, agir ou sofrer dano"), true));

        raca(new Raca("Tauren", Map.of(CONSTITUICAO, 2, FORCA, 1), "Grande", DESLOCAMENTO_PADRAO,
                List.of("Pisada de Guerra: atordoa até cinco inimigos ao redor por 2 segundos",
                        "Resistência: aumenta os pontos de vida máximos",
                        "Força Bruta: aumenta o dano dos acertos críticos",
                        "Cultivo: vantagem em testes de herbalismo"),
                List.of(), true));

        raca(new Raca("Troll", Map.of(DESTREZA, 2, CONSTITUICAO, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Insanidade: ataca e conjura mais rápido por 12 segundos",
                        "Regeneração: recupera vida mais rápido, inclusive em combate",
                        "Dança do Vodu: reduz a duração dos efeitos que prendem",
                        "Matador de Feras: aprende mais rápido lutando contra bestas"),
                List.of(), true));

        raca(new Raca("Elfo Sangrento", Map.of(INTELIGENCIA, 2, CARISMA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Torrente Arcana: rouba um efeito benéfico dos inimigos ao redor e recupera recurso",
                        "Acuidade Arcana: aumenta a chance de acerto crítico",
                        "Afinidade Arcana: vantagem em testes de encantamento",
                        "Resistência Arcana: reduz o dano arcano sofrido"),
                List.of(), false));

        raca(new Raca("Goblin", Map.of(INTELIGENCIA, 2, DESTREZA, 1), "Pequeno", DESLOCAMENTO_PADRAO,
                List.of("Salto Foguete: dispara o cinto-foguete e avança uma grande distância",
                        "Barragem de Foguetes: dispara os foguetes do cinto e causa dano de fogo",
                        "Tempo é Dinheiro: age e ataca mais rápido",
                        "Melhores Ofertas: compra sempre pelo menor preço, em qualquer facção"),
                List.of("Depois do Salto Foguete, você não consegue frear a queda por 10 segundos"), true));
    }

    // ---------- Raças aliadas ----------

    private void carregarRacasAliadas() {
        raca(new Raca("Anão Ferro Negro", Map.of(CONSTITUICAO, 2, FORCA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Sangue de Fogo: queima os efeitos negativos e aumenta um atributo por 8 segundos",
                        "Forjado nas Chamas: reduz o dano físico sofrido",
                        "Explorador de Masmorras: anda mais rápido dentro de masmorras e cavernas",
                        "Produção em Massa: forja mais rápido e com vantagem"),
                List.of(), false));

        raca(new Raca("Elfo do Vazio", Map.of(INTELIGENCIA, 2, CARISMA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Fenda Espacial: abre uma fenda e se teleporta até ela depois",
                        "Abraço Entrópico: de vez em quando o dano e a cura aumentam por 12 segundos",
                        "Calma Sobrenatural: sofrer dano não atrapalha mais a conjuração",
                        "Frio da Noite: reduz o dano necrótico sofrido"),
                List.of(), false));

        raca(new Raca("Orc Mag'har", Map.of(FORCA, 2, SABEDORIA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Chamado Ancestral: os antepassados reforçam seu melhor atributo por 15 segundos",
                        "Sangue Selvagem: reduz a duração de venenos, doenças e maldições",
                        "Vigor Compartilhado: companheiros e invocações ganham mais vida",
                        "Céu Aberto: monta e cavalga mais rápido"),
                List.of(), true));

        raca(new Raca("Troll Zandalari", Map.of(CONSTITUICAO, 2, SABEDORIA, 1), "Médio", DESLOCAMENTO_PADRAO,
                List.of("Regenerano': recupera metade da vida máxima em 6 segundos",
                        "Abraço dos Loas: escolhe um loa num santuário e ganha a bênção dele",
                        "Rasante do Pterrordax: plana e cai devagar por 2 minutos",
                        "Cidade de Ouro: recebe mais ouro das criaturas derrotadas"),
                List.of("O Regenerano' é interrompido por qualquer dano direto"), true));

        raca(new Raca("Tauren Altomontês", Map.of(CONSTITUICAO, 2, FORCA, 1), "Grande", DESLOCAMENTO_PADRAO,
                List.of("Investida Taurina: avança por 1 segundo derrubando todos no caminho",
                        "Tenacidade Rústica: reduz o dano sofrido",
                        "Montanhês: aumenta a versatilidade em ataque, defesa e cura",
                        "Orgulho de Chifredaço: vantagem em testes de mineração"),
                List.of(), true));

        raca(new Raca("Vulpera", Map.of(DESTREZA, 2, CARISMA, 1), "Pequeno", DESLOCAMENTO_PADRAO,
                List.of("Montar Acampamento: arma um acampamento completo ao ar livre e volta para ele depois",
                        "Saco de Truques: escolhe um truque que fere e atrasa o inimigo ou cura e acelera o aliado",
                        "Faro para Encrenca: reduz o primeiro golpe que cada inimigo acerta em você",
                        "Alforjes de Alpaca: carrega bem mais coisa que as outras raças"),
                List.of(), true));
    }

    /**
     * As treze classes do World of Warcraft.
     *
     * O dado de vida segue a resistência da classe no jogo: quem usa placas
     * tem d12 ou d10, quem usa couro ou malha tem d8 e os conjuradores de
     * tecido têm d6. O último parâmetro só é usado por quem se defende sem
     * armadura pesada: o Monge com Sabedoria e o Druida com Constituição,
     * por causa da forma de urso.
     */
    private void carregarClasses() {
        classe(new Classe("Guerreiro", 12, FORCA, List.of(FORCA, CONSTITUICAO), null));
        classe(new Classe("Cavaleiro da Morte", 12, FORCA, List.of(FORCA, CONSTITUICAO), null));
        classe(new Classe("Paladino", 10, FORCA, List.of(SABEDORIA, CARISMA), null));
        classe(new Classe("Caçador", 10, DESTREZA, List.of(DESTREZA, SABEDORIA), null));
        classe(new Classe("Caçador de Demônios", 10, DESTREZA, List.of(DESTREZA, CARISMA), null));
        classe(new Classe("Ladino", 8, DESTREZA, List.of(DESTREZA, INTELIGENCIA), null));
        classe(new Classe("Monge", 8, DESTREZA, List.of(FORCA, DESTREZA), SABEDORIA));
        classe(new Classe("Druida", 8, SABEDORIA, List.of(INTELIGENCIA, SABEDORIA), CONSTITUICAO));
        classe(new Classe("Xamã", 8, SABEDORIA, List.of(CONSTITUICAO, SABEDORIA), null));
        classe(new Classe("Conjurante", 8, INTELIGENCIA, List.of(INTELIGENCIA, CARISMA), null));
        classe(new Classe("Sacerdote", 6, SABEDORIA, List.of(SABEDORIA, CARISMA), null));
        classe(new Classe("Mago", 6, INTELIGENCIA, List.of(INTELIGENCIA, SABEDORIA), null));
        classe(new Classe("Bruxo", 6, INTELIGENCIA, List.of(CONSTITUICAO, INTELIGENCIA), null));
    }

    private void raca(Raca r) { racas.put(r.nome(), r); }
    private void classe(Classe c) { classes.put(c.nome(), c); }

    @Override public List<Raca> listarRacas() { return new ArrayList<>(racas.values()); }
    @Override public List<Classe> listarClasses() { return new ArrayList<>(classes.values()); }
    @Override public Optional<Raca> buscarRaca(String nome) { return Optional.ofNullable(racas.get(nome)); }
    @Override public Optional<Classe> buscarClasse(String nome) { return Optional.ofNullable(classes.get(nome)); }
}
