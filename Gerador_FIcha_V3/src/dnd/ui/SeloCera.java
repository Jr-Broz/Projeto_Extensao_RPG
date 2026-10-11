package dnd.ui;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * Selo de lacre marcando se a ficha é de um aliado ou de um inimigo.
 *
 * <p>A borda é irregular de propósito: cera pingada à mão nunca sai redonda. O
 * recorte é sorteado com semente fixa, então o selo é sempre o mesmo e não
 * muda de forma a cada vez que a tela se redesenha.
 */
public class SeloCera extends JComponent {

    private final String texto;
    private final Color cor;

    public SeloCera(String texto, Color cor) {
        this.texto = texto;
        this.cor = cor;
        setPreferredSize(new Dimension(92, 92));
        setToolTipText("Papel na mesa: " + texto.toLowerCase());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;
        int raio = Math.min(getWidth(), getHeight()) / 2 - 6;

        // Contorno irregular da cera
        Polygon borda = new Polygon();
        Random molde = new Random(texto.hashCode());
        int pontos = 28;
        for (int i = 0; i < pontos; i++) {
            double angulo = 2 * Math.PI * i / pontos;
            double variacao = raio * (0.92 + molde.nextDouble() * 0.14);
            borda.addPoint(centroX + (int) Math.round(variacao * Math.cos(angulo)),
                    centroY + (int) Math.round(variacao * Math.sin(angulo)));
        }

        g2.setColor(new Color(0, 0, 0, 55));
        g2.translate(2, 3);
        g2.fillPolygon(borda);
        g2.translate(-2, -3);

        g2.setColor(cor);
        g2.fillPolygon(borda);

        // Brilho da cera: um crescente mais claro no alto, à esquerda
        Area brilho = new Area(new Ellipse2D.Double(centroX - raio * 0.75, centroY - raio * 0.75,
                raio * 1.1, raio * 1.1));
        brilho.subtract(new Area(new Ellipse2D.Double(centroX - raio * 0.5, centroY - raio * 0.45,
                raio * 1.1, raio * 1.1)));
        g2.setColor(new Color(255, 255, 255, 40));
        g2.fill(brilho);

        // Anel gravado pelo carimbo
        g2.setColor(new Color(0, 0, 0, 60));
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawOval(centroX - raio + 7, centroY - raio + 7, (raio - 7) * 2, (raio - 7) * 2);

        g2.setFont(Tema.ROTULO);
        g2.setColor(new Color(0xF2E7CE));
        FontMetrics medidas = g2.getFontMetrics();
        g2.drawString(texto, centroX - medidas.stringWidth(texto) / 2,
                centroY + medidas.getAscent() / 2 - 2);

        g2.dispose();
    }
}
