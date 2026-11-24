package wow.dados;

import wow.model.Atributo;
import wow.model.Classe;
import wow.model.Faccao;
import wow.model.Raca;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Le as raças de um arquivo de texto em vez de ter elas escritas no codigo.
//
// Serve pra alguem adicionar raça sem precisar recompilar o programa, e é a
// razao do RepositorioDados existir como interface: trocar a origem dos dados
// sem mexer em mais nada.
//
// As classes continuam vindo do RepositorioLocal, pq elas mexem com regra
// (armadura, dado de vida, salvaguardas) e nao so com dado solto.
//
// Formato do arquivo, um por linha, separado por ponto e virgula:
//
//   nome ; faccao ; tamanho ; bonus ; vantagens ; limitacoes
//
// bonus      -> FORCA+2,CONSTITUICAO+1   (ou vazio)
// vantagens  -> separadas por barra: Uma coisa | Outra coisa
// limitacoes -> igual as vantagens, pode ficar vazio
// linha em branco ou comecando com # é ignorada
//
// exemplo:
//   Ogro ; HORDA ; Grande ; FORCA+3,INTELIGENCIA-2 ; Forte demais | Couro grosso ; Nao passa em porta
public class RepositorioArquivo implements RepositorioDados {

    // no WoW toda raça corre igual, entao o arquivo nem pede esse campo
    private static final double DESLOCAMENTO_PADRAO = 9;

    private final Map<String, Raca> racas = new LinkedHashMap<>();
    private final RepositorioLocal classes = new RepositorioLocal();

    public RepositorioArquivo(Path arquivo) throws IOException {
        int numero = 0;
        for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
            numero++;
            String limpa = linha.trim();
            if (limpa.isEmpty() || limpa.startsWith("#")) {
                continue;
            }
            try {
                Raca raca = lerRaca(limpa);
                racas.put(raca.nome(), raca);
            } catch (RuntimeException erro) {
                // avisa qual linha estragou, senao fica dificil de achar
                throw new IOException("erro na linha " + numero + " de " + arquivo.getFileName()
                        + ": " + erro.getMessage(), erro);
            }
        }
        if (racas.isEmpty()) {
            throw new IOException("o arquivo " + arquivo.getFileName() + " nao tem nenhuma raça");
        }
    }

    private Raca lerRaca(String linha) {
        String[] campos = linha.split(";", -1);
        if (campos.length != 6) {
            throw new IllegalArgumentException("esperava 6 campos separados por ';', achei "
                    + campos.length);
        }
        return new Raca(
                campos[0].trim(),
                lerBonus(campos[3].trim()),
                campos[2].trim(),
                DESLOCAMENTO_PADRAO,
                lerLista(campos[4]),
                lerLista(campos[5]),
                Faccao.valueOf(campos[1].trim().toUpperCase()));
    }

    // le "FORCA+2,CONSTITUICAO+1" e vira um mapa
    private Map<Atributo, Integer> lerBonus(String texto) {
        Map<Atributo, Integer> bonus = new EnumMap<>(Atributo.class);
        if (texto.isEmpty()) {
            return bonus;
        }
        for (String parte : texto.split(",")) {
            String p = parte.trim();
            int corte = Math.max(p.indexOf('+'), p.indexOf('-'));
            if (corte <= 0) {
                throw new IllegalArgumentException("bonus sem sinal: " + p);
            }
            Atributo atributo = Atributo.valueOf(p.substring(0, corte).trim().toUpperCase());
            bonus.put(atributo, Integer.parseInt(p.substring(corte).trim()));
        }
        return bonus;
    }

    // le "Uma coisa | Outra coisa" e vira uma lista
    private List<String> lerLista(String texto) {
        List<String> itens = new ArrayList<>();
        for (String parte : texto.split("\\|")) {
            String item = parte.trim();
            if (!item.isEmpty()) {
                itens.add(item);
            }
        }
        return itens;
    }

    @Override public List<Raca> listarRacas() { return new ArrayList<>(racas.values()); }
    @Override public List<Classe> listarClasses() { return classes.listarClasses(); }
    @Override public Optional<Raca> buscarRaca(String nome) { return Optional.ofNullable(racas.get(nome)); }
    @Override public Optional<Classe> buscarClasse(String nome) { return classes.buscarClasse(nome); }
}
