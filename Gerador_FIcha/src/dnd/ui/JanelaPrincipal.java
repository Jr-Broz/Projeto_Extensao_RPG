package dnd.ui;

import dnd.dados.RepositorioDados;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class JanelaPrincipal extends JFrame {

    private final RepositorioDados repositorio;
    private final GeradorFicha gerador;

    private final JTextField campoNome = new JTextField();
    private final JComboBox<Opcao<Raca>> comboRaca = new JComboBox<>();
    private final JComboBox<Opcao<Classe>> comboClasse = new JComboBox<>();
    private final JSpinner spinnerNivel = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JRadioButton radioAliado = new JRadioButton("Aliado", true);
    private final JRadioButton radioInimigo = new JRadioButton("Inimigo");
    private final JComboBox<GeradorAtributos.Metodo> comboMetodo =
            new JComboBox<>(GeradorAtributos.Metodo.values());
    private final JCheckBox checkPriorizar = new JCheckBox("Maior valor no atributo da classe", true);
    private final Botao botaoGerar = new Botao("Gerar ficha", true);
    private final Botao botaoRolarDeNovo = new Botao("Rolar atributos de novo", false);
    private final JLabel status = Tema.rotulo("Carregando raças e classes…", Tema.PEQUENA, Tema.NEBLINA);
    private final PainelFicha painelFicha = new PainelFicha();

    private Ficha fichaAtual;

    public JanelaPrincipal(RepositorioDados repositorio) {
        super("Forja de Fichas — gerador de D&D 5e");
        this.repositorio = repositorio;
        this.gerador = new GeradorFicha(repositorio);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel raiz = new JPanel(new BorderLayout(16, 0));
        raiz.setBackground(Tema.NOITE);
        raiz.setBorder(Tema.margem(16, 16));
        raiz.add(painelConfiguracao(), BorderLayout.WEST);
        raiz.add(painelFicha, BorderLayout.CENTER);
        setContentPane(raiz);

        botaoGerar.setEnabled(false);
        botaoRolarDeNovo.setEnabled(false);
        botaoGerar.addActionListener(e -> gerar());
        botaoRolarDeNovo.addActionListener(e -> rolarDeNovo());
        getRootPane().setDefaultButton(botaoGerar);

        setMinimumSize(new Dimension(1180, 760));
        setLocationRelativeTo(null);
        carregarDados();
    }

    // ---------- montagem ----------

    private JComponent painelConfiguracao() {
        PainelArredondado p = new PainelArredondado(new GridBagLayout(), Tema.GRIMORIO, 20);
        p.setBorder(Tema.margem(20, 18));
        p.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        g.insets = new Insets(0, 0, 4, 0);
        p.add(Tema.rotulo("Forja de Fichas", Tema.NOME.deriveFont(24f), Tema.LATAO), g);
        g.gridy++;
        g.insets = new Insets(0, 0, 20, 0);
        p.add(Tema.rotulo("Aliados e inimigos prontos para a mesa", Tema.SUBTITULO.deriveFont(13f), Tema.NEBLINA), g);
        g.gridy++;

        campoNome.setToolTipText("Deixe em branco para sortear um nome");
        ComboEscuro.aplicar(comboRaca);
        ComboEscuro.aplicar(comboClasse);
        ComboEscuro.aplicar(comboMetodo);
        adicionarCampo(p, g, "Nome (em branco para sortear)", campoNome);
        adicionarCampo(p, g, "Raça", comboRaca);
        adicionarCampo(p, g, "Classe", comboClasse);
        adicionarCampo(p, g, "Nível", spinnerNivel);

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioAliado);
        grupo.add(radioInimigo);
        JPanel tipos = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tipos.setOpaque(false);
        estilizar(radioAliado);
        estilizar(radioInimigo);
        tipos.add(radioAliado);
        tipos.add(Box.createHorizontalStrut(16));
        tipos.add(radioInimigo);
        adicionarCampo(p, g, "Papel na mesa", tipos);

        adicionarCampo(p, g, "Como gerar os atributos", comboMetodo);
        estilizar(checkPriorizar);
        g.insets = new Insets(0, 0, 22, 0);
        p.add(checkPriorizar, g);
        g.gridy++;

        JPanel botoes = new JPanel();
        botoes.setOpaque(false);
        botoes.setLayout(new BoxLayout(botoes, BoxLayout.Y_AXIS));
        for (Botao b : List.of(botaoGerar, botaoRolarDeNovo)) {
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            botoes.add(b);
            botoes.add(Box.createVerticalStrut(8));
        }
        g.insets = new Insets(0, 0, 0, 0);
        p.add(botoes, g);
        g.gridy++;

        // empurra o status para o fim
        g.weighty = 1;
        g.anchor = GridBagConstraints.SOUTHWEST;
        g.fill = GridBagConstraints.NONE;
        p.add(status, g);
        return p;
    }

    private void adicionarCampo(JPanel p, GridBagConstraints g, String rotulo, JComponent campo) {
        g.insets = new Insets(0, 0, 5, 0);
        p.add(Tema.rotulo(rotulo, Tema.CORPO_NEG, Tema.PERGAMINHO), g);
        g.gridy++;
        g.insets = new Insets(0, 0, 14, 0);
        campo.setFont(Tema.CORPO);
        p.add(campo, g);
        g.gridy++;
    }

    private void estilizar(JComponent c) {
        c.setOpaque(false);
        c.setForeground(Tema.PERGAMINHO);
        c.setFont(Tema.CORPO);
    }

    // ---------- carregamento (o "request" de raças e classes) ----------

    private void carregarDados() {
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
                    comboRaca.addItem(new Opcao<>("Aleatória", null));
                    racas.forEach(r -> comboRaca.addItem(new Opcao<>(r.monstruosa() ? r.nome() + " (monstro)" : r.nome(), r)));
                    comboClasse.addItem(new Opcao<>("Aleatória", null));
                    classes.forEach(c -> comboClasse.addItem(new Opcao<>(c.nome(), c)));
                    status.setText(racas.size() + " raças e " + classes.size() + " classes disponíveis");
                    botaoGerar.setEnabled(true);
                } catch (Exception ex) {
                    status.setText("Não foi possível carregar os dados: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ---------- ações ----------

    private void gerar() {
        var params = new GeradorFicha.Parametros(
                campoNome.getText(),
                valor(comboRaca),
                valor(comboClasse),
                (Integer) spinnerNivel.getValue(),
                radioInimigo.isSelected() ? TipoFicha.INIMIGO : TipoFicha.ALIADO,
                (GeradorAtributos.Metodo) comboMetodo.getSelectedItem(),
                checkPriorizar.isSelected());
        exibir(gerador.gerar(params));
    }

    /** Mantém nome, raça, classe, nível e papel; só rola os atributos outra vez. */
    private void rolarDeNovo() {
        if (fichaAtual == null) return;
        var params = new GeradorFicha.Parametros(
                fichaAtual.nome(), fichaAtual.raca(), fichaAtual.classe(), fichaAtual.nivel(), fichaAtual.tipo(),
                (GeradorAtributos.Metodo) comboMetodo.getSelectedItem(),
                checkPriorizar.isSelected());
        exibir(gerador.gerar(params));
    }

    private void exibir(Ficha f) {
        fichaAtual = f;
        painelFicha.mostrar(f);
        botaoRolarDeNovo.setEnabled(true);
    }

    private static <T> T valor(JComboBox<Opcao<T>> combo) {
        @SuppressWarnings("unchecked")
        Opcao<T> o = (Opcao<T>) combo.getSelectedItem();
        return o == null ? null : o.valor();
    }
}
