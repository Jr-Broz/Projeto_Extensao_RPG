package dnd.service;

/**
 * As sílabas usadas para montar os nomes, separadas por povo.
 *
 * Cada tabela tem começos, meios, fins e sobrenomes. Como cada parte é
 * sorteada em separado, poucas sílabas dão muitos nomes diferentes.
 *
 * As sílabas misturam as que o projeto já usava, que vinham do D&D, com
 * sílabas no estilo do World of Warcraft.
 */
public class TabelaNomes {

    private final String[] comecos;
    private final String[] meios;
    private final String[] fins;
    private final String[] sobrenomes;

    public TabelaNomes(String[] comecos, String[] meios, String[] fins, String[] sobrenomes) {
        this.comecos = comecos;
        this.meios = meios;
        this.fins = fins;
        this.sobrenomes = sobrenomes;
    }

    public String[] comecos() { return comecos; }
    public String[] meios() { return meios; }
    public String[] fins() { return fins; }
    public String[] sobrenomes() { return sobrenomes; }

    /** Quantos nomes diferentes esta tabela consegue montar. */
    public int combinacoes() {
        return comecos.length * (meios.length + 1) * fins.length * (sobrenomes.length + 1);
    }

    /**
     * Escolhe a tabela pelo nome da raça. As raças aliadas usam a tabela do
     * povo de origem: o Anão Ferro Negro usa a dos anões, o Orc Mag'har usa a
     * dos orcs, e assim por diante.
     */
    public static TabelaNomes daRaca(String nomeDaRaca) {
        if (nomeDaRaca == null) {
            return HUMANO;
        }
        return switch (nomeDaRaca) {
            case "Anão", "Anão Ferro Negro" -> ANAO;
            case "Elfo Noturno", "Elfo Sangrento", "Elfo do Vazio" -> ELFO;
            case "Gnomo" -> GNOMO;
            case "Draenei" -> DRAENEI;
            case "Worgen" -> WORGEN;
            case "Pandaren" -> PANDAREN;
            case "Dracthyr" -> DRACONICO;
            case "Orc", "Orc Mag'har" -> ORC;
            case "Morto-vivo" -> MORTO_VIVO;
            case "Tauren", "Tauren Altomontês" -> TAUREN;
            case "Troll", "Troll Zandalari" -> TROLL;
            case "Goblin", "Vulpera" -> GOBLIN;
            default -> HUMANO;
        };
    }

    // ---------- as tabelas ----------

    private static final TabelaNomes HUMANO = new TabelaNomes(
            new String[]{"Ar", "Bel", "Bre", "Dor", "El", "Gar", "Hal", "Lor", "Mir", "Nor",
                    "Tor", "Val", "And", "Var", "Jai", "Tir", "Bol", "Rei", "Edw", "Mar"},
            new String[]{"ald", "an", "ber", "dri", "ri", "val", "win", "thor"},
            new String[]{"uin", "ian", "na", "as", "on", "ric", "win", "mar", "eth", "ius",
                    "ena", "ald", "ara"},
            new String[]{"Ventobravo", "Punho de Luz", "Mãolonga", "Escudo-Leal", "Alvorada",
                    "Coroa Partida", "de Tol Barad", "Guarda do Norte"});

    private static final TabelaNomes ANAO = new TabelaNomes(
            new String[]{"Bran", "Dur", "Mur", "Thaur", "Mod", "Bael", "Gor", "Khar", "Mag",
                    "Thel", "Bal", "Grim", "Dag", "Ulf", "Thor", "Nor"},
            new String[]{"bar", "din", "gar", "mun", "rak", "thor", "bek"},
            new String[]{"in", "ir", "li", "din", "rak", "mund", "gar", "dur", "bek", "nar",
                    "ni", "am"},
            new String[]{"Barbabronze", "Martelo de Ferro", "Forjanegra", "Pedrapunho",
                    "Veia de Ouro", "Bigorna Funda", "Escudo de Granito", "Picareta Firme"});

    private static final TabelaNomes ELFO = new TabelaNomes(
            new String[]{"Ael", "Cae", "Elu", "Fae", "Gal", "Il", "Lae", "Mith", "Nae", "Que",
                    "Syl", "Tha", "Val", "Ys", "Ara", "Lor", "Eri", "Vae", "Kael", "Anar"},
            new String[]{"an", "dri", "la", "ri", "the", "va", "lu", "mi", "ne", "ther"},
            new String[]{"wen", "iel", "ras", "thil", "dor", "lian", "mar", "sara", "vyr",
                    "deth", "nis", "as", "ion"},
            new String[]{"Andarsol", "Cantoflecha", "Lua Prateada", "Fogo-Solar",
                    "Brisa Sombria", "Folhaverde", "Alvorada Eterna", "Véu de Cinzas"});

    private static final TabelaNomes GNOMO = new TabelaNomes(
            new String[]{"Fizz", "Gim", "Nog", "Tink", "Wix", "Zib", "Bix", "Dob", "Kel",
                    "Mek", "Pip", "Spar", "Toc", "Wiz"},
            new String[]{"a", "le", "o", "we", "zi", "ber"},
            new String[]{"zik", "bim", "trec", "nic", "pok", "lim", "zap", "bom", "ri", "to"},
            new String[]{"Molamestra", "Aperta-Parafuso", "Engrenagem Veloz", "Faísca Fina",
                    "Gira-Chave", "Explosão Controlada"});

