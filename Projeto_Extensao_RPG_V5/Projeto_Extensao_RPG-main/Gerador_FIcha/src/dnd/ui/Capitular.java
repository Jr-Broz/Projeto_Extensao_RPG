package dnd.ui;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A inicial iluminada do nome da personagem.
 *
 * <p>É a letra grande e decorada que abre capítulo nos manuscritos medievais:
 * um campo de tinta vermelha com filete de ouro, a letra em cor de pergaminho e
 * um ponto dourado em cada canto. Dentro da janela é o único lugar onde o ouro
 * aparece, justamente para ser o detalhe que o olho procura primeiro.
 */
public class Capitular extends JComponent {

    private static final int LADO = 62;

    private final char letra;

    public Capitular(String nome) {
        this.letra = primeiraLetra(nome);
        setPreferredSize(new Dimension(LADO, LADO));
        setMinimumSize(new Dimension(LADO, LADO));
        setMaximumSize(new Dimension(LADO, LADO));
    }

    /** Primeira letra que valha a pena ilustrar; cai em "?" se não houver nenhuma. */
    private static char primeiraLetra(String nome) {
        if (nome != null) {
            for (char c : nome.toCharArray()) {
                if (Character.isLetter(c)) {
                    return Character.toUpperCase(c);
                }
            }
        }
        return '?';
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int lado = Math.min(getWidth(), getHeight()) - 2;

        // Campo de tinta vermelha
        g2.setColor(Tema.RUBRICA);
        g2.fillRect(1, 1, lado, lado);

        // Filete de ouro por dentro e risco de tinta por fora
        g2.setColor(Tema.OURO);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawRect(6, 6, lado - 11, lado - 11);

        g2.setColor(Tema.TINTA);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRect(1, 1, lado, lado);

        // Um ponto de ouro em cada canto, o enfeite mínimo da capitular
        g2.setColor(Tema.OURO);
        int recuo = 11;
        for (int[] canto : new int[][]{{recuo, recuo}, {lado - recuo, recuo},
                {recuo, lado - recuo}, {lado - recuo, lado - recuo}}) {
            g2.fillOval(canto[0] - 2, canto[1] - 2, 4, 4);
        }

        // A letra
        g2.setFont(Tema.CAPITULAR);
        g2.setColor(Tema.PAPEL);
        FontMetrics medidas = g2.getFontMetrics();
        String texto = String.valueOf(letra);
        g2.drawString(texto,
                1 + (lado - medidas.stringWidth(texto)) / 2,
                1 + lado / 2 + medidas.getAscent() / 2 - 5);

        g2.dispose();
    }
}
