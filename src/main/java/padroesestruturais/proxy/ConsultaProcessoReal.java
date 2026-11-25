package padroesestruturais.proxy;

import java.util.HashMap;
import java.util.Map;

public class ConsultaProcessoReal implements ConsultaProcesso {

    private final Map<String, Processo> processos = new HashMap<>();
    private int consultasRealizadas = 0;

    public ConsultaProcessoReal() {
    }

    public void adicionar(Processo processo) {
        processos.put(processo.getNumero(), processo);
    }

    @Override
    public String consultar(String numero) {
        Processo processo = buscar(numero);
        consultasRealizadas++;
        return processo.getConteudo();
    }

    @Override
    public boolean isSigiloso(String numero) {
        return buscar(numero).isSigiloso();
    }

    public int getConsultasRealizadas() {
        return consultasRealizadas;
    }

    private Processo buscar(String numero) {
        Processo processo = processos.get(numero);
        if (processo == null) {
            throw new IllegalArgumentException("Processo inexistente");
        }
        return processo;
    }
}
