package dnd.exportacao;

import dnd.model.Atributo;
import dnd.model.Ficha;
import dnd.model.TipoFicha;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;


public class FichaEmPdf {

    private static final double MARGEM = 44;
    private static final double LARGURA_UTIL = DocumentoPdf.LARGURA_A4 - MARGEM * 2;
    private static final double DIREITA = MARGEM + LARGURA_UTIL;

    /** Escreve a ficha no caminho indicado. */
    public void salvar(Ficha ficha, Path destino) throws IOException {
        DocumentoPdf pdf = new DocumentoPdf();
        pdf.preto();

        moldura(pdf);

        double y = MARGEM + 16;
        y = cabecalho(pdf, ficha, y);
        y = atributos(pdf, ficha, y + 24);
        y = estatisticas(pdf, ficha, y + 22);
        y = tracos(pdf, ficha, y + 22);
        y = rolagens(pdf, ficha, y + 18);
        anotacoes(pdf, y + 18);
        rodape(pdf);

        pdf.salvar(destino);
    }

    public static String nomeDeArquivo(Ficha ficha) {
        String base = ficha.nome()
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return (base.isBlank() ? "ficha" : base) + ".pdf";
    }

    private void moldura(DocumentoPdf pdf) {
        double recuo = 18;
        double x = recuo;
        double y = recuo;
        double largura = DocumentoPdf.LARGURA_A4 - recuo * 2;
        double altura = DocumentoPdf.ALTURA_A4 - recuo * 2;

        pdf.retangulo(x, y, largura, altura, 1.2);
        pdf.retangulo(x + 4, y + 4, largura - 8, altura - 8, 0.6);

        losango(pdf, x, y);
        losango(pdf, x + largura, y);
        losango(pdf, x, y + altura);
        losango(pdf, x + largura, y + altura);
    }

    private void losango(DocumentoPdf pdf, double x, double y) {
        double r = 4;
        pdf.linha(x, y - r, x + r, y, 1.0);
        pdf.linha(x + r, y, x, y + r, 1.0);
        pdf.linha(x, y + r, x - r, y, 1.0);
        pdf.linha(x - r, y, x, y - r, 1.0);
    }

    // ---------- partes da ficha ----------

    private double cabecalho(DocumentoPdf pdf, Ficha ficha, double y) {
        double ladoCapitular = 44;

        pdf.retangulo(MARGEM, y, ladoCapitular, ladoCapitular, 1.6);
        pdf.retangulo(MARGEM + 4, y + 4, ladoCapitular - 8, ladoCapitular - 8, 0.6);
        pdf.textoCentralizado(MARGEM + ladoCapitular / 2, y + ladoCapitular / 2 + 11,
                DocumentoPdf.Fonte.NEGRITO, 30, String.valueOf(inicial(ficha.nome())));

        double textoX = MARGEM + ladoCapitular + 8;
        pdf.texto(textoX, y + 24, DocumentoPdf.Fonte.NEGRITO, 25, resto(ficha.nome()));

        String descricao = ficha.raca().nome() + " " + ficha.classe().nome().toLowerCase()
                + ", nível " + ficha.nivel();
        pdf.texto(textoX, y + 42, DocumentoPdf.Fonte.ITALICO, 13, descricao);

        String papel = ficha.tipo() == TipoFicha.INIMIGO ? "INIMIGO" : "ALIADO";
        double larguraSelo = 84;
        pdf.retangulo(DIREITA - larguraSelo, y + 2, larguraSelo, 26, 1.4);
        pdf.textoCentralizado(DIREITA - larguraSelo / 2, y + 20,
                DocumentoPdf.Fonte.NEGRITO, 12, papel);

        double base = y + ladoCapitular + 12;
        pdf.linha(MARGEM, base, DIREITA, base, 1.0);
        return base;
    }

    private char inicial(String nome) {
        for (char c : nome.toCharArray()) {
            if (Character.isLetter(c)) {
                return Character.toUpperCase(c);
            }
        }
        return '?';
    }

    private String resto(String nome) {
        for (int i = 0; i < nome.length(); i++) {
            if (Character.isLetter(nome.charAt(i))) {
                return nome.substring(i + 1);
            }
        }
        return nome;
    }

    private double atributos(DocumentoPdf pdf, Ficha ficha, double y) {
        double vao = 8;
        double largura = (LARGURA_UTIL - vao * 5) / 6;
        double alturaCaixa = 48;
        List<Atributo> salvaguardas = ficha.classe().salvaguardas();

        for (int i = 0; i < Atributo.values().length; i++) {
            Atributo atributo = Atributo.values()[i];
            double x = MARGEM + i * (largura + vao);
            double centro = x + largura / 2;
            boolean principal = atributo == ficha.classe().atributoPrincipal();

            pdf.textoCentralizado(centro, y + 10, DocumentoPdf.Fonte.NEGRITO, 10, atributo.nome());

            pdf.retangulo(x, y + 16, largura, alturaCaixa, principal ? 1.8 : 1.1);
            pdf.retangulo(x + 3, y + 19, largura - 6, alturaCaixa - 6, 0.5);
            pdf.textoCentralizado(centro, y + 16 + alturaCaixa / 2 + 8,
                    DocumentoPdf.Fonte.NEGRITO, 21,
                    String.valueOf(ficha.valorFinal(atributo)));

            double centroCirculo = y + 16 + alturaCaixa + 5;
            pdf.circuloOpaco(centro, centroCirculo, 13, principal ? 1.8 : 1.1);
            pdf.textoCentralizado(centro, centroCirculo + 5, DocumentoPdf.Fonte.NEGRITO, 13,
                    comSinal(ficha.modificador(atributo)));

            if (salvaguardas.contains(atributo)) {
                int bonus = ficha.modificador(atributo) + ficha.bonusProficiencia();
                pdf.textoCentralizado(centro, centroCirculo + 28, DocumentoPdf.Fonte.ITALICO, 8,
                        "salvaguarda " + comSinal(bonus));
            }
        }
        return y + 16 + alturaCaixa + 18 + 22;
    }

