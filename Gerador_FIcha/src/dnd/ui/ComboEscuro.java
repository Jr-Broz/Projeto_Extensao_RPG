package dnd.ui;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

/** Deixa o JComboBox no tema escuro (o Nimbus insiste num cinza claro). */
public final class ComboEscuro {
    private ComboEscuro() {}

    public static void aplicar(JComboBox<?> combo) {
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton seta = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Tema.LATAO);
                        int cx = getWidth() / 2, cy = getHeight() / 2;
                        g2.fillPolygon(new Polygon(new int[]{cx - 5, cx + 5, cx}, new int[]{cy - 2, cy - 2, cy + 4}, 3));
                        g2.dispose();
                    }
                };
                seta.setContentAreaFilled(false);
                seta.setBorderPainted(false);
                seta.setFocusPainted(false);
                return seta;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, java.awt.Rectangle b, boolean foco) {
                g.setColor(Tema.ARDOSIA);
                g.fillRect(b.x, b.y, b.width, b.height);
            }
        });
        combo.setBackground(Tema.ARDOSIA);
        combo.setForeground(Tema.PERGAMINHO);
        combo.setBorder(BorderFactory.createLineBorder(Tema.LINHA));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean selecionado, boolean foco) {
                super.getListCellRendererComponent(list, value, index, selecionado, foco);
                setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                setBackground(selecionado && index >= 0 ? new java.awt.Color(0x3B3322) : Tema.ARDOSIA);
                setForeground(selecionado && index >= 0 ? Tema.LATAO : Tema.PERGAMINHO);
                list.setBackground(Tema.ARDOSIA);
                return this;
            }
        });
    }
}
