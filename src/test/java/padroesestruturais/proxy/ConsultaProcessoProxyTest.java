package padroesestruturais.proxy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ConsultaProcessoProxyTest {

    private ConsultaProcessoReal real;
    private AtomicInteger criacoes;

    @BeforeEach
    void preparar() {
        real = new ConsultaProcessoReal();
        real.adicionar(new Processo("0001", false, "conteúdo público"));
        real.adicionar(new Processo("0002", true, "conteúdo sigiloso"));
        criacoes = new AtomicInteger();
    }

    private ConsultaProcessoProxy proxyDe(String nome, Perfil perfil) {
        return new ConsultaProcessoProxy(new Usuario(nome, perfil), () -> {
            criacoes.incrementAndGet();
            return real;
        });
    }

    @Test
    void proxyEhUmaConsultaProcesso() {
        ConsultaProcesso consulta = proxyDe("Ana", Perfil.JUIZ);

        assertEquals("conteúdo público", consulta.consultar("0001"));
    }

    @Test
    void servicoRealSoEhCriadoNaPrimeiraConsulta() {
        ConsultaProcessoProxy proxy = proxyDe("Ana", Perfil.JUIZ);
        assertEquals(0, criacoes.get());

        proxy.consultar("0001");
        proxy.consultar("0002");

        assertEquals(1, criacoes.get());
    }

    @Test
    void consultaRepetidaUsaCacheENaoChamaOServicoReal() {
        ConsultaProcessoProxy proxy = proxyDe("Ana", Perfil.ADVOGADO);

        proxy.consultar("0001");
        proxy.consultar("0001");
        proxy.consultar("0001");

        assertEquals(1, real.getConsultasRealizadas());
    }

    @Test
    void cacheEhPorProxyEntaoOutroUsuarioConsultaDeNovo() {
        proxyDe("Ana", Perfil.JUIZ).consultar("0001");
        proxyDe("Bia", Perfil.JUIZ).consultar("0001");

        assertEquals(2, real.getConsultasRealizadas());
    }

    @Test
    void juizESevidorAcessamProcessoSigiloso() {
        assertEquals("conteúdo sigiloso", proxyDe("Ana", Perfil.JUIZ).consultar("0002"));
        assertEquals("conteúdo sigiloso", proxyDe("Caio", Perfil.SERVIDOR).consultar("0002"));
    }

    @Test
    void advogadoEVisitanteNaoAcessamProcessoSigiloso() {
        AcessoNegadoException ex = assertThrows(AcessoNegadoException.class,
                () -> proxyDe("Dani", Perfil.ADVOGADO).consultar("0002"));
        assertEquals("Acesso negado ao processo sigiloso", ex.getMessage());

        assertThrows(AcessoNegadoException.class, () -> proxyDe("Eva", Perfil.VISITANTE).consultar("0002"));
    }

    @Test
    void acessoNegadoNaoChegaAConsultarOConteudoNoServicoReal() {
        ConsultaProcessoProxy proxy = proxyDe("Eva", Perfil.VISITANTE);

        assertThrows(AcessoNegadoException.class, () -> proxy.consultar("0002"));

        assertEquals(0, real.getConsultasRealizadas());
    }

    @Test
    void visitantePodeConsultarProcessoPublico() {
        assertEquals("conteúdo público", proxyDe("Eva", Perfil.VISITANTE).consultar("0001"));
    }

    @Test
    void numeroNuloOuVazioLancaExcecaoSemCriarOServicoReal() {
        ConsultaProcessoProxy proxy = proxyDe("Ana", Perfil.JUIZ);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> proxy.consultar(null));
        assertEquals("Número do processo inválido", ex.getMessage());
        assertThrows(IllegalArgumentException.class, () -> proxy.consultar("  "));

        assertEquals(0, criacoes.get());
    }

    @Test
    void processoInexistenteEhPropagadoPeloServicoReal() {
        ConsultaProcessoProxy proxy = proxyDe("Ana", Perfil.JUIZ);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> proxy.consultar("9999"));

        assertEquals("Processo inexistente", ex.getMessage());
    }

    @Test
    void isSigilosoEhRepassadoAoServicoReal() {
        ConsultaProcessoProxy proxy = proxyDe("Eva", Perfil.VISITANTE);

        assertFalse(proxy.isSigiloso("0001"));
        assertTrue(proxy.isSigiloso("0002"));
    }

    @Test
    void registrosAuditamRealCacheENegado() {
        ConsultaProcessoProxy proxy = proxyDe("Eva", Perfil.VISITANTE);

        proxy.consultar("0001");
        proxy.consultar("0001");
        assertThrows(AcessoNegadoException.class, () -> proxy.consultar("0002"));

        assertEquals(List.of(
                "Eva | 0001 | REAL",
                "Eva | 0001 | CACHE",
                "Eva | 0002 | NEGADO"), proxy.getRegistros());
    }

    @Test
    void listaDeRegistrosNaoPodeSerAlteradaDeFora() {
        ConsultaProcessoProxy proxy = proxyDe("Ana", Perfil.JUIZ);
        proxy.consultar("0001");

        assertThrows(UnsupportedOperationException.class, () -> proxy.getRegistros().clear());
    }
}
