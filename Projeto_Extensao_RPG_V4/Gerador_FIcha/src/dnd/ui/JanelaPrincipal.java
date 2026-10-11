package dnd.ui;

import dnd.dados.RepositorioDados;
import dnd.exportacao.FichaEmPdf;
import dnd.model.Classe;
import dnd.model.Ficha;
import dnd.model.Raca;
import dnd.model.TipoFicha;
import dnd.service.GeradorAtributos;
import dnd.service.GeradorFicha;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.List;
import java.util.Random;

/** A janela: o pedido numa folha à esquerda, a ficha pronta na folha à direita. */
public class JanelaPrincipal extends JFrame {

    private static final int RECUO = Pergaminho.recuoDaMoldura();

    private final RepositorioDados repositorio;
    private final GeradorFicha gerador;

    /** O campo do nome desenha a própria dica enquanto estiver vazio. */
    private final JTextField campoNome = new JTextField() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            CamposDeFormulario.desenharDica(this, g);
        }
    };

    private final JComboBox<Opcao<Raca>> comboRaca = new JComboBox<>();
    private final JComboBox<Opcao<Classe>> comboClasse = new JComboBox<>();
    private final JComboBox<Integer> comboNivel = new JComboBox<>();
    private final JRadioButton radioAliado = new JRadioButton("Aliado", true);
    private final JRadioButton radioInimigo = new JRadioButton("Inimigo");
    private final JComboBox<GeradorAtributos.Metodo> comboMetodo =
            new JComboBox<>(GeradorAtributos.Metodo.values());
    private final JCheckBox checkPriorizar =
            new JCheckBox("Maior valor no atributo da classe", true);
    private final BotaoTinta botaoGerar = new BotaoTinta("Rolar a ficha", true);
    private final BotaoTinta botaoRolarDeNovo = new BotaoTinta("Rolar atributos de novo", false);
    private final BotaoTinta botaoSalvarPdf = new BotaoTinta("Salvar em PDF para imprimir", false);
    private final JLabel status = Tema.rotulo("Abrindo o catálogo…", Tema.NOTA, Tema.TINTA_FRACA);
    private final PainelFicha painelFicha = new PainelFicha();

    private Ficha fichaAtual;

    public JanelaPrincipal(RepositorioDados repositorio) {
        super("Forja de Fichas — gerador de D&D 5e");
        this.repositorio = repositorio;
        this.gerador = new GeradorFicha(repositorio);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mesa = new Mesa();
        mesa.setLayout(new BorderLayout(20, 0));
        mesa.setBorder(Tema.margem(20, 20));
        mesa.add(folhaDoPedido(), BorderLayout.WEST);
        mesa.add(painelFicha, BorderLayout.CENTER);
        setContentPane(mesa);

        botaoGerar.setEnabled(false);
        botaoRolarDeNovo.setEnabled(false);
        botaoSalvarPdf.setEnabled(false);
        botaoGerar.addActionListener(evento -> gerar());
        botaoRolarDeNovo.addActionListener(evento -> rolarDeNovo());
        botaoSalvarPdf.addActionListener(evento -> salvarEmPdf());
        getRootPane().setDefaultButton(botaoGerar);

        setMinimumSize(new Dimension(1024, 640));
        ajustarAoMonitor(1340, 880);
        setLocationRelativeTo(null);
        carregarCatalogo();
    }

    /**
     * Abre num tamanho confortável, mas nunca maior do que a tela disponível:
     * em notebook de 1366x768 a janela encolhe sozinha em vez de nascer com os
     * botões para fora do monitor.
     */
    private void ajustarAoMonitor(int larguraDesejada, int alturaDesejada) {
        Rectangle area = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(larguraDesejada, (int) (area.width * 0.95)),
                Math.min(alturaDesejada, (int) (area.height * 0.95)));
    }

    // ---------- montagem da tela ----------

    private JComponent folhaDoPedido() {
        Formulario folha = new Formulario();

        GridBagConstraints posicao = new GridBagConstraints();
        posicao.gridx = 0;
        posicao.gridy = 0;
        posicao.weightx = 1;
        posicao.fill = GridBagConstraints.HORIZONTAL;
        posicao.anchor = GridBagConstraints.NORTHWEST;

        posicao.insets = new Insets(0, 0, 2, 0);
        folha.add(Tema.rotulo("Forja de Fichas", Tema.TITULO, Tema.TINTA), posicao);
        posicao.gridy++;

        posicao.insets = new Insets(0, 0, 10, 0);
        folha.add(Tema.rotulo("Aliados e inimigos prontos para a mesa",
                Tema.CORPO_ITALICO, Tema.TINTA_FRACA), posicao);
        posicao.gridy++;

        posicao.insets = new Insets(0, 0, 18, 0);
        folha.add(new Ornamento(), posicao);
        posicao.gridy++;

        CamposDeFormulario.destacar(campoNome, "em branco, a Forja sorteia");
        campoNome.setToolTipText("Deixe em branco para sortear um nome");
        CamposDeFormulario.aplicar(comboRaca);
        CamposDeFormulario.aplicar(comboClasse);
        CamposDeFormulario.aplicar(comboMetodo);
        prepararNivel();

        adicionarCampo(folha, posicao, "Nome", campoNome);
        adicionarCampo(folha, posicao, "Raça", comboRaca);
        adicionarCampo(folha, posicao, "Classe", comboClasse);
        adicionarCampo(folha, posicao, "Nível", comboNivel);
        adicionarCampo(folha, posicao, "Papel na mesa", papelNaMesa());
        adicionarCampo(folha, posicao, "Como rolar os atributos", comboMetodo);

        MarcaDeTinta.aplicar(checkPriorizar, false);
        posicao.insets = new Insets(0, 0, 20, 0);
        folha.add(checkPriorizar, posicao);
        posicao.gridy++;

        posicao.insets = new Insets(0, 0, 0, 0);
        folha.add(botoes(), posicao);
        posicao.gridy++;

        // Sobra elástica: empurra o rodapé para o pé da folha e segura o
        // formulário no alto dela.
        posicao.insets = new Insets(0, 0, 0, 0);
        posicao.weighty = 1;
        posicao.fill = GridBagConstraints.BOTH;
        folha.add(Box.createGlue(), posicao);
        posicao.gridy++;

        posicao.weighty = 0;
        posicao.fill = GridBagConstraints.HORIZONTAL;
        posicao.insets = new Insets(14, 0, 0, 0);
        folha.add(status, posicao);

        // Em telas baixas o formulário rola, em vez de ter campos cortados.
        JScrollPane rolagem = new JScrollPane(folha);
        rolagem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        CamposDeFormulario.aplicar(rolagem);

        Pergaminho pagina = new Pergaminho(new BorderLayout());
        pagina.setBorder(Tema.margem(RECUO + 16, RECUO + 18));
        pagina.setPreferredSize(new Dimension(396, 0));
        pagina.add(rolagem, BorderLayout.CENTER);
        return pagina;
    }

    private void prepararNivel() {
        for (int nivel = 1; nivel <= 20; nivel++) {
            comboNivel.addItem(nivel);
        }
        CamposDeFormulario.aplicar(comboNivel);
    }

    private JComponent papelNaMesa() {
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioAliado);
        grupo.add(radioInimigo);
        MarcaDeTinta.aplicar(radioAliado, true);
        MarcaDeTinta.aplicar(radioInimigo, true);

        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha.setOpaque(false);
        linha.add(radioAliado);
        linha.add(Box.createHorizontalStrut(24));
        linha.add(radioInimigo);
        return linha;
    }

    private JComponent botoes() {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        for (BotaoTinta botao : List.of(botaoGerar, botaoRolarDeNovo, botaoSalvarPdf)) {
            botao.setAlignmentX(Component.LEFT_ALIGNMENT);
            botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            painel.add(botao);
            painel.add(Box.createVerticalStrut(9));
        }
        return painel;
    }

    private void adicionarCampo(JPanel folha, GridBagConstraints posicao,
                                String rotulo, JComponent campo) {
        posicao.insets = new Insets(0, 0, 3, 0);
        folha.add(Tema.rotulo(rotulo, Tema.ROTULO, Tema.TINTA), posicao);
        posicao.gridy++;

        posicao.insets = new Insets(0, 0, 13, 0);
        folha.add(campo, posicao);
        posicao.gridy++;
    }

    // ---------- carregamento do catálogo ----------

    private void carregarCatalogo() {
        new SwingWorker<Void, Void>() {
            private List<Raca> racas;
            private List<Classe> classes;

            @Override
            protected Void doInBackground() {
                racas = repositorio.listarRacas();
                classes = repositorio.listarClasses();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    comboRaca.addItem(new Opcao<>("Sortear a raça", null));
                    for (Raca raca : racas) {
                        String rotulo = raca.monstruosa() ? raca.nome() + " (monstro)" : raca.nome();
                        comboRaca.addItem(new Opcao<>(rotulo, raca));
                    }

                    comboClasse.addItem(new Opcao<>("Sortear a classe", null));
                    for (Classe classe : classes) {
                        comboClasse.addItem(new Opcao<>(classe.nome(), classe));
                    }

                    status.setText(racas.size() + " raças e " + classes.size()
                            + " classes no catálogo");
                    botaoGerar.setEnabled(true);
                } catch (Exception erro) {
                    status.setText("O catálogo não abriu: " + erro.getMessage());
                }
            }
        }.execute();
    }

    // ---------- ações ----------

    private void gerar() {
        GeradorFicha.Parametros parametros = new GeradorFicha.Parametros(
                campoNome.getText(),
                valor(comboRaca),
                valor(comboClasse),
                (Integer) comboNivel.getSelectedItem(),
                radioInimigo.isSelected() ? TipoFicha.INIMIGO : TipoFicha.ALIADO,
                metodoSelecionado(),
                checkPriorizar.isSelected());
        exibir(gerador.gerar(parametros));
    }

    /** Mantém nome, raça, classe, nível e papel; só rola os atributos outra vez. */
    private void rolarDeNovo() {
        if (fichaAtual == null) {
            return;
        }
        GeradorFicha.Parametros parametros = new GeradorFicha.Parametros(
                fichaAtual.nome(), fichaAtual.raca(), fichaAtual.classe(), fichaAtual.nivel(),
                fichaAtual.tipo(), metodoSelecionado(), checkPriorizar.isSelected());
        exibir(gerador.gerar(parametros));
    }

    private GeradorAtributos.Metodo metodoSelecionado() {
        return (GeradorAtributos.Metodo) comboMetodo.getSelectedItem();
    }

    private void exibir(Ficha ficha) {
        fichaAtual = ficha;
        painelFicha.mostrar(ficha);
        botaoRolarDeNovo.setEnabled(true);
        botaoSalvarPdf.setEnabled(true);
    }

    /** Pergunta onde salvar e grava a ficha em A4, pronta para imprimir. */
    private void salvarEmPdf() {
        if (fichaAtual == null) {
            return;
        }

        JFileChooser seletor = new JFileChooser(System.getProperty("user.home"));
        seletor.setDialogTitle("Salvar a ficha em PDF");
        seletor.setSelectedFile(new File(FichaEmPdf.nomeDeArquivo(fichaAtual)));
        seletor.setFileFilter(new FileNameExtensionFilter("Documento PDF", "pdf"));

        if (seletor.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path destino = comExtensaoPdf(seletor.getSelectedFile().toPath());
        if (Files.exists(destino) && !confirmarSubstituicao(destino)) {
            return;
        }

        try {
            new FichaEmPdf().salvar(fichaAtual, destino);
            status.setText("Ficha salva em " + destino.getFileName());
        } catch (IOException erro) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível salvar o arquivo.\n" + erro.getMessage(),
                    "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Garante a extensão, que o usuário pode ter apagado ao renomear no diálogo. */
    private Path comExtensaoPdf(Path caminho) {
        String nome = caminho.getFileName().toString();
        return nome.toLowerCase(Locale.ROOT).endsWith(".pdf")
                ? caminho
                : caminho.resolveSibling(nome + ".pdf");
    }

    private boolean confirmarSubstituicao(Path destino) {
        int resposta = JOptionPane.showConfirmDialog(this,
                destino.getFileName() + " já existe. Substituir?",
                "Substituir arquivo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return resposta == JOptionPane.YES_OPTION;
    }

    private static <T> T valor(JComboBox<Opcao<T>> combo) {
        @SuppressWarnings("unchecked")
        Opcao<T> opcao = (Opcao<T>) combo.getSelectedItem();
        return opcao == null ? null : opcao.valor();
    }

    /** Formulário que ocupa a largura da folha e cresce só o necessário em altura. */
    private static class Formulario extends JPanel implements Scrollable {
        Formulario() {
            super(new GridBagLayout());
            setOpaque(false);
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
            return 120;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            // Formulário mais curto que a folha: ocupa a folha inteira, e a
            // folga elástica joga o rodapé para baixo. Mais comprido: rola.
            Component janela = getParent();
            return janela != null && getPreferredSize().height < janela.getHeight();
        }
    }

    /** Fundo da janela: o couro verde da mesa onde as folhas estão apoiadas. */
    private static class Mesa extends JPanel {

        private static final TexturePaint COURO = criarCouro();

        Mesa() {
            // Sem isto o JPanel repinta o fundo padrão por cima do couro.
            setOpaque(false);
        }

        private static TexturePaint criarCouro() {
            int lado = 96;
            BufferedImage ladrilho = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = ladrilho.createGraphics();
            g.setColor(Tema.MESA);
            g.fillRect(0, 0, lado, lado);

            Random grao = new Random(13);
            for (int i = 0; i < 1500; i++) {
                int x = grao.nextInt(lado);
                int y = grao.nextInt(lado);
                g.setColor(new Color(Tema.MESA_CLARA.getRed(), Tema.MESA_CLARA.getGreen(),
                        Tema.MESA_CLARA.getBlue(), 36 + grao.nextInt(24)));
                g.fillRect(x, y, 1 + grao.nextInt(2), 1);
            }
            g.dispose();
            return new TexturePaint(ladrilho, new Rectangle(0, 0, lado, lado));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(COURO);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
