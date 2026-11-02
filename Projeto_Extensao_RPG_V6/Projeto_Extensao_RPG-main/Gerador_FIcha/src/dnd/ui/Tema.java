package dnd.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

/**
 * A identidade visual do programa: uma ficha de papel sobre a mesa de jogo.
 *
 * <p>A janela é o tampo de couro verde da mesa; os painéis são folhas de
 * pergaminho apoiadas em cima; o texto é tinta ferro-galha. Os títulos usam o
 * vermelho de rubrica com que os manuscritos antigos marcavam o começo de cada
 * parte, e o ouro velho aparece só na capitular, para não virar enfeite.
 *
 * <p>As fontes são as lógicas do Java ({@code Font.SERIF}), que existem em
 * qualquer instalação: no Linux caem em DejaVu Serif, no Windows em Times New
 * Roman. Assim nenhuma fonte precisa ser instalada junto com o programa.
 */
public final class Tema {

    // Tampo da mesa
    public static final Color MESA = new Color(0x1E2A21);
    public static final Color MESA_CLARA = new Color(0x31402F);

    // Papel
    public static final Color PAPEL = new Color(0xE9DDBE);
    public static final Color PAPEL_SOMBRA = new Color(0xD8C8A0);
    public static final Color PAPEL_VINCO = new Color(0xC3AE82);

    // Tinta
    public static final Color TINTA = new Color(0x2B2118);
    public static final Color TINTA_FRACA = new Color(0x6B5940);
    public static final Color RUBRICA = new Color(0x8A2B22);
    public static final Color OURO = new Color(0xB0802C);

    // Lacres
    public static final Color LACRE_ALIADO = new Color(0x3C5B3A);
    public static final Color LACRE_INIMIGO = new Color(0x8A2B22);

    // Tipografia: tudo serifado, como material impresso de RPG
    public static final Font TITULO = new Font(Font.SERIF, Font.BOLD, 32);
    public static final Font NOME_FICHA = new Font(Font.SERIF, Font.BOLD, 38);
    public static final Font CAPITULAR = new Font(Font.SERIF, Font.BOLD, 46);
    public static final Font SUBTITULO = new Font(Font.SERIF, Font.ITALIC, 18);
    public static final Font SECAO = new Font(Font.SERIF, Font.BOLD, 18);
    public static final Font ROTULO = new Font(Font.SERIF, Font.BOLD, 15);
    public static final Font CORPO = new Font(Font.SERIF, Font.PLAIN, 15);
    public static final Font CORPO_ITALICO = new Font(Font.SERIF, Font.ITALIC, 15);
    public static final Font ENTRADA = new Font(Font.SERIF, Font.PLAIN, 18);
    public static final Font ENTRADA_GRANDE = new Font(Font.SERIF, Font.PLAIN, 24);
    public static final Font NUMERO_GRANDE = new Font(Font.SERIF, Font.BOLD, 32);
    public static final Font NUMERO_MEDIO = new Font(Font.SERIF, Font.BOLD, 24);
    public static final Font NOTA = new Font(Font.SERIF, Font.ITALIC, 13);

    private Tema() {}

    /**
     * Mantém o visual igual em qualquer sistema.
     *
     * <p>O Nimbus é o único look and feel que acompanha todas as instalações do
     * Java e aceita ter as cores trocadas, então o programa não fica com cara
     * de Windows numa máquina e de GTK em outra.
     */
    public static void aplicarLookAndFeel() {
        try {
            UIManager.put("control", PAPEL);
            UIManager.put("info", PAPEL);
            UIManager.put("nimbusBase", new Color(0x7A6647));
            UIManager.put("nimbusBlueGrey", PAPEL_SOMBRA);
            UIManager.put("nimbusLightBackground", PAPEL);
            UIManager.put("nimbusFocus", RUBRICA);
            UIManager.put("nimbusSelectionBackground", PAPEL_VINCO);
            UIManager.put("nimbusSelectedText", TINTA);
            UIManager.put("nimbusDisabledText", TINTA_FRACA);
            UIManager.put("text", TINTA);
            UIManager.put("textForeground", TINTA);
            UIManager.put("ToolTip.background", PAPEL);
            UIManager.put("ToolTip.foreground", TINTA);

            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception e) {
            // Sem Nimbus o programa continua funcionando, só com o visual padrão.
            System.err.println("Visual personalizado indisponível: " + e.getMessage());
        }
    }

    public static JLabel rotulo(String texto, Font fonte, Color cor) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fonte);
        etiqueta.setForeground(cor);
        return etiqueta;
    }

    public static Border margem(int vertical, int horizontal) {
        return BorderFactory.createEmptyBorder(vertical, horizontal, vertical, horizontal);
    }

    public static Border margem(int cima, int esquerda, int baixo, int direita) {
        return BorderFactory.createEmptyBorder(cima, esquerda, baixo, direita);
    }

    /** Bônus sempre com sinal, como nas fichas de papel. */
    public static String sinal(int valor) {
        return String.format("%+d", valor);
    }
}
