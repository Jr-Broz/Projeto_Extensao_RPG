package dnd;

import dnd.dados.RepositorioLocal;
import dnd.ui.JanelaPrincipal;
import dnd.ui.Tema;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        Tema.aplicarLookAndFeel();
        SwingUtilities.invokeLater(() -> new JanelaPrincipal(new RepositorioLocal()).setVisible(true));
    }
}
