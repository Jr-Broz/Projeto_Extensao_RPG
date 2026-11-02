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
import javax.swing.text.DefaultCaret;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.List;

/** A folha da direita: a ficha gerada, como ela sairia impressa. */
public class PainelFicha extends Pergaminho {

    private static final int RECUO = Pergaminho.recuoDaMoldura();

    public PainelFicha() {
        super(new BorderLayout());
        setBorder(Tema.margem(RECUO + 18, RECUO + 20));
        mostrarVazio();
    }

    public void mostrarVazio() {
        removeAll();

        JPanel vazio = new JPanel();
        vazio.setOpaque(false);
        vazio.setLayout(new BoxLayout(vazio, BoxLayout.Y_AXIS));

        JLabel titulo = Tema.rotulo("Folha em branco", Tema.TITULO, Tema.TINTA);
        JLabel dica = Tema.rotulo(
                "Preencha o pedido ao lado, ou deixe tudo no sorteio, e mande rolar.",
                Tema.CORPO_ITALICO, Tema.TINTA_FRACA);

        Ornamento filete = new Ornamento();
        filete.setMaximumSize(new Dimension(300, 18));

        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        dica.setAlignmentX(Component.CENTER_ALIGNMENT);
        filete.setAlignmentX(Component.CENTER_ALIGNMENT);

        vazio.add(Box.createVerticalGlue());
        vazio.add(titulo);
        vazio.add(Box.createVerticalStrut(12));
        vazio.add(filete);
        vazio.add(Box.createVerticalStrut(12));
        vazio.add(dica);
        vazio.add(Box.createVerticalGlue());

        add(vazio, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void mostrar(Ficha ficha) {
        removeAll();

        Coluna coluna = new Coluna();
        coluna.adicionar(cabecalho(ficha), 12);
        coluna.adicionar(new Ornamento(), 14);
        coluna.adicionar(atributos(ficha), 18);
        coluna.adicionar(estatisticas(ficha), 18);
        coluna.adicionar(tracos(ficha), 18);
        coluna.adicionar(rolagens(ficha), 0);
        coluna.fecharPagina();

        JScrollPane rolagem = new JScrollPane(coluna);
        rolagem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        CamposDeFormulario.aplicar(rolagem);

        add(rolagem, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ---------- partes da folha ----------

    private JComponent cabecalho(Ficha ficha) {
        JPanel painel = new JPanel(new BorderLayout(6, 0));
        painel.setOpaque(false);

        // A capitular fica colada no alto do nome, como numa página de livro.
        JPanel inicial = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        inicial.setOpaque(false);
        inicial.add(new Capitular(ficha.nome()));
        painel.add(inicial, BorderLayout.WEST);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel nome = Tema.rotulo(restoDoNome(ficha.nome()), fonteDoNome(ficha.nome()), Tema.TINTA);
        String descricao = ficha.raca().nome() + " " + ficha.classe().nome().toLowerCase()
                + ", nível " + ficha.nivel();
        JLabel subtitulo = Tema.rotulo(descricao, Tema.SUBTITULO, Tema.TINTA_FRACA);
        nome.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(Box.createVerticalStrut(2));
        textos.add(nome);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);
        painel.add(textos, BorderLayout.CENTER);

        boolean inimigo = ficha.tipo() == TipoFicha.INIMIGO;
        SeloCera selo = new SeloCera(ficha.tipo().toString(),
                inimigo ? Tema.LACRE_INIMIGO : Tema.LACRE_ALIADO);
        JPanel cantoDoSelo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        cantoDoSelo.setOpaque(false);
        cantoDoSelo.add(selo);
        painel.add(cantoDoSelo, BorderLayout.EAST);

        return painel;
    }

    /** A primeira letra já está na capitular, então o rótulo mostra o resto. */
    private String restoDoNome(String nome) {
        int inicial = -1;
        for (int i = 0; i < nome.length(); i++) {
            if (Character.isLetter(nome.charAt(i))) {
                inicial = i;
                break;
            }
        }
        return inicial < 0 ? nome : nome.substring(inicial + 1);
    }

    /** Nome comprido encolhe a letra em vez de vazar para fora da folha. */
    private Font fonteDoNome(String nome) {
        if (nome.length() <= 18) {
            return Tema.NOME_FICHA;
        }
        return nome.length() <= 26
                ? Tema.NOME_FICHA.deriveFont(32f)
                : Tema.NOME_FICHA.deriveFont(26f);
    }

    private JComponent atributos(Ficha ficha) {
        JPanel grade = new JPanel(new GridLayout(1, 6, 8, 0));
        grade.setOpaque(false);

        List<Atributo> salvaguardas = ficha.classe().salvaguardas();
        for (Atributo atributo : Atributo.values()) {
            boolean principal = atributo == ficha.classe().atributoPrincipal();
            int racial = ficha.bonusRacial(atributo);
            String origem = racial == 0
                    ? "rolou " + ficha.valorBase(atributo)
                    : "rolou " + ficha.valorBase(atributo) + ", raça " + Tema.sinal(racial);

            grade.add(new CaixaAtributo(
                    atributo.nome(),
                    ficha.valorFinal(atributo),
                    Tema.sinal(ficha.modificador(atributo)),
                    origem,
                    principal,
                    salvaguardas.contains(atributo)));
        }
        return grade;
    }

    private JComponent estatisticas(Ficha ficha) {
        JPanel linha = new JPanel(new GridLayout(1, 5, 8, 0));
        linha.setOpaque(false);
        linha.add(campo("Pontos de vida", String.valueOf(ficha.pontosDeVida())));
        linha.add(campo("Armadura (CA)", String.valueOf(ficha.classeArmadura())));
        linha.add(campo("Deslocamento", formatarDeslocamento(ficha.raca().deslocamento())));
        linha.add(campo("Proficiência", Tema.sinal(ficha.bonusProficiencia())));
        linha.add(campo("Dado de vida", "d" + ficha.classe().dadoVida()));
        return linha;
    }

    private String formatarDeslocamento(double metros) {
        return metros % 1 == 0
                ? String.format("%.0f m", metros)
                : String.format("%.1f m", metros).replace('.', ',');
    }

    /** Valor dentro de um quadro de régua dupla, com o rótulo embaixo. */
    private JComponent campo(String rotulo, String valor) {
        JPanel quadro = new Quadro(new BorderLayout(0, 2), null);
        quadro.setBorder(Tema.margem(11, 10));

        JLabel numero = Tema.rotulo(valor, Tema.NUMERO_MEDIO, Tema.TINTA);
        numero.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel legenda = Tema.rotulo(rotulo, Tema.NOTA, Tema.TINTA_FRACA);
        legenda.setHorizontalAlignment(SwingConstants.CENTER);

        quadro.add(numero, BorderLayout.CENTER);
        quadro.add(legenda, BorderLayout.SOUTH);
        return quadro;
    }

    private JComponent tracos(Ficha ficha) {
        JPanel painel = new JPanel(new GridLayout(1, 2, 14, 0));
        painel.setOpaque(false);
        painel.add(lista("Vantagens", ficha.raca().vantagens()));

        List<String> desvantagens = ficha.raca().desvantagens();
        painel.add(lista("Desvantagens", desvantagens.isEmpty()
                ? List.of("Nenhuma desvantagem racial")
                : desvantagens));
        return painel;
    }

    private JComponent lista(String titulo, List<String> itens) {
        JPanel quadro = new Quadro(new BorderLayout(), titulo);

        StringBuilder texto = new StringBuilder();
        for (String item : itens) {
            texto.append("❧  ").append(item).append('\n');
        }
        quadro.add(texto(texto.toString().trim(), Tema.CORPO), BorderLayout.CENTER);
        return quadro;
    }

    private JComponent rolagens(Ficha ficha) {
        JPanel quadro = new Quadro(new BorderLayout(), "Como os atributos saíram");
        quadro.add(texto(String.join("\n", ficha.detalhesRolagem()), Tema.CORPO_ITALICO),
                BorderLayout.CENTER);
        return quadro;
    }

    private JTextArea texto(String conteudo, Font fonte) {
        JTextArea area = new JTextArea(conteudo) {
            @Override
            public Dimension getPreferredSize() {
                int largura = getWidth();
                if (largura <= 0) {
                    return super.getPreferredSize();
                }
                // Mede a altura real já com as linhas quebradas nesta largura.
                setSize(largura, Short.MAX_VALUE);
                Dimension medida = super.getPreferredSize();
                medida.width = largura;
                return medida;
            }
        };
        if (area.getCaret() instanceof DefaultCaret cursor) {
            // Sem isto, medir o texto faz o cursor pedir rolagem e a folha sobe sozinha.
            cursor.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }
        area.setFont(fonte);
        area.setForeground(Tema.TINTA);
        area.setBackground(new Color(0, 0, 0, 0));
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder());
        return area;
    }

    /**
     * Quadro de régua, com a legenda atravessando a linha de cima.
     *
     * <p>A régua é desenhada em dois pedaços, abrindo um vão onde o título
     * passa. É assim que os formulários impressos antigos nomeiam uma seção, e
     * fica bem mais interessante do que um título solto dentro da caixa.
     */
    private static class Quadro extends JPanel {

        private static final int ALTURA_DA_LEGENDA = 11;

        private final String titulo;

        Quadro(LayoutManager layout, String titulo) {
            super(layout);
            this.titulo = titulo;
            setOpaque(false);
            int topo = titulo == null ? 0 : ALTURA_DA_LEGENDA;
            setBorder(Tema.margem(topo + 16, 16, 15, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int largura = getWidth() - 1;
            int altura = getHeight() - 1;

            if (titulo == null) {
                g2.setColor(Tema.TINTA);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRect(0, 0, largura, altura);
                g2.setColor(Tema.TINTA_FRACA);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRect(4, 4, largura - 8, altura - 8);
                g2.dispose();
                super.paintComponent(g);
                return;
            }

            int topo = ALTURA_DA_LEGENDA;
            g2.setFont(Tema.SECAO);
            FontMetrics medidas = g2.getFontMetrics();
            int inicioDoTexto = 22;
            int vao = medidas.stringWidth(titulo) + 16;

            g2.setColor(Tema.TINTA);
            g2.setStroke(new BasicStroke(1.5f));
            // Régua de cima em dois pedaços, com o vão da legenda no meio
            g2.drawLine(0, topo, inicioDoTexto - 8, topo);
            g2.drawLine(inicioDoTexto + vao - 8, topo, largura, topo);
            g2.drawLine(0, topo, 0, altura);
            g2.drawLine(largura, topo, largura, altura);
            g2.drawLine(0, altura, largura, altura);

            g2.setColor(Tema.RUBRICA);
            g2.drawString(titulo, inicioDoTexto, topo + medidas.getAscent() / 2 + 1);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Faixa de largura total e altura só do necessário.
     *
     * <p>A altura é consultada na hora de posicionar, e não no momento em que o
     * componente entra na coluna: textos que quebram em várias linhas só sabem
     * a própria altura depois de conhecerem a largura disponível.
     */
    private static class Linha extends JPanel {
        Linha(JComponent conteudo) {
            super(new BorderLayout());
            setOpaque(false);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            add(conteudo, BorderLayout.CENTER);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }
    }

    /** Coluna vertical que acompanha a largura da área de rolagem. */
    private static class Coluna extends JPanel implements Scrollable {
        Coluna() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        void adicionar(JComponent componente, int espacoDepois) {
            add(new Linha(componente));
            if (espacoDepois > 0) {
                add(Box.createVerticalStrut(espacoDepois));
                // Uma folga elástica em cada vão: se a folha for mais alta que o
                // conteúdo, a sobra se reparte entre as seções em vez de virar um
                // vazio no pé da página.
                add(Box.createVerticalGlue());
            }
        }

        /** Fecha a página com uma folga maior, para o conteúdo não colar no rodapé. */
        void fecharPagina() {
            add(Box.createVerticalGlue());
            add(Box.createVerticalGlue());
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visivel, int eixo, int sentido) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visivel, int eixo, int sentido) {
            return 140;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            // Conteúdo mais curto que a folha: ocupa a folha inteira, e as
            // folgas elásticas distribuem a sobra. Mais comprido: volta a rolar.
            Component janela = getParent();
            return janela != null && getPreferredSize().height < janela.getHeight();
        }
    }
}
