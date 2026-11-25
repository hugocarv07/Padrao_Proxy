package padroesestruturais.proxy;

public class Main {

    public static void main(String[] args) {
        ConsultaProcesso juiz = new ConsultaProcessoProxy(new Usuario("Dr. Hugo", Perfil.JUIZ), Main::criarServico);
        ConsultaProcessoProxy visitante = new ConsultaProcessoProxy(new Usuario("Visitante", Perfil.VISITANTE), Main::criarServico);

        System.out.println(juiz.consultar("0001"));
        System.out.println(juiz.consultar("0002"));
        System.out.println(visitante.consultar("0001"));
        System.out.println(visitante.consultar("0001"));

        try {
            visitante.consultar("0002");
        } catch (AcessoNegadoException e) {
            System.out.println(e.getMessage());
        }

        visitante.getRegistros().forEach(System.out::println);
    }

    private static ConsultaProcesso criarServico() {
        ConsultaProcessoReal real = new ConsultaProcessoReal();
        real.adicionar(new Processo("0001", false, "Processo 0001 - Cobrança indevida"));
        real.adicionar(new Processo("0002", true, "Processo 0002 - Segredo de justiça"));
        return real;
    }
}
