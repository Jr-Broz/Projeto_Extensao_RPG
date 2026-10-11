package dnd.ui;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Painel que se desenha como uma folha de pergaminho apoiada na mesa, com a
 * moldura de régua dupla e os florões de canto que as páginas impressas antigas
 * trazem.
 *
 * <p>O envelhecimento é todo pintado em código — fibras, manchas e sombra nas
 * bordas — para o programa continuar sendo um único diretório de código-fonte,
 * sem imagens de fundo para carregar e sem caminho de arquivo que possa quebrar
 * em outro sistema.
 */
public class Pergaminho extends JPanel {

    /** Textura de fibra gerada uma vez e reaproveitada por todos os painéis. */
    private static final TexturePaint FIBRAS = criarFibras();

    /** Quanto a moldura decorativa fica para dentro da borda da folha. */
    private static final int RECUO_DA_MOLDURA = 12;

    private final boolean comMoldura;
    private int[] manchasX;
    private int[] manchasY;
    private int[] manchasRaio;

    public Pergaminho(LayoutManager layout) {
        this(layout, true);
    }

    public Pergaminho(LayoutManager layout, boolean comMoldura) {
        super(layout);
        this.comMoldura = comMoldura;
        setOpaque(false);
    }

    private static TexturePaint criarFibras() {
        int lado = 128;
        BufferedImage ladrilho = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
        Random fiapo = new Random(7); // semente fixa: a textura é sempre a mesma
        Graphics2D g = ladrilho.createGraphics();
        g.setColor(Tema.PAPEL);
        g.fillRect(0, 0, lado, lado);

        for (int i = 0; i < 1000; i++) {
            int x = fiapo.nextInt(lado);
            int y = fiapo.nextInt(lado);
            int tom = 16 + fiapo.nextInt(24);
            g.setColor(new Color(92, 72, 42, tom));
            if (fiapo.nextInt(4) == 0) {
                g.drawLine(x, y, x + 1 + fiapo.nextInt(3), y); // fiapo deitado
            } else {
                g.fillRect(x, y, 1, 1);
            }
        }
        g.dispose();
        return new TexturePaint(ladrilho, new Rectangle(0, 0, lado, lado));
    }

    /** Manchas de idade, sorteadas uma vez por painel e fixas depois disso. */
    private void prepararManchas(int largura, int altura) {
        if (manchasX != null) {
            return;
        }
        Random sorte = new Random(largura * 31L + altura);
        int quantidade = 5;
        manchasX = new int[quantidade];
        manchasY = new int[quantidade];
        manchasRaio = new int[quantidade];
        for (int i = 0; i < quantidade; i++) {
            manchasX[i] = sorte.nextInt(Math.max(1, largura));
            manchasY[i] = sorte.nextInt(Math.max(1, altura));
            manchasRaio[i] = 24 + sorte.nextInt(44);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int largura = getWidth() - 4;
        int altura = getHeight() - 5;
        prepararManchas(largura, altura);

        // Sombra projetada na mesa
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRect(4, 5, largura, altura);

        // A folha
        g2.setPaint(FIBRAS);
        g2.fillRect(0, 0, largura, altura);

        // Manchas de idade
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.028f));
        g2.setColor(new Color(0x7A5A2E));
        for (int i = 0; i < manchasX.length; i++) {
            int raio = manchasRaio[i];
            g2.fillOval(manchasX[i] - raio / 2, manchasY[i] - raio / 2, raio, raio);
        }
        g2.setComposite(AlphaComposite.SrcOver);

        escurecerBordas(g2, largura, altura);

        if (comMoldura) {
            desenharMoldura(g2, largura, altura);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    /** Bordas mais escuras, como papel manuseado muitas vezes. */
    private void escurecerBordas(Graphics2D g2, int largura, int altura) {
        int faixa = 24;
        for (int i = 0; i < faixa; i++) {
            int alfa = (int) (32 * (1 - (double) i / faixa));
            g2.setColor(new Color(110, 86, 50, alfa));
            g2.drawRect(i, i, largura - 1 - i * 2, altura - 1 - i * 2);
        }
    }

    /** Régua dupla com um florão em cada canto, como página de livro impresso. */
    private void desenharMoldura(Graphics2D g2, int largura, int altura) {
        int x = RECUO_DA_MOLDURA;
        int y = RECUO_DA_MOLDURA;
        int w = largura - RECUO_DA_MOLDURA * 2 - 1;
        int h = altura - RECUO_DA_MOLDURA * 2 - 1;

        g2.setColor(Tema.TINTA_FRACA);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawRect(x, y, w, h);
        g2.setStroke(new BasicStroke(0.7f));
        g2.drawRect(x + 4, y + 4, w - 8, h - 8);

        florao(g2, x, y, 1, 1);
        florao(g2, x + w, y, -1, 1);
        florao(g2, x, y + h, 1, -1);
        florao(g2, x + w, y + h, -1, -1);
    }

    /**
     * Florão de canto: um losango sobre a quina e dois riscos curtos saindo
     * dela, que é o enfeite mais comum nas molduras de página antigas.
     */
    private void florao(Graphics2D g2, int x, int y, int paraDireita, int paraBaixo) {
        int braco = 11;

        g2.setColor(Tema.RUBRICA);
        g2.fillPolygon(
                new int[]{x, x + 4 * paraDireita, x, x - 4 * paraDireita},
                new int[]{y - 4 * paraBaixo, y, y + 4 * paraBaixo, y}, 4);

        g2.setColor(Tema.TINTA_FRACA);
        g2.setStroke(new BasicStroke(0.9f));
        g2.drawLine(x + 7 * paraDireita, y, x + (7 + braco) * paraDireita, y);
        g2.drawLine(x, y + 7 * paraBaixo, x, y + (7 + braco) * paraBaixo);
    }

    /** Espaço que a moldura ocupa, para o conteúdo não encostar nela. */
    public static int recuoDaMoldura() {
        return RECUO_DA_MOLDURA;
    }
}
