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


// O elemento marcante da ficha: cada atributo aparece dentro da silhueta de um d20 

public class SeloD20 extends JComponent {
    private final int valor;
    private final Color cor;

    public SeloD20(int valor, Color cor) {
        this.valor = valor;
        this.cor = cor;
        setPreferredSize(new Dimension(86, 92));
        setMinimumSize(new Dimension(70, 76));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cx = getWidth() / 2, cy = getHeight() / 2;
        int r = Math.min(getWidth(), getHeight()) / 2 - 3;

        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double ang = Math.toRadians(60 * i - 90);
            hex.addPoint(cx + (int) Math.round(r * Math.cos(ang)), cy + (int) Math.round(r * Math.sin(ang)));
        }
        // face central do d20: triângulo interno ligado aos vértices do hexágono
        Polygon face = new Polygon();
        for (int i = 0; i < 3; i++) {
            double ang = Math.toRadians(120 * i - 90);
            face.addPoint(cx + (int) Math.round(r * 0.58 * Math.cos(ang)),
                          cy + (int) Math.round(r * 0.58 * Math.sin(ang)));
        }
        // cada vértice do hexágono se liga a 1 ou 2 cantos do triângulo
        int[][] ligacoes = {{0}, {0, 1}, {1}, {1, 2}, {2}, {2, 0}};

        g2.setColor(Tema.NOITE);
        g2.fillPolygon(hex);
        g2.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 70));
        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i < 6; i++) {
            for (int t : ligacoes[i]) {
                g2.drawLine(hex.xpoints[i], hex.ypoints[i], face.xpoints[t], face.ypoints[t]);
            }
        }
        g2.drawPolygon(face);
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(2f));
        g2.drawPolygon(hex);

        g2.setFont(Tema.NUMERO);
        g2.setColor(Tema.PERGAMINHO);
        String texto = String.valueOf(valor);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, cx - fm.stringWidth(texto) / 2, cy + fm.getAscent() / 2 - 3);
        g2.dispose();
    }
}
