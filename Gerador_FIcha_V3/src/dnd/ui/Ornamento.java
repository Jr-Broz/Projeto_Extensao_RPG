package dnd.ui;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

/**
 * Filete com um losango no meio e um ponto de cada lado, do jeito que os livros
 * antigos separam as partes de um capítulo.
 */
public class Ornamento extends JComponent {

    public Ornamento() {
        setPreferredSize(new Dimension(10, 18));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int meioY = getHeight() / 2;
        int meioX = getWidth() / 2;
        int vao = 20;

        g2.setColor(Tema.TINTA_FRACA);
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(0, meioY, meioX - vao, meioY);
        g2.drawLine(meioX + vao, meioY, getWidth(), meioY);
        g2.fillOval(meioX - vao - 7, meioY - 2, 4, 4);
        g2.fillOval(meioX + vao + 3, meioY - 2, 4, 4);

        g2.setColor(Tema.RUBRICA);
        g2.fillPolygon(new Polygon(
                new int[]{meioX, meioX + 7, meioX, meioX - 7},
                new int[]{meioY - 5, meioY, meioY + 5, meioY}, 4));

        g2.dispose();
    }
}
