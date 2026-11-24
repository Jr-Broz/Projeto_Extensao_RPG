package wow;

import wow.dados.RepositorioArquivo;
import wow.dados.RepositorioDados;
import wow.dados.RepositorioLocal;
import wow.ui.JanelaPrincipal;
import wow.ui.Tema;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {

    // se existir um racas.txt na pasta de onde o programa foi aberto, as raças
    // vem dele. senao vem das q estao escritas no codigo
    private static final Path ARQUIVO_DE_RACAS = Path.of("racas.txt");

    public static void main(String[] args) {

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        RepositorioDados repositorio = escolherRepositorio();

        Tema.aplicarLookAndFeel();
        SwingUtilities.invokeLater(() -> new JanelaPrincipal(repositorio).setVisible(true));
    }

    // o programa funciona sem o arquivo, entao se ele nao existir ou estiver
    // quebrado a gente avisa no terminal e segue com as raças do codigo, em
    // vez de nao abrir
    private static RepositorioDados escolherRepositorio() {
        if (!Files.exists(ARQUIVO_DE_RACAS)) {
            return new RepositorioLocal();
        }
        try {
            RepositorioDados doArquivo = new RepositorioArquivo(ARQUIVO_DE_RACAS);
            System.out.println("Raças carregadas de " + ARQUIVO_DE_RACAS.toAbsolutePath());
            return doArquivo;
        } catch (IOException erro) {
            System.err.println("Nao consegui ler o " + ARQUIVO_DE_RACAS + ": " + erro.getMessage());
            System.err.println("Vou usar as raças que estao no codigo.");
            return new RepositorioLocal();
        }
    }
}
