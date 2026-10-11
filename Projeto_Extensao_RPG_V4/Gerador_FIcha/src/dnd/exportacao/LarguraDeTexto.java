package dnd.exportacao;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

final class LarguraDeTexto {

    private static final FontRenderContext CONTEXTO =
            new FontRenderContext(new AffineTransform(), true, true);

    private LarguraDeTexto() {}

    static double medir(String texto, DocumentoPdf.Fonte fonte, double tamanho) {
        return fonteJava(fonte, tamanho).getStringBounds(texto, CONTEXTO).getWidth();
    }

    /** Divide o texto em linhas que cabem na largura pedida, quebrando entre palavras. */
    static List<String> quebrar(String texto, double largura, DocumentoPdf.Fonte fonte,
                                double tamanho) {
        List<String> linhas = new ArrayList<>();
        StringBuilder atual = new StringBuilder();

        for (String palavra : texto.split("\\s+")) {
            String tentativa = atual.isEmpty() ? palavra : atual + " " + palavra;
            if (medir(tentativa, fonte, tamanho) <= largura || atual.isEmpty()) {
                atual.setLength(0);
                atual.append(tentativa);
            } else {
                linhas.add(atual.toString());
                atual.setLength(0);
                atual.append(palavra);
            }
        }
        if (!atual.isEmpty()) {
            linhas.add(atual.toString());
        }
        return linhas;
    }

    private static Font fonteJava(DocumentoPdf.Fonte fonte, double tamanho) {
        int estilo = switch (fonte) {
            case NEGRITO -> Font.BOLD;
            case ITALICO -> Font.ITALIC;
            default -> Font.PLAIN;
        };
        return new Font(Font.SERIF, estilo, 100).deriveFont((float) (tamanho));
    }
}
