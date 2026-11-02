package dnd.exportacao;

import dnd.model.Atributo;
import dnd.model.Ficha;
import dnd.model.TipoFicha;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

/** Desenha a ficha numa folha e manda para a impressora (ou para um PDF). */
public class FichaImpressa implements Printable {

    private static final Font TITULO = new Font("Serif", Font.BOLD, 22);
    private static final Font SUBTITULO = new Font("Serif", Font.ITALIC, 12);
    private static final Font ROTULO = new Font("Serif", Font.BOLD, 9);
    private static final Font NUMERO = new Font("Serif", Font.BOLD, 20);
    private static final Font TEXTO = new Font("Serif", Font.PLAIN, 10);

    private final Ficha ficha;

    public FichaImpressa(Ficha ficha) {
        this.ficha = ficha;
    }

    /** Abre a janela de impressão do sistema. Lá dá para escolher salvar em PDF. */
    public static void imprimir(Ficha ficha) throws PrinterException {
        PrinterJob impressao = PrinterJob.getPrinterJob();
        impressao.setJobName("Ficha de " + ficha.nome());
        impressao.setPrintable(new FichaImpressa(ficha));
        if (impressao.printDialog()) {
            impressao.print();
        }
    }

    @Override
    public int print(Graphics g, PageFormat pagina, int numeroDaPagina) {
        if (numeroDaPagina > 0) {
            return NO_SUCH_PAGE; // a ficha cabe em uma folha só
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pagina.getImageableX(), pagina.getImageableY());
        desenhar(g2, (int) pagina.getImageableWidth());
        return PAGE_EXISTS;
    }

    private void desenhar(Graphics2D g2, int largura) {
        int y = 30;
        y = desenharCabecalho(g2, largura, y);
        y = desenharAtributos(g2, largura, y + 25);
        y = desenharNumeros(g2, largura, y + 20);

        // As duas listas começam na mesma altura, lado a lado
        int coluna = largura / 2 - 7;
        int topoDasListas = y + 25;
        int fimDaEsquerda = desenharLista(g2, 0, topoDasListas, coluna,
                "Vantagens", ficha.raca().vantagens());
        int fimDaDireita = desenharLista(g2, largura / 2 + 7, topoDasListas, coluna,
                "Desvantagens", desvantagens());

        y = Math.max(fimDaEsquerda, fimDaDireita);
        y = desenharRolagens(g2, y + 20);
        desenharAnotacoes(g2, largura, y + 20);
    }

    /** Raça sem desvantagem ainda ganha uma linha, para a coluna não ficar vazia. */
    private List<String> desvantagens() {
        return ficha.raca().desvantagens().isEmpty()
                ? List.of("Nenhuma desvantagem racial")
                : ficha.raca().desvantagens();
    }

    private int desenharRolagens(Graphics2D g2, int y) {
        g2.setFont(ROTULO);
        g2.drawString("Como os atributos saíram", 0, y);
        g2.setFont(TEXTO);
        int linha = y + 16;
        for (String detalhe : ficha.detalhesRolagem()) {
            g2.drawString(detalhe, 0, linha);
            linha += 13;
        }
        return linha;
    }

    /** Pauta em branco para anotar dano e iniciativa durante a sessão. */
    private void desenharAnotacoes(Graphics2D g2, int largura, int y) {
        g2.setFont(ROTULO);
        g2.drawString("Anotações da sessão", 0, y);
        for (int linha = y + 20; linha < y + 160; linha += 20) {
            g2.drawLine(0, linha, largura, linha);
        }
    }

    private int desenharCabecalho(Graphics2D g2, int largura, int y) {
        g2.setFont(TITULO);
        g2.drawString(ficha.nome(), 0, y);

        g2.setFont(SUBTITULO);
        String descricao = ficha.raca().nome() + " " + ficha.classe().nome().toLowerCase()
                + ", nível " + ficha.nivel();
        g2.drawString(descricao, 0, y + 16);

        String papel = ficha.tipo() == TipoFicha.INIMIGO ? "INIMIGO" : "ALIADO";
        g2.setFont(ROTULO);
        g2.drawRect(largura - 70, y - 14, 70, 20);
        g2.drawString(papel, largura - 70 + 14, y);

        g2.drawLine(0, y + 26, largura, y + 26);
        return y + 26;
    }

    private int desenharAtributos(Graphics2D g2, int largura, int y) {
        int vao = 6;
        int caixa = (largura - vao * 5) / 6;

        for (int i = 0; i < Atributo.values().length; i++) {
            Atributo atributo = Atributo.values()[i];
            int x = i * (caixa + vao);

            g2.setFont(ROTULO);
            centralizar(g2, atributo.nome(), x, x + caixa, y);

            g2.drawRect(x, y + 6, caixa, 45);
            g2.setFont(NUMERO);
            centralizar(g2, String.valueOf(ficha.valorFinal(atributo)), x, x + caixa, y + 40);

            g2.setFont(ROTULO);
            String mod = String.format("%+d", ficha.modificador(atributo));
            g2.drawOval(x + caixa / 2 - 13, y + 45, 26, 20);
            centralizar(g2, mod, x, x + caixa, y + 60);
        }
        return y + 65;
    }

    private int desenharNumeros(Graphics2D g2, int largura, int y) {
        String[][] campos = {
                {"Pontos de vida", String.valueOf(ficha.pontosDeVida())},
                {"Armadura", String.valueOf(ficha.classeArmadura())},
                {"Proficiência", String.format("%+d", ficha.bonusProficiencia())},
                {"Dado de vida", "d" + ficha.classe().dadoVida()},
        };
        int vao = 8;
        int caixa = (largura - vao * 3) / 4;

        for (int i = 0; i < campos.length; i++) {
            int x = i * (caixa + vao);
            g2.drawRect(x, y, caixa, 40);
            g2.setFont(NUMERO);
            centralizar(g2, campos[i][1], x, x + caixa, y + 24);
            g2.setFont(ROTULO);
            centralizar(g2, campos[i][0], x, x + caixa, y + 35);
        }
        return y + 40;
    }

    private int desenharLista(Graphics2D g2, int x, int y, int largura,
                              String titulo, List<String> itens) {
        g2.setFont(ROTULO);
        g2.drawString(titulo, x, y);

        g2.setFont(TEXTO);
        int linha = y + 16;
        for (String item : itens) {
            for (String pedaco : quebrarEmLinhas(g2, "- " + item, largura)) {
                g2.drawString(pedaco, x, linha);
                linha += 13;
            }
        }
        return linha;
    }

    /** Corta o texto em linhas que cabem na largura, quebrando entre palavras. */
    private List<String> quebrarEmLinhas(Graphics2D g2, String texto, int largura) {
        List<String> linhas = new ArrayList<>();
        String atual = "";

        for (String palavra : texto.split(" ")) {
            String tentativa = atual.isEmpty() ? palavra : atual + " " + palavra;
            if (g2.getFontMetrics().stringWidth(tentativa) <= largura || atual.isEmpty()) {
                atual = tentativa;
            } else {
                linhas.add(atual);
                atual = palavra;
            }
        }
        if (!atual.isEmpty()) {
            linhas.add(atual);
        }
        return linhas;
    }

    private void centralizar(Graphics2D g2, String texto, int esquerda, int direita, int y) {
        int meio = (esquerda + direita) / 2;
        int metadeDoTexto = g2.getFontMetrics().stringWidth(texto) / 2;
        g2.drawString(texto, meio - metadeDoTexto, y);
    }
}