    private double estatisticas(DocumentoPdf pdf, Ficha ficha, double y) {
        String[][] campos = {
                {"Pontos de vida", String.valueOf(ficha.pontosDeVida())},
                {"Armadura (CA)", String.valueOf(ficha.classeArmadura())},
                {"Deslocamento", deslocamento(ficha.raca().deslocamento())},
                {"Proficiência", comSinal(ficha.bonusProficiencia())},
                {"Dado de vida", "d" + ficha.classe().dadoVida()},
        };

        double vao = 8;
        double largura = (LARGURA_UTIL - vao * (campos.length - 1)) / campos.length;
        double altura = 44;

        for (int i = 0; i < campos.length; i++) {
            double x = MARGEM + i * (largura + vao);
            pdf.retangulo(x, y, largura, altura, 1.1);
            pdf.retangulo(x + 3, y + 3, largura - 6, altura - 6, 0.5);
            pdf.textoCentralizado(x + largura / 2, y + 24, DocumentoPdf.Fonte.NEGRITO, 16,
                    campos[i][1]);
            pdf.textoCentralizado(x + largura / 2, y + 37, DocumentoPdf.Fonte.ITALICO, 9,
                    campos[i][0]);
        }
        return y + altura;
    }

    private String deslocamento(double metros) {
        return metros % 1 == 0
                ? String.format(Locale.US, "%.0f m", metros)
                : String.format(Locale.US, "%.1f m", metros).replace('.', ',');
    }

    private double tracos(DocumentoPdf pdf, Ficha ficha, double y) {
        double vao = 14;
        double largura = (LARGURA_UTIL - vao) / 2;

        List<String> vantagens = ficha.raca().vantagens();
        List<String> desvantagens = ficha.raca().desvantagens().isEmpty()
                ? List.of("Nenhuma desvantagem racial")
                : ficha.raca().desvantagens();

        double altura = Math.max(alturaDaLista(vantagens, largura), alturaDaLista(desvantagens, largura));

        lista(pdf, MARGEM, y, largura, altura, "Vantagens", vantagens);
        lista(pdf, MARGEM + largura + vao, y, largura, altura, "Desvantagens", desvantagens);
        return y + altura;
    }

    private double alturaDaLista(List<String> itens, double largura) {
        double altura = 26;
        for (String item : itens) {
            altura += LarguraDeTexto.quebrar(item, largura - 34,
                    DocumentoPdf.Fonte.NORMAL, 10).size() * 13;
        }
        return altura + 10;
    }

    private void lista(DocumentoPdf pdf, double x, double y, double largura, double altura,
                       String titulo, List<String> itens) {
        quadroComLegenda(pdf, x, y, largura, altura, titulo);

        double linha = y + 30;
        for (String item : itens) {

            pdf.texto(x + 12, linha, DocumentoPdf.Fonte.NORMAL, 10, "•");
            linha += pdf.paragrafo(x + 22, linha, largura - 34,
                    DocumentoPdf.Fonte.NORMAL, 10, 13, item);
        }
    }

    private double rolagens(DocumentoPdf pdf, Ficha ficha, double y) {
        double altura = 26 + ficha.detalhesRolagem().size() * 13 + 10;
        quadroComLegenda(pdf, MARGEM, y, LARGURA_UTIL, altura, "Como os atributos saíram");

        double linha = y + 30;
        for (String detalhe : ficha.detalhesRolagem()) {
            pdf.texto(MARGEM + 12, linha, DocumentoPdf.Fonte.ITALICO, 10, detalhe);
            linha += 13;
        }
        return y + altura;
    }


    private void anotacoes(DocumentoPdf pdf, double y) {
        double fim = DocumentoPdf.ALTURA_A4 - MARGEM - 22;
        double altura = fim - y;
        if (altura < 70) {
            return;
        }

        quadroComLegenda(pdf, MARGEM, y, LARGURA_UTIL, altura, "Anotações da sessão");
        for (double linha = y + 40; linha < fim - 12; linha += 20) {
            pdf.linha(MARGEM + 14, linha, DIREITA - 14, linha, 0.4);
        }
    }

    private void quadroComLegenda(DocumentoPdf pdf, double x, double y, double largura,
                                  double altura, String titulo) {
        double inicioDoTexto = x + 16;
        double vao = LarguraDeTexto.medir(titulo, DocumentoPdf.Fonte.NEGRITO, 12) + 14;

        pdf.linha(x, y, inicioDoTexto - 6, y, 1.2);
        pdf.linha(inicioDoTexto + vao - 6, y, x + largura, y, 1.2);
        pdf.linha(x, y, x, y + altura, 1.2);
        pdf.linha(x + largura, y, x + largura, y + altura, 1.2);
        pdf.linha(x, y + altura, x + largura, y + altura, 1.2);

        pdf.texto(inicioDoTexto, y + 4, DocumentoPdf.Fonte.NEGRITO, 12, titulo);
    }

    private void rodape(DocumentoPdf pdf) {
        pdf.texto(MARGEM, DocumentoPdf.ALTURA_A4 - MARGEM + 6, DocumentoPdf.Fonte.ITALICO, 9,
                "Gerado pela Forja de Fichas — regras do D&D 5e (SRD)");
    }

    private static String comSinal(int valor) {
        return String.format("%+d", valor);
    }
}
