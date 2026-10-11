package dnd.exportacao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DocumentoPdf {

    /** Tamanho A4 em pontos (1 ponto = 1/72 de polegada). */
    public static final double LARGURA_A4 = 595.28;
    public static final double ALTURA_A4 = 841.89;

    public enum Fonte {
        NORMAL("/F1"),
        NEGRITO("/F2"),
        ITALICO("/F3");

        private final String referencia;

        Fonte(String referencia) {
            this.referencia = referencia;
        }
    }

    private static final Charset WIN_ANSI = Charset.forName("windows-1252");

    private final List<StringBuilder> paginas = new ArrayList<>();
    private StringBuilder pagina;

    public DocumentoPdf() {
        novaPagina();
    }

    public final void novaPagina() {
        pagina = new StringBuilder();
        paginas.add(pagina);
    }

    // ---------- desenho ----------
    // O eixo Y é contado a partir do alto da página, que é como se pensa um
    // documento; a conversão para o sistema do PDF acontece aqui dentro.

    public void texto(double x, double yDoTopo, Fonte fonte, double tamanho, String conteudo) {
        pagina.append("BT ").append(fonte.referencia).append(' ').append(numero(tamanho))
                .append(" Tf ").append(numero(x)).append(' ').append(numero(aPartirDoTopo(yDoTopo)))
                .append(" Td (").append(escapar(conteudo)).append(") Tj ET\n");
    }

    /** Escreve o texto centralizado em torno de {@code centroX}. */
    public void textoCentralizado(double centroX, double yDoTopo, Fonte fonte, double tamanho,
                                  String conteudo) {
        texto(centroX - LarguraDeTexto.medir(conteudo, fonte, tamanho) / 2,
                yDoTopo, fonte, tamanho, conteudo);
    }

    /** Escreve o texto terminando em {@code direitaX}. */
    public void textoAlinhadoADireita(double direitaX, double yDoTopo, Fonte fonte, double tamanho,
                                      String conteudo) {
        texto(direitaX - LarguraDeTexto.medir(conteudo, fonte, tamanho),
                yDoTopo, fonte, tamanho, conteudo);
    }

    /**
     * Escreve um parágrafo quebrando as linhas na largura pedida.
     *
     * @return a altura ocupada
     */
    public double paragrafo(double x, double yDoTopo, double largura, Fonte fonte, double tamanho,
                            double entrelinha, String conteudo) {
        double y = yDoTopo;
        for (String linha : LarguraDeTexto.quebrar(conteudo, largura, fonte, tamanho)) {
            texto(x, y, fonte, tamanho, linha);
            y += entrelinha;
        }
        return y - yDoTopo;
    }

    public void linha(double x1, double y1DoTopo, double x2, double y2DoTopo, double espessura) {
        pagina.append(numero(espessura)).append(" w ")
                .append(numero(x1)).append(' ').append(numero(aPartirDoTopo(y1DoTopo))).append(" m ")
                .append(numero(x2)).append(' ').append(numero(aPartirDoTopo(y2DoTopo))).append(" l S\n");
    }

    public void retangulo(double x, double yDoTopo, double largura, double altura, double espessura) {
        pagina.append(numero(espessura)).append(" w ")
                .append(numero(x)).append(' ').append(numero(aPartirDoTopo(yDoTopo + altura)))
                .append(' ').append(numero(largura)).append(' ').append(numero(altura))
                .append(" re S\n");
    }

    /** Círculo desenhado com quatro curvas de Bézier, que é como o PDF faz arcos. */
    public void circulo(double centroX, double centroYDoTopo, double raio, double espessura) {
        caminhoDoCirculo(centroX, centroYDoTopo, raio, espessura);
        pagina.append("S\n");
    }

    /**
     * Círculo com o miolo branco, que apaga o que passa por baixo dele.
     *
     * <p>É o que faz o círculo do modificador ficar pendurado na borda da caixa
     * de atributo sem a régua da caixa atravessando o número.
     */
    public void circuloOpaco(double centroX, double centroYDoTopo, double raio, double espessura) {

        pagina.append("1 1 1 rg\n");
        caminhoDoCirculo(centroX, centroYDoTopo, raio, espessura);

        pagina.append("B\n0 0 0 rg\n");
    }

    private void caminhoDoCirculo(double centroX, double centroYDoTopo, double raio,
                                  double espessura) {
        double k = raio * 0.5523; // aproximação de um quarto de círculo por Bézier
        double cx = centroX;
        double cy = aPartirDoTopo(centroYDoTopo);

        pagina.append(numero(espessura)).append(" w ")
                .append(numero(cx - raio)).append(' ').append(numero(cy)).append(" m\n");
        curva(cx - raio, cy + k, cx - k, cy + raio, cx, cy + raio);
        curva(cx + k, cy + raio, cx + raio, cy + k, cx + raio, cy);
        curva(cx + raio, cy - k, cx + k, cy - raio, cx, cy - raio);
        curva(cx - k, cy - raio, cx - raio, cy - k, cx - raio, cy);
    }

    private void curva(double x1, double y1, double x2, double y2, double x3, double y3) {
        pagina.append(numero(x1)).append(' ').append(numero(y1)).append(' ')
                .append(numero(x2)).append(' ').append(numero(y2)).append(' ')
                .append(numero(x3)).append(' ').append(numero(y3)).append(" c\n");
    }

    public void cor(double vermelho, double verde, double azul) {
        pagina.append(numero(vermelho)).append(' ').append(numero(verde)).append(' ')
                .append(numero(azul)).append(" RG ")
                .append(numero(vermelho)).append(' ').append(numero(verde)).append(' ')
                .append(numero(azul)).append(" rg\n");
    }

    public void preto() {
        cor(0, 0, 0);
    }

    // ---------- montagem do arquivo ----------

    public void salvar(Path destino) throws IOException {
        Files.write(destino, montar());
    }

    private byte[] montar() throws IOException {
        List<byte[]> objetos = new ArrayList<>();
        int primeiroConteudo = 4;
        int totalPaginas = paginas.size();

        StringBuilder filhos = new StringBuilder();
        for (int i = 0; i < totalPaginas; i++) {
            filhos.append(primeiroConteudo + totalPaginas + i).append(" 0 R ");
        }

        objetos.add(bytes("<< /Type /Catalog /Pages 2 0 R >>"));
        objetos.add(bytes("<< /Type /Pages /Kids [" + filhos.toString().trim()
                + "] /Count " + totalPaginas + " >>"));
        objetos.add(bytes("<< /Font << /F1 " + (primeiroConteudo + totalPaginas * 2) + " 0 R"
                + " /F2 " + (primeiroConteudo + totalPaginas * 2 + 1) + " 0 R"
                + " /F3 " + (primeiroConteudo + totalPaginas * 2 + 2) + " 0 R >> >>"));

        // Fluxos de conteúdo, um por página
        for (StringBuilder conteudo : paginas) {
            byte[] fluxo = conteudo.toString().getBytes(WIN_ANSI);
            ByteArrayOutputStream objeto = new ByteArrayOutputStream();
            objeto.write(bytes("<< /Length " + fluxo.length + " >>\nstream\n"));
            objeto.write(fluxo);
            objeto.write(bytes("\nendstream"));
            objetos.add(objeto.toByteArray());
        }

        // Páginas
        for (int i = 0; i < totalPaginas; i++) {
            objetos.add(bytes("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 "
                    + numero(LARGURA_A4) + " " + numero(ALTURA_A4) + "]"
                    + " /Resources 3 0 R /Contents " + (primeiroConteudo + i) + " 0 R >>"));
        }

        objetos.add(fonte("Times-Roman"));
        objetos.add(fonte("Times-Bold"));
        objetos.add(fonte("Times-Italic"));

        return escreverCorpo(objetos);
    }

    private byte[] escreverCorpo(List<byte[]> objetos) throws IOException {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        saida.write(bytes("%PDF-1.4\n"));
        saida.write(new byte[]{'%', (byte) 0xE2, (byte) 0xE3, (byte) 0xCF, (byte) 0xD3, '\n'});

        int[] posicoes = new int[objetos.size() + 1];
        for (int i = 0; i < objetos.size(); i++) {
            posicoes[i + 1] = saida.size();
            saida.write(bytes((i + 1) + " 0 obj\n"));
            saida.write(objetos.get(i));
            saida.write(bytes("\nendobj\n"));
        }

        int inicioDaTabela = saida.size();
        saida.write(bytes("xref\n0 " + (objetos.size() + 1) + "\n"));
        saida.write(bytes("0000000000 65535 f \n"));
        for (int i = 1; i <= objetos.size(); i++) {
            saida.write(bytes(String.format("%010d 00000 n \n", posicoes[i])));
        }
        saida.write(bytes("trailer\n<< /Size " + (objetos.size() + 1) + " /Root 1 0 R >>\n"
                + "startxref\n" + inicioDaTabela + "\n%%EOF\n"));
        return saida.toByteArray();
    }

    private byte[] fonte(String nome) {
        return bytes("<< /Type /Font /Subtype /Type1 /BaseFont /" + nome
                + " /Encoding /WinAnsiEncoding >>");
    }

    private static byte[] bytes(String texto) {
        return texto.getBytes(WIN_ANSI);
    }

    private double aPartirDoTopo(double y) {
        return ALTURA_A4 - y;
    }

    private static String numero(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
