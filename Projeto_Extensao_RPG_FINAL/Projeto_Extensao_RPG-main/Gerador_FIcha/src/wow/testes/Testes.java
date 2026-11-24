package wow.testes;

import wow.dados.ClassesPorRaca;
import wow.dados.RepositorioArquivo;
import wow.dados.RepositorioDados;
import wow.dados.RepositorioLocal;
import wow.model.Armadura;
import wow.model.Atributo;
import wow.model.Classe;
import wow.model.Faccao;
import wow.model.Ficha;
import wow.model.Raca;
import wow.service.GeradorAtributos;
import wow.service.GeradorFicha;
import wow.service.GeradorNomes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Testes do projeto, rodados pelo ./testar.sh
//
// Nao usei JUnit de proposito: o projeto inteiro nao depende de biblioteca
// nenhuma, entao quem clonar compila e roda sem baixar nada. É so um main
// que confere as coisas e conta quantas falharam.
public class Testes {

    private static int passou = 0;
    private static int falhou = 0;

    public static void main(String[] args) throws IOException {
        RepositorioLocal repositorio = new RepositorioLocal();
        GeradorFicha gerador = new GeradorFicha(repositorio);

        System.out.println("== catalogo ==");
        catalogo(repositorio);

        System.out.println("\n== combinaçao de raça e classe ==");
        combinacoes(repositorio, gerador);

        System.out.println("\n== facçao ==");
        faccoes(repositorio, gerador);

        System.out.println("\n== armadura ==");
        armaduras(repositorio);

        System.out.println("\n== contas da ficha ==");
        contas(repositorio, gerador);

        System.out.println("\n== nomes ==");
        nomes(repositorio);

        System.out.println("\n== raças vindas de arquivo ==");
        repositorioDeArquivo();

        System.out.println("\n--------------------------------");
        System.out.println(passou + " passaram, " + falhou + " falharam");
        if (falhou > 0) {
            System.exit(1);
        }
    }

    // ---------- os testes ----------

    private static void catalogo(RepositorioLocal r) {
        confere("tem 20 raças", r.listarRacas().size() == 20);
        confere("tem 13 classes", r.listarClasses().size() == 13);

        boolean todasComVantagem = true;
        for (Raca raca : r.listarRacas()) {
            if (raca.vantagens().isEmpty()) {
                todasComVantagem = false;
            }
        }
        confere("toda raça tem pelo menos uma vantagem", todasComVantagem);
    }

    private static void combinacoes(RepositorioLocal r, GeradorFicha g) {
        // o sorteio nunca pode dar uma combinaçao q o jogo nao deixa
        boolean tudoValido = true;
        for (int i = 0; i < 3000; i++) {
            Ficha f = gerar(g, null, null);
            if (!ClassesPorRaca.permite(f.raca(), f.classe())) {
                tudoValido = false;
            }
        }
        confere("3000 sorteios livres so dao combinaçao valida", tudoValido);

        // com a raça presa, a classe sorteada tem q servir pra ela
        boolean racaPresaOk = true;
        for (Raca raca : r.listarRacas()) {
            for (int i = 0; i < 50; i++) {
                if (!ClassesPorRaca.permite(raca, gerar(g, raca, null).classe())) {
                    racaPresaOk = false;
                }
            }
        }
        confere("raça escolhida nunca ganha classe proibida", racaPresaOk);

        // com a classe presa, a raça sorteada tem q poder ser aquilo
        boolean classePresaOk = true;
        for (Classe classe : r.listarClasses()) {
            for (int i = 0; i < 50; i++) {
                if (!ClassesPorRaca.permite(gerar(g, null, classe).raca(), classe)) {
                    classePresaOk = false;
                }
            }
        }
        confere("classe escolhida nunca cai em raça proibida", classePresaOk);

        // os casos mais apertados do jogo
        confere("Conjurante so o Dracthyr", quantasRacas(r, "Conjurante") == 1);
        confere("Caçador de Demonios so 2 raças", quantasRacas(r, "Caçador de Demônios") == 2);
        confere("Druida 6 raças", quantasRacas(r, "Druida") == 6);
        confere("Paladino 7 raças", quantasRacas(r, "Paladino") == 7);
        confere("Xama 12 raças", quantasRacas(r, "Xamã") == 12);
        confere("Guerreiro qualquer raça", quantasRacas(r, "Guerreiro") == 20);

        Raca goblin = r.buscarRaca("Goblin").orElseThrow();
        Classe paladino = r.buscarClasse("Paladino").orElseThrow();
        confere("Goblin nao pode ser Paladino", !ClassesPorRaca.permite(goblin, paladino));
    }

