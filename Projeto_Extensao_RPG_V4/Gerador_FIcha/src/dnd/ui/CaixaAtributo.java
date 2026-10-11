package dnd.ui;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A caixa de atributo da ficha de papel: o nome em cima, o valor dentro de um
 * quadro de régua dupla e o modificador num círculo pendurado na base.
 *
 * <p>É o desenho que todo jogador reconhece de longe, e por isso vale mais aqui
 * do que qualquer cartão genérico de aplicativo.
 */
public class CaixaAtributo extends JComponent {

    private static final int DIAMETRO_CIRCULO = 44;

    private final String nomeAtributo;
    private final int valor;
    private final String modificador;
    private final String origem;
    private final boolean destacado;
    private final boolean proficienteEmSalvaguarda;

    public CaixaAtributo(String nomeAtributo, int valor, String modificador, String origem,
                         boolean destacado, boolean proficienteEmSalvaguarda) {
        this.nomeAtributo = nomeAtributo;
        this.valor = valor;
        this.modificador = modificador;
        this.origem = origem;
        this.destacado = destacado;
        this.proficienteEmSalvaguarda = proficienteEmSalvaguarda;
        setPreferredSize(new Dimension(126, 156));
        setMinimumSize(new Dimension(100, 150));
        setToolTipText(nomeAtributo + ": " + valor + " (" + origem + ")");
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int largura = getWidth();
        int centroX = largura / 2;
        Color tinta = destacado ? Tema.RUBRICA : Tema.TINTA;
        float traco = destacado ? 2.2f : 1.4f;

        // Nome do atributo
        g2.setFont(Tema.ROTULO);
        g2.setColor(tinta);
        FontMetrics medidas = g2.getFontMetrics();
        int baseDoNome = medidas.getAscent();
        g2.drawString(nomeAtributo, centroX - medidas.stringWidth(nomeAtributo) / 2, baseDoNome);

        // Quadro do valor, com a régua dupla desenhada por dentro
        int topo = baseDoNome + 8;
        int alturaQuadro = 68;
        int margem = 5;

        g2.setStroke(new BasicStroke(traco));
        g2.drawRect(margem, topo, largura - margem * 2, alturaQuadro);
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawRect(margem + 4, topo + 4, largura - margem * 2 - 8, alturaQuadro - 8);

        // Valor do atributo
        g2.setFont(Tema.NUMERO_GRANDE);
        g2.setColor(Tema.TINTA);
        medidas = g2.getFontMetrics();
        String texto = String.valueOf(valor);
        g2.drawString(texto, centroX - medidas.stringWidth(texto) / 2,
                topo + alturaQuadro / 2 + medidas.getAscent() / 2 - 6);

        // Círculo do modificador, encostado na base do quadro
        int centroY = topo + alturaQuadro + 2;
        g2.setColor(Tema.PAPEL);
        g2.fillOval(centroX - DIAMETRO_CIRCULO / 2, centroY - DIAMETRO_CIRCULO / 2,
                DIAMETRO_CIRCULO, DIAMETRO_CIRCULO);
        g2.setColor(tinta);
        g2.setStroke(new BasicStroke(traco));
        g2.drawOval(centroX - DIAMETRO_CIRCULO / 2, centroY - DIAMETRO_CIRCULO / 2,
                DIAMETRO_CIRCULO, DIAMETRO_CIRCULO);

        g2.setFont(Tema.NUMERO_MEDIO);
        g2.setColor(Tema.TINTA);
        medidas = g2.getFontMetrics();
        g2.drawString(modificador, centroX - medidas.stringWidth(modificador) / 2,
                centroY + medidas.getAscent() / 2 - 3);

        // Rodapé: de onde veio o valor e se a salvaguarda é proficiente
        int rodape = centroY + DIAMETRO_CIRCULO / 2 + 16;
        g2.setFont(Tema.NOTA);
        medidas = g2.getFontMetrics();
        g2.setColor(Tema.TINTA_FRACA);
        g2.drawString(origem, centroX - medidas.stringWidth(origem) / 2, rodape);

        if (proficienteEmSalvaguarda) {
            g2.setColor(Tema.RUBRICA);
            String marca = "✦ salvaguarda";
            g2.drawString(marca, centroX - medidas.stringWidth(marca) / 2,
                    rodape + medidas.getHeight());
        }

        g2.dispose();
    }
}
