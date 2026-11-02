package dnd.ui;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 * Deixa os campos do formulário com cara de linha preenchida à mão: fundo de
 * papel, texto em tinta e uma régua embaixo no lugar da moldura cinza que o
 * sistema desenha por padrão.
 */
public final class CamposDeFormulario {

    private static final String PROPRIEDADE_DICA = "dica";

    private CamposDeFormulario() {}

    /** Campo de texto escrito sobre uma linha, como num formulário de papel. */
    public static void aplicar(JTextField campo) {
        campo.setFont(Tema.ENTRADA);
        campo.setForeground(Tema.TINTA);
        campo.setCaretColor(Tema.RUBRICA);
        campo.setOpaque(false);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new ReguaInferior(), Tema.margem(5, 2, 7, 2)));
        campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, 40));
    }

    /**
     * O mesmo campo, com uma dica apagada enquanto estiver vazio — do jeito que
     * um formulário impresso traz a instrução em cinza dentro da própria linha.
     */
    public static void aplicar(JTextField campo, String dica) {
        aplicar(campo);
        campo.putClientProperty(PROPRIEDADE_DICA, dica);
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evento) {
                campo.repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evento) {
                campo.repaint();
            }
        });
    }

    /** Campo principal da folha: a mesma linha, com letra de título. */
    public static void destacar(JTextField campo, String dica) {
        aplicar(campo, dica);
        campo.setFont(Tema.ENTRADA_GRANDE);
        campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, 52));
    }

    /**
     * Desenha a dica de um campo preparado aqui. Precisa ser chamado de dentro
     * do {@code paintComponent} do campo, porque só ali há o contexto gráfico
     * certo para escrever por cima do papel.
     */
    public static void desenharDica(JTextField campo, Graphics g) {
        Object dica = campo.getClientProperty(PROPRIEDADE_DICA);
        if (dica == null || !campo.getText().isEmpty()) {
            // A dica fica à vista mesmo com o cursor no campo: ela some quando
            // o usuário escreve, não quando ele clica.
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(Tema.CORPO_ITALICO.deriveFont(15f));
        g2.setColor(Tema.TINTA_FRACA);
        Insets dentro = campo.getInsets();
        g2.drawString(dica.toString(), dentro.left + 2, campo.getHeight() - dentro.bottom - 8);
        g2.dispose();
    }

    /** Lista suspensa com seta de tinta e sem o relevo cinza do sistema. */
    public static void aplicar(JComboBox<?> combo) {
        combo.setFont(Tema.ENTRADA);
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton seta = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Tema.RUBRICA);
                        int centroX = getWidth() / 2;
                        int centroY = getHeight() / 2;
                        g2.fillPolygon(new Polygon(
                                new int[]{centroX - 6, centroX + 6, centroX},
                                new int[]{centroY - 3, centroY - 3, centroY + 5}, 3));
                        g2.dispose();
                    }
                };
                seta.setContentAreaFilled(false);
                seta.setBorderPainted(false);
                seta.setFocusPainted(false);
                return seta;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle area, boolean temFoco) {
                // O fundo é o próprio pergaminho; aqui só entra a régua de baixo.
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Tema.TINTA_FRACA);
                g2.setStroke(new BasicStroke(1.1f));
                int baseY = area.y + area.height - 1;
                g2.drawLine(area.x, baseY, area.x + area.width, baseY);
                g2.dispose();
            }
        });
        combo.setOpaque(false);
        combo.setForeground(Tema.TINTA);
        combo.setBorder(Tema.margem(3, 2, 3, 2));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 40));

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean selecionado, boolean temFoco) {
                super.getListCellRendererComponent(lista, valor, indice, selecionado, temFoco);
                setFont(Tema.CORPO.deriveFont(16f));
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                boolean naLista = indice >= 0;
                setBackground(selecionado && naLista ? Tema.PAPEL_VINCO : Tema.PAPEL);
                setForeground(Tema.TINTA);
                lista.setBackground(Tema.PAPEL);
                return this;
            }
        });
    }

    /** Barra de rolagem discreta: um vinco de papel no lugar do controle do sistema. */
    public static void aplicar(JScrollPane rolagem) {
        rolagem.setOpaque(false);
        rolagem.getViewport().setOpaque(false);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.setViewportBorder(null);
        rolagem.getVerticalScrollBar().setOpaque(false);
        rolagem.getVerticalScrollBar().setPreferredSize(new Dimension(9, 0));
        rolagem.getVerticalScrollBar().setUnitIncrement(18);
        rolagem.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected JButton createIncreaseButton(int orientacao) {
                return semBotao();
            }

            @Override
            protected JButton createDecreaseButton(int orientacao) {
                return semBotao();
            }

            private JButton semBotao() {
                JButton vazio = new JButton();
                vazio.setPreferredSize(new Dimension(0, 0));
                vazio.setBorderPainted(false);
                vazio.setContentAreaFilled(false);
                return vazio;
            }

            @Override
            protected void paintTrack(Graphics g, JComponent componente, Rectangle area) {
                // A trilha é o próprio pergaminho.
            }

            @Override
            protected void paintThumb(Graphics g, JComponent componente, Rectangle area) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.PAPEL_VINCO);
                g2.fillRoundRect(area.x + 2, area.y, area.width - 4, area.height, 6, 6);
                g2.dispose();
            }
        });
    }

    /** Linha de tinta sob o campo, no lugar da borda padrão do sistema. */
    private static class ReguaInferior extends AbstractBorder {
        @Override
        public void paintBorder(Component componente, Graphics g, int x, int y,
                                int largura, int altura) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Tema.TINTA_FRACA);
            g2.setStroke(new BasicStroke(1.1f));
            g2.drawLine(x, y + altura - 1, x + largura, y + altura - 1);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component componente) {
            return new Insets(0, 0, 2, 0);
        }
    }

    /** Cor de fundo das listas suspensas, para quem precisar casar com elas. */
    public static Color fundoDaLista() {
        return Tema.PAPEL;
    }
}