    private static final TabelaNomes DRAENEI = new TabelaNomes(
            new String[]{"Ari", "Ful", "Kaa", "Mor", "Nob", "Rhu", "Vel", "Zar", "Exo",
                    "Jor", "Mae", "Ony"},
            new String[]{"an", "dor", "el", "ra", "thu", "vi"},
            new String[]{"aan", "oor", "uul", "een", "iel", "aar", "oth", "ien"},
            new String[]{"dos Naaru", "Luz de Argus", "Portador do Cristal", "Vigia da Luz",
                    "Exilado de Draenor", "Guia do Exílio"});

    private static final TabelaNomes WORGEN = new TabelaNomes(
            new String[]{"Dar", "Gael", "Hal", "Lor", "Mor", "Ral", "Syl", "Vin", "Gra", "Thel"},
            new String[]{"en", "gar", "mo", "ric", "win"},
            new String[]{"ric", "don", "mund", "gar", "wen", "drek", "ald", "an"},
            new String[]{"Presa Noturna", "de Gilneas", "Uivo Longo", "Garra Cinzenta",
                    "Caçador da Névoa", "Pelo de Prata"});

    private static final TabelaNomes PANDAREN = new TabelaNomes(
            new String[]{"Chen", "Li", "Mei", "Shen", "Tao", "Wu", "Yun", "Zhu", "Bao",
                    "Jin", "Kun", "Mo"},
            new String[]{"an", "ji", "li", "shi", "yu"},
            new String[]{"lin", "mei", "tao", "shan", "po", "yun", "wei", "ling", "bo"},
            new String[]{"Pata de Jade", "Névoa Serena", "Bambu Firme", "Punho de Trovão",
                    "Copo Cheio", "Caminho Tranquilo"});

    private static final TabelaNomes DRACONICO = new TabelaNomes(
            new String[]{"Kaz", "Vyr", "Ner", "Sar", "Dra", "Ryg", "Zal", "Mal", "Thaz", "Ebys"},
            new String[]{"an", "ith", "ra", "zhu", "kar"},
            new String[]{"oth", "ash", "yx", "ar", "zir", "ux", "ion", "eth"},
            new String[]{"Asa de Bronze", "Escama Negra", "Garra Esmeralda", "Sopro de Âmbar",
                    "Guarda de Valdrakken", "Ninhada Antiga"});

    private static final TabelaNomes ORC = new TabelaNomes(
            new String[]{"Grom", "Durot", "Kar", "Mog", "Nok", "Rag", "Thok", "Ug", "Varg",
                    "Zug", "Gor", "Blak", "Krag", "Skar", "Uth"},
            new String[]{"ash", "dar", "ka", "mok", "thu", "zur"},
            new String[]{"mash", "gar", "thar", "rok", "ash", "grim", "duk", "nak", "ok", "zul"},
            new String[]{"Punho Sangrento", "Lobo Gélido", "Mão Negra", "Canto de Guerra",
                    "Quebra-Crânios", "Filho de Durotar", "o Matador"});

    private static final TabelaNomes MORTO_VIVO = new TabelaNomes(
            new String[]{"Mor", "Nath", "Varim", "Dere", "Kel", "Ulf", "Grim", "Hel", "Thal", "Sylv"},
            new String[]{"an", "dre", "ka", "ri", "tha", "zel"},
            new String[]{"anos", "thas", "as", "ius", "eth", "ira", "on", "ane"},
            new String[]{"o Esquecido", "Mão Fria", "da Fossa", "Sopro de Praga",
                    "o Que Voltou", "Lápide Rachada"});

    private static final TabelaNomes TAUREN = new TabelaNomes(
            new String[]{"Bai", "Cair", "Hamu", "Mag", "Tah", "Hurn", "Mull", "Sun", "Ark", "Oro"},
            new String[]{"ha", "na", "to", "wa", "ru"},
            new String[]{"ul", "un", "uk", "ra", "no", "tha", "mo", "wan"},
            new String[]{"Sangue Total", "Chifre de Trovão", "Caminhante do Vento",
                    "Pele de Rocha", "Olho Ancestral", "Pisa-Firme"});

    private static final TabelaNomes TROLL = new TabelaNomes(
            new String[]{"Vol", "Zul", "Sen", "Rok", "Jin", "Tal", "Gaz", "Mal", "Hex", "Zan"},
            new String[]{"ja", "ki", "ra", "zu", "da"},
            new String[]{"jin", "zar", "din", "rok", "kar", "tul", "ji", "an", "thek"},
            new String[]{"o Vodu", "Caçador de Loas", "Olho de Serpente", "Dente Afiado",
                    "dos Ecos", "Mordida Rápida"});

    private static final TabelaNomes GOBLIN = new TabelaNomes(
            new String[]{"Gaz", "Niz", "Riz", "Zik", "Mox", "Bilg", "Druk", "Sprok", "Wiz",
                    "Trag", "Snik", "Grak"},
            new String[]{"a", "le", "o", "zi", "gu"},
            new String[]{"lo", "zik", "nic", "zap", "trec", "bim", "gan", "ruk", "mug", "lob"},
            new String[]{"Lucro Certo", "Mola Solta", "Barril de Pólvora", "Juro Alto",
                    "Ferro-Velho", "Negócio Fechado"});
}