    private static void faccoes(RepositorioLocal r, GeradorFicha g) {
        // a raça manda: mesmo forçando o lado contrario, a ficha sai com o
        // lado da raça
        boolean racaManda = true;
        for (Raca raca : r.listarRacas()) {
            for (Faccao forcada : List.of(Faccao.ALIANCA, Faccao.HORDA)) {
                Ficha f = g.gerar(new GeradorFicha.Parametros(null, raca, null, 5, forcada,
                        GeradorAtributos.Metodo.ARRAY_PADRAO, true));
                Faccao esperada = raca.faccao().escolheOLado() ? forcada : raca.faccao();
                if (f.faccao() != esperada) {
                    racaManda = false;
                }
            }
        }
        confere("a raça manda na facçao, menos as neutras", racaManda);

        Raca pandaren = r.buscarRaca("Pandaren").orElseThrow();
        Ficha semEscolha = g.gerar(new GeradorFicha.Parametros(null, pandaren, null, 1, null,
                GeradorAtributos.Metodo.ARRAY_PADRAO, true));
        confere("raça neutra sem escolha fica Neutro", semEscolha.faccao() == Faccao.NEUTRO);

        int alianca = 0, horda = 0, neutro = 0;
        for (Raca raca : r.listarRacas()) {
            if (raca.faccao() == Faccao.ALIANCA) alianca++;
            else if (raca.faccao() == Faccao.HORDA) horda++;
            else neutro++;
        }
        confere("8 Alianca, 10 Horda, 2 Neutro", alianca == 8 && horda == 10 && neutro == 2);
    }

    private static void armaduras(RepositorioLocal r) {
        confere("placas ignoram a destreza", Armadura.PLACAS.defesa(5) == Armadura.PLACAS.defesa(0));
        confere("malha trava a destreza em +2", Armadura.MALHA.defesa(5) == 16);
        confere("couro usa a destreza inteira", Armadura.COURO.defesa(4) == 16);
        confere("destreza negativa ainda atrapalha o tecido", Armadura.TECIDO.defesa(-1) == 10);

        confere("Guerreiro usa placas",
                r.buscarClasse("Guerreiro").orElseThrow().armadura() == Armadura.PLACAS);
        confere("Mago usa tecido",
                r.buscarClasse("Mago").orElseThrow().armadura() == Armadura.TECIDO);
        confere("Caçador usa malha",
                r.buscarClasse("Caçador").orElseThrow().armadura() == Armadura.MALHA);

        boolean todasTemArmadura = true;
        for (Classe c : r.listarClasses()) {
            if (c.armadura() == null) {
                todasTemArmadura = false;
            }
        }
        confere("toda classe tem armadura", todasTemArmadura);
    }

    private static void contas(RepositorioLocal r, GeradorFicha g) {
        confere("modificador de 10 é 0", Atributo.modificador(10) == 0);
        confere("modificador de 18 é +4", Atributo.modificador(18) == 4);
        confere("modificador de 7 é -2", Atributo.modificador(7) == -2);

        // nenhuma combinaçao pode dar vida ou armadura zerada
        boolean valoresSaos = true;
        for (Raca raca : r.listarRacas()) {
            for (Classe classe : ClassesPorRaca.daRaca(raca, r.listarClasses())) {
                for (int nivel : new int[]{1, 10, 20}) {
                    Ficha f = g.gerar(new GeradorFicha.Parametros(null, raca, classe, nivel,
                            Faccao.ALIANCA, GeradorAtributos.Metodo.ROLAGEM_4D6, true));
                    if (f.pontosDeVida() < 1 || f.classeArmadura() < 1
                            || f.bonusProficiencia() < 2) {
                        valoresSaos = false;
                    }
                }
            }
        }
        confere("vida, armadura e proficiencia nunca saem zerados", valoresSaos);

        Ficha nivel1 = g.gerar(new GeradorFicha.Parametros(null,
                r.buscarRaca("Humano").orElseThrow(), r.buscarClasse("Mago").orElseThrow(),
                1, Faccao.ALIANCA, GeradorAtributos.Metodo.ARRAY_PADRAO, true));
        confere("proficiencia no nivel 1 é +2", nivel1.bonusProficiencia() == 2);

        Ficha nivel20 = g.gerar(new GeradorFicha.Parametros(null,
                r.buscarRaca("Humano").orElseThrow(), r.buscarClasse("Mago").orElseThrow(),
                20, Faccao.ALIANCA, GeradorAtributos.Metodo.ARRAY_PADRAO, true));
        confere("proficiencia no nivel 20 é +6", nivel20.bonusProficiencia() == 6);

        // o atributo nunca pode passar de 30 nem ficar abaixo de 1
        boolean dentroDoLimite = true;
        for (Raca raca : r.listarRacas()) {
            Ficha f = gerar(g, raca, null);
            for (Atributo a : Atributo.values()) {
                if (f.valorFinal(a) < 1 || f.valorFinal(a) > 30) {
                    dentroDoLimite = false;
                }
            }
        }
        confere("atributo fica entre 1 e 30", dentroDoLimite);
    }

