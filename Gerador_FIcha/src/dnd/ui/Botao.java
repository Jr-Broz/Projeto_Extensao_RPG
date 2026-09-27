package dnd.ui;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Botão pintado à mão: principal (latão cheio) ou secundário (contorno). */
public class Botao extends JButton {
    private final boolean principal;
    private boolean sobre;

    public Botao(String texto, boolean principal) {
        super(texto);
        this.principal = principal;
        setFont(Tema.CORPO_NEG);
        setForeground(principal ? Tema.NOITE : Tema.LATAO);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setBorder(Tema.margem(10, 16));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { sobre = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { sobre = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth() - 1, h = getHeight() - 1;
        if (!isEnabled()) {
            g2.setColor(Tema.LINHA);
            g2.drawRoundRect(0, 0, w, h, 12, 12);
            setForeground(Tema.NEBLINA);
        } else if (principal) {
            g2.setColor(sobre ? Tema.LATAO.brighter() : Tema.LATAO);
            g2.fillRoundRect(0, 0, w, h, 12, 12);
            setForeground(Tema.NOITE);
        } else {
            if (sobre) {
                g2.setColor(new Color(0x2F2A20));
                g2.fillRoundRect(0, 0, w, h, 12, 12);
            }
            g2.setColor(Tema.LATAO);
            g2.drawRoundRect(0, 0, w, h, 12, 12);
            setForeground(Tema.LATAO);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
