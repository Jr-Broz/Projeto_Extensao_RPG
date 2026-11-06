package dnd.ui;

import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Botão desenhado como um carimbo de tinta: régua dupla em volta e fundo
 * chapado. O principal sai impresso em vermelho de rubrica; o secundário fica
 * só com o contorno, como um campo ainda a ser preenchido.
 *
 * <p>Ao ser pressionado o desenho inteiro anda um pixel, que é o jeito mais
 * simples de dar a sensação do carimbo encostando no papel.
 */
public class BotaoTinta extends JButton {

    private final boolean principal;
    private boolean sobOCursor;
    private boolean pressionado;

    public BotaoTinta(String texto, boolean principal) {
        super(texto);
        this.principal = principal;
        setFont(Tema.ROTULO.deriveFont(16f));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setBorder(Tema.margem(12, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setForeground(principal ? Tema.PAPEL : Tema.TINTA);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sobOCursor = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sobOCursor = false;
                pressionado = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressionado = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressionado = false;
                repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension tamanho = super.getPreferredSize();
        tamanho.height = Math.max(tamanho.height, 46);
        return tamanho;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int largura = getWidth() - 2;
        int altura = getHeight() - 2;
        int anda = pressionado ? 1 : 0;

        if (!isEnabled()) {
            g2.setColor(Tema.PAPEL_VINCO);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRect(1, 1, largura, altura);
            setForeground(Tema.TINTA_FRACA);
        } else if (principal) {
            g2.setColor(sobOCursor ? clarear(Tema.RUBRICA) : Tema.RUBRICA);
            g2.fillRect(1 + anda, 1 + anda, largura, altura);
            g2.setColor(new Color(0, 0, 0, 80));
            g2.setStroke(new BasicStroke(0.9f));
            g2.drawRect(5 + anda, 5 + anda, largura - 8, altura - 8);
            setForeground(Tema.PAPEL);
        } else {
            if (sobOCursor) {
                g2.setColor(Tema.PAPEL_SOMBRA);
                g2.fillRect(1 + anda, 1 + anda, largura, altura);
            }
            g2.setColor(Tema.TINTA);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(1 + anda, 1 + anda, largura, altura);
            g2.setStroke(new BasicStroke(0.8f));
            g2.drawRect(5 + anda, 5 + anda, largura - 8, altura - 8);
            setForeground(Tema.TINTA);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private static Color clarear(Color cor) {
        return new Color(Math.min(255, cor.getRed() + 26),
                Math.min(255, cor.getGreen() + 20),
                Math.min(255, cor.getBlue() + 18));
    }
}