    private static void nomes(RepositorioLocal r) {
        boolean nenhumVazio = true;
        for (Raca raca : r.listarRacas()) {
            for (int i = 0; i < 100; i++) {
                String nome = GeradorNomes.gerar(raca);
                if (nome == null || nome.isBlank()) {
                    nenhumVazio = false;
                }
            }
        }
        confere("nenhum nome sai vazio", nenhumVazio);

        // tem q dar variedade, senao o gerador nao serve pra nada
        Raca orc = r.buscarRaca("Orc").orElseThrow();
        Set<String> vistos = new HashSet<>();
        for (int i = 0; i < 2000; i++) {
            vistos.add(GeradorNomes.gerar(orc));
        }
        confere("2000 sorteios de orc dao mais de 500 nomes diferentes", vistos.size() > 500);

        // raça nula nao pode quebrar, cai na tabela de humano
        confere("raça nula nao quebra o gerador", GeradorNomes.gerar(null) != null);
    }

    private static void repositorioDeArquivo() throws IOException {
        Path temporario = Files.createTempFile("racas", ".txt");
        Files.writeString(temporario, """
                # raça inventada, so pra testar a leitura
                Ogro ; HORDA ; Grande ; FORCA+3,INTELIGENCIA-2 ; Forte demais | Couro grosso ; Nao passa em porta estreita
                Arakkoa ; ALIANCA ; Medio ; DESTREZA+2 ; Planar ;
                """, StandardCharsets.UTF_8);

        RepositorioDados doArquivo = new RepositorioArquivo(temporario);
        confere("leu as 2 raças do arquivo", doArquivo.listarRacas().size() == 2);

        Raca ogro = doArquivo.buscarRaca("Ogro").orElseThrow();
        confere("leu o bonus de Força", ogro.bonus().get(Atributo.FORCA) == 3);
        confere("leu bonus negativo", ogro.bonus().get(Atributo.INTELIGENCIA) == -2);
        confere("leu a facçao", ogro.faccao() == Faccao.HORDA);
        confere("leu as 2 vantagens", ogro.vantagens().size() == 2);
        confere("leu a limitaçao", ogro.limitacoes().size() == 1);

        Raca arakkoa = doArquivo.buscarRaca("Arakkoa").orElseThrow();
        confere("raça sem limitaçao vem com lista vazia", arakkoa.limitacoes().isEmpty());

        confere("as classes continuam vindo do codigo", doArquivo.listarClasses().size() == 13);

        // linha torta tem q avisar qual linha é, nao so estourar
        Path torto = Files.createTempFile("torto", ".txt");
        Files.writeString(torto, "Ogro ; HORDA ; Grande\n", StandardCharsets.UTF_8);
        boolean avisou = false;
        try {
            new RepositorioArquivo(torto);
        } catch (IOException erro) {
            avisou = erro.getMessage().contains("linha 1");
        }
        confere("arquivo torto avisa qual linha estragou", avisou);

        Files.deleteIfExists(temporario);
        Files.deleteIfExists(torto);
    }

    // ---------- ajudantes ----------

    private static Ficha gerar(GeradorFicha g, Raca raca, Classe classe) {
        return g.gerar(new GeradorFicha.Parametros(null, raca, classe, 5, Faccao.ALIANCA,
                GeradorAtributos.Metodo.ROLAGEM_4D6, true));
    }

    private static int quantasRacas(RepositorioLocal r, String classe) {
        return ClassesPorRaca.daClasse(r.buscarClasse(classe).orElseThrow(), r.listarRacas()).size();
    }

    private static void confere(String oQue, boolean deuCerto) {
        System.out.println((deuCerto ? "  ok      " : "  FALHOU  ") + oQue);
        if (deuCerto) {
            passou++;
        } else {
            falhou++;
        }
    }
}
