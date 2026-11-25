package padroesestruturais.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ConsultaProcessoProxy implements ConsultaProcesso {

    private final Usuario usuario;
    private final Supplier<ConsultaProcesso> fabrica;
    private ConsultaProcesso real;
    private final Map<String, String> cache = new HashMap<>();
    private final List<String> registros = new ArrayList<>();

    public ConsultaProcessoProxy(Usuario usuario, Supplier<ConsultaProcesso> fabrica) {
        this.usuario = usuario;
        this.fabrica = fabrica;
    }

    @Override
    public String consultar(String numero) {
        validar(numero);

        if (isSigiloso(numero) && !usuario.getPerfil().podeVerSigiloso()) {
            registrar(numero, "NEGADO");
            throw new AcessoNegadoException("Acesso negado ao processo sigiloso");
        }

        if (cache.containsKey(numero)) {
            registrar(numero, "CACHE");
            return cache.get(numero);
        }

        String conteudo = getReal().consultar(numero);
        cache.put(numero, conteudo);
        registrar(numero, "REAL");
        return conteudo;
    }

    @Override
    public boolean isSigiloso(String numero) {
        validar(numero);
        return getReal().isSigiloso(numero);
    }

    public List<String> getRegistros() {
        return Collections.unmodifiableList(registros);
    }

    private ConsultaProcesso getReal() {
        if (real == null) {
            real = fabrica.get();
        }
        return real;
    }

    private void validar(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Número do processo inválido");
        }
    }

    private void registrar(String numero, String resultado) {
        registros.add(usuario.getNome() + " | " + numero + " | " + resultado);
    }
}
