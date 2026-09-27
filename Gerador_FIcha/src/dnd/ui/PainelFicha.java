package dnd.ui;

import dnd.model.Atributo;
import dnd.model.Ficha;
import dnd.model.TipoFicha;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.List;

/** Área da direita: mostra a ficha gerada. */
public class PainelFicha extends PainelArredondado {

    public PainelFicha() {
        super(new BorderLayout(), Tema.GRIMORIO, 20);
        setBorder(Tema.margem(22, 24));
        mostrarVazio();
    }

    public void mostrarVazio() {
        removeAll();
        JPanel vazio = new JPanel();
        vazio.setOpaque(false);
        vazio.setLayout(new BoxLayout(vazio, BoxLayout.Y_AXIS));
        JLabel titulo = Tema.rotulo("Nenhuma ficha ainda", Tema.NOME, Tema.PERGAMINHO);
        JLabel dica = Tema.rotulo("Escolha raça, classe e nível ao lado, ou deixe tudo no aleatório, e clique em Gerar ficha.",
                Tema.CORPO, Tema.NEBLINA);
        titulo.setAlignmentX(CENTER_ALIGNMENT);
        dica.setAlignmentX(CENTER_ALIGNMENT);
        vazio.add(Box.createVerticalGlue());
        vazio.add(titulo);
        vazio.add(Box.createVerticalStrut(8));
        vazio.add(dica);
        vazio.add(Box.createVerticalGlue());
        add(vazio, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void mostrar(Ficha f) {
        removeAll();
        Coluna col = new Coluna();
        col.adicionar(cabecalho(f), 18);
        col.adicionar(atributos(f), 18);
        col.adicionar(estatisticas(f), 18);
        col.adicionar(tracos(f), 18);
        col.adicionar(rolagens(f), 0);

        JScrollPane scroll = new JScrollPane(col);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ---------- seções ----------

    private JComponent cabecalho(Ficha f) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Tema.rotulo(f.nome(), Tema.NOME, Tema.PERGAMINHO));
        textos.add(Box.createVerticalStrut(2));
        String sub = f.raca().nome() + " " + f.classe().nome().toLowerCase() + ", nível " + f.nivel();
        textos.add(Tema.rotulo(sub, Tema.SUBTITULO, Tema.NEBLINA));
        p.add(textos, BorderLayout.CENTER);

        Color corTipo = f.tipo() == TipoFicha.INIMIGO ? Tema.INIMIGO : Tema.ALIADO;
        PainelArredondado selo = new PainelArredondado(new FlowLayout(FlowLayout.CENTER, 0, 0), Tema.NOITE, 14);
        selo.setCorBorda(corTipo);
        selo.setBorder(Tema.margem(6, 14));
        selo.add(Tema.rotulo(f.tipo().toString(), Tema.CORPO_NEG, corTipo));
        JPanel envoltorio = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        envoltorio.setOpaque(false);
        envoltorio.add(selo);
        p.add(envoltorio, BorderLayout.EAST);
        return p;
    }

    private JComponent atributos(Ficha f) {
        JPanel grade = new JPanel(new GridLayout(1, 6, 10, 0));
        grade.setOpaque(false);
        List<Atributo> salvaguardas = f.classe().salvaguardas();

        for (Atributo a : Atributo.values()) {
            boolean principal = a == f.classe().atributoPrincipal();
            PainelArredondado cartao = new PainelArredondado(new BorderLayout(0, 4), Tema.ARDOSIA, 16);
            cartao.setBorder(Tema.margem(10, 6));
            if (principal) cartao.setCorBorda(Tema.LATAO);

            JLabel nome = Tema.rotulo(a.nome(), Tema.CORPO_NEG, principal ? Tema.LATAO : Tema.PERGAMINHO);
            nome.setHorizontalAlignment(SwingConstants.CENTER);
            cartao.add(nome, BorderLayout.NORTH);

            JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            centro.setOpaque(false);
            centro.add(new SeloD20(f.valorFinal(a), principal ? Tema.LATAO : Tema.LINHA.brighter()));
            cartao.add(centro, BorderLayout.CENTER);

            JPanel rodape = new JPanel();
            rodape.setOpaque(false);
            rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
            JLabel mod = Tema.rotulo(Tema.sinal(f.modificador(a)), Tema.NUMERO_MED.deriveFont(18f), Tema.PERGAMINHO);
            mod.setToolTipText("Modificador de " + a.nome());
            int racial = f.bonusRacial(a);
            String origem = "base " + f.valorBase(a) + (racial != 0 ? ", raça " + Tema.sinal(racial) : "");
            JLabel detalhe = Tema.rotulo(origem, Tema.PEQUENA, Tema.NEBLINA);
            mod.setAlignmentX(CENTER_ALIGNMENT);
            detalhe.setAlignmentX(CENTER_ALIGNMENT);
            rodape.add(mod);
            rodape.add(detalhe);
            if (salvaguardas.contains(a)) {
                JLabel ts = Tema.rotulo("salvaguarda", Tema.PEQUENA, Tema.LATAO);
                ts.setAlignmentX(CENTER_ALIGNMENT);
                rodape.add(ts);
            }
            cartao.add(rodape, BorderLayout.SOUTH);
            grade.add(cartao);
        }
        return grade;
    }

    private JComponent estatisticas(Ficha f) {
        JPanel linha = new JPanel(new GridLayout(1, 5, 10, 0));
        linha.setOpaque(false);
        String desl = (f.raca().deslocamento() % 1 == 0)
                ? String.format("%.0f m", f.raca().deslocamento())
                : String.format("%.1f m", f.raca().deslocamento()).replace('.', ',');
        linha.add(estatistica("Pontos de vida", String.valueOf(f.pontosDeVida())));
        linha.add(estatistica("Classe de armadura", String.valueOf(f.classeArmadura())));
        linha.add(estatistica("Deslocamento", desl));
        linha.add(estatistica("Proficiência", Tema.sinal(f.bonusProficiencia())));
        linha.add(estatistica("Dado de vida", "d" + f.classe().dadoVida()));
        return linha;
    }

    private JComponent estatistica(String rotulo, String valor) {
        PainelArredondado c = new PainelArredondado(new BorderLayout(), Tema.ARDOSIA, 14);
        c.setBorder(Tema.margem(10, 12));
        c.add(Tema.rotulo(valor, Tema.NUMERO_MED, Tema.PERGAMINHO), BorderLayout.CENTER);
        c.add(Tema.rotulo(rotulo, Tema.PEQUENA, Tema.NEBLINA), BorderLayout.SOUTH);
        return c;
    }

    private JComponent tracos(Ficha f) {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setOpaque(false);
        p.add(listaTracos("Vantagens", f.raca().vantagens(), Tema.ALIADO));
        List<String> desv = f.raca().desvantagens();
        p.add(listaTracos("Desvantagens", desv.isEmpty() ? List.of("Nenhuma desvantagem racial") : desv, Tema.INIMIGO));
        return p;
    }

    private JComponent listaTracos(String titulo, List<String> itens, Color cor) {
        PainelArredondado c = new PainelArredondado(new BorderLayout(0, 8), Tema.ARDOSIA, 16);
        c.setBorder(Tema.margem(12, 14));
        JLabel t = Tema.rotulo(titulo, Tema.SECAO, cor);
        c.add(t, BorderLayout.NORTH);
        StringBuilder sb = new StringBuilder();
        for (String item : itens) sb.append("•  ").append(item).append('\n');
        c.add(areaTexto(sb.toString().trim(), Tema.CORPO, Tema.ARDOSIA), BorderLayout.CENTER);
        return c;
    }

    private JComponent rolagens(Ficha f) {
        PainelArredondado c = new PainelArredondado(new BorderLayout(0, 8), Tema.NOITE, 16);
        c.setCorBorda(Tema.LINHA);
        c.setBorder(Tema.margem(12, 14));
        c.add(Tema.rotulo("Como os atributos saíram", Tema.SECAO, Tema.PERGAMINHO), BorderLayout.NORTH);
        c.add(areaTexto(String.join("\n", f.detalhesRolagem()), new Font(Font.MONOSPACED, Font.PLAIN, 12), Tema.NOITE),
                BorderLayout.CENTER);
        return c;
    }

    private JTextArea areaTexto(String texto, Font fonte, Color fundo) {
        JTextArea area = new JTextArea(texto);
        area.setFont(fonte);
        area.setForeground(Tema.PERGAMINHO);
        area.setBackground(fundo); // o Nimbus pinta o fundo mesmo sem opacidade
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder());
        return area;
    }

    /** Coluna vertical que acompanha a largura da área de rolagem. */
    private static class Coluna extends JPanel implements Scrollable {
        Coluna() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        void adicionar(JComponent c, int espacoDepois) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
            add(c);
            if (espacoDepois > 0) add(Box.createVerticalStrut(espacoDepois));
        }

        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 120; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
