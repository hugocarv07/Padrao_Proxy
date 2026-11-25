package padroesestruturais.proxy;

public class Processo {
    private final String numero;
    private final boolean sigiloso;
    private final String conteudo;

    public Processo(String numero, boolean sigiloso, String conteudo) {
        this.numero = numero;
        this.sigiloso = sigiloso;
        this.conteudo = conteudo;
    }

    public String getNumero() {
        return numero;
    }

    public boolean isSigiloso() {
        return sigiloso;
    }

    public String getConteudo() {
        return conteudo;
    }
}
