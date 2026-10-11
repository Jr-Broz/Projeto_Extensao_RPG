package dnd.ui;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Marcas de seleção desenhadas a tinta: um quadrado com visto para as opções
 * que se somam e um círculo preenchido para as que se excluem.
 *
 * <p>Sem isto, caixas de seleção e botões de opção aparecem com o relevo cinza
 * do sistema e destoam do resto da folha.
 */
public class MarcaDeTinta implements Icon {

    private static final int LADO = 18;

    private final boolean redonda;
    private final boolean marcada;

    private MarcaDeTinta(boolean redonda, boolean marcada) {
        this.redonda = redonda;
        this.marcada = marcada;
    }

    /** Troca as marcas de uma caixa de seleção ou de um botão de opção. */
    public static void aplicar(AbstractButton botao, boolean redonda) {
        botao.setIcon(new MarcaDeTinta(redonda, false));
        botao.setSelectedIcon(new MarcaDeTinta(redonda, true));
        botao.setOpaque(false);
        botao.setFocusPainted(false);
        botao.setForeground(Tema.TINTA);
        botao.setFont(Tema.CORPO.deriveFont(16f));
        botao.setIconTextGap(9);
    }

    @Override
    public void paintIcon(Component componente, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Tema.TINTA);
        g2.setStroke(new BasicStroke(1.5f));

        if (redonda) {
            g2.drawOval(x, y, LADO - 1, LADO - 1);
            if (marcada) {
                g2.setColor(Tema.RUBRICA);
                g2.fillOval(x + 5, y + 5, LADO - 10, LADO - 10);
            }
        } else {
            g2.drawRect(x, y, LADO - 1, LADO - 1);
            if (marcada) {
                g2.setColor(Tema.RUBRICA);
                g2.setStroke(new BasicStroke(2.4f));
                g2.drawLine(x + 4, y + 9, x + 7, y + 13);
                g2.drawLine(x + 7, y + 13, x + 14, y + 4);
            }
        }
        g2.dispose();
    }

    @Override
    public int getIconWidth() { return LADO; }

    @Override
    public int getIconHeight() { return LADO; }
}
