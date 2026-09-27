package dnd.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

public final class Tema {
    public static final Color NOITE      = new Color(0x151B26); // fundo da janela
    public static final Color GRIMORIO   = new Color(0x1E2635); // painéis
    public static final Color ARDOSIA    = new Color(0x283246); // cartões internos
    public static final Color LINHA      = new Color(0x3A4760);
    public static final Color LATAO      = new Color(0xC9A45C); // acento
    public static final Color PERGAMINHO = new Color(0xE8E0CC); // texto
    public static final Color NEBLINA    = new Color(0x8E97A8); // texto secundário
    public static final Color ALIADO     = new Color(0x5FA8A0);
    public static final Color INIMIGO    = new Color(0xC4554D);

    public static final Font NOME       = new Font(Font.SERIF, Font.BOLD, 30);
    public static final Font SUBTITULO  = new Font(Font.SERIF, Font.ITALIC, 16);
    public static final Font SECAO      = new Font(Font.SERIF, Font.BOLD, 17);
    public static final Font NUMERO     = new Font(Font.SERIF, Font.BOLD, 28);
    public static final Font NUMERO_MED = new Font(Font.SERIF, Font.BOLD, 22);
    public static final Font CORPO      = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font CORPO_NEG  = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    public static final Font PEQUENA    = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

    private Tema() {}

    /** Nimbus com paleta escura: deixa combos, spinners e rolagens coerentes. */
    public static void aplicarLookAndFeel() {
        try {
            UIManager.put("control", GRIMORIO);
            UIManager.put("info", ARDOSIA);
            UIManager.put("nimbusBase", new Color(0x121826));
            UIManager.put("nimbusBlueGrey", new Color(0x1C2330));
            UIManager.put("nimbusLightBackground", ARDOSIA);
            UIManager.put("nimbusFocus", LATAO);
            UIManager.put("nimbusSelectionBackground", new Color(0x6B5A36));
            UIManager.put("nimbusSelectedText", PERGAMINHO);
            UIManager.put("nimbusDisabledText", NEBLINA);
            UIManager.put("text", PERGAMINHO);
            UIManager.put("textForeground", PERGAMINHO);
            UIManager.put("ToolTip.background", ARDOSIA);
            UIManager.put("ToolTip.foreground", PERGAMINHO);
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus indisponível, usando o visual padrão: " + e.getMessage());
        }
    }

    public static JLabel rotulo(String texto, Font fonte, Color cor) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte);
        l.setForeground(cor);
        return l;
    }

    public static Border margem(int v, int h) {
        return BorderFactory.createEmptyBorder(v, h, v, h);
    }

    public static String sinal(int valor) {
        return String.format("%+d", valor);
    }
}