package padroesestruturais.proxy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsultaProcessoRealTest {

    private ConsultaProcessoReal real;

    @BeforeEach
    void preparar() {
        real = new ConsultaProcessoReal();
        real.adicionar(new Processo("0001", false, "conteúdo público"));
        real.adicionar(new Processo("0002", true, "conteúdo sigiloso"));
    }

    @Test
    void consultaRetornaOConteudoDoProcesso() {
        assertEquals("conteúdo público", real.consultar("0001"));
        assertEquals("conteúdo sigiloso", real.consultar("0002"));
    }

    @Test
    void sigiloEhInformadoCorretamente() {
        assertFalse(real.isSigiloso("0001"));
        assertTrue(real.isSigiloso("0002"));
    }

    @Test
    void contaApenasAsConsultasDeConteudo() {
        real.isSigiloso("0001");
        assertEquals(0, real.getConsultasRealizadas());

        real.consultar("0001");
        real.consultar("0001");
        assertEquals(2, real.getConsultasRealizadas());
    }

    @Test
    void processoInexistenteLancaExcecao() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> real.consultar("9999"));
        assertEquals("Processo inexistente", ex.getMessage());
        assertEquals(0, real.getConsultasRealizadas());
    }
}
