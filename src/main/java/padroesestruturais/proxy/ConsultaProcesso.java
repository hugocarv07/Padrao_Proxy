package padroesestruturais.proxy;

public interface ConsultaProcesso {

    String consultar(String numero);

    boolean isSigiloso(String numero);
}
