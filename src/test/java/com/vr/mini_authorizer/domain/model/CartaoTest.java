package com.vr.mini_authorizer.domain.model;

import com.vr.mini_authorizer.domain.exception.MotivoRecusa;
import com.vr.mini_authorizer.domain.exception.TransacaoRecusadaException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * testes da entidade Cartao: criação e regras de débito (senha e saldo)
 */
class CartaoTest {

    private final NumeroCartao numero = NumeroCartao.de("6549873025634501");
    private final Senha senha = Senha.de("1234");

    @Test
    void cartaoNovoNasceComSaldoDeQuinhentosReais() {
        Cartao cartao = Cartao.novo(numero, senha);

        assertThat(cartao.numero()).isEqualTo(numero);
        assertThat(cartao.senha()).isEqualTo(senha);
        assertThat(cartao.saldo()).isEqualTo(Valor.de("500.00"));
    }

    @Test
    void reconstituirPreservaOSaldoInformado() {
        Cartao cartao = Cartao.reconstituir(numero, senha, Valor.de("495.15"));

        assertThat(cartao.saldo()).isEqualTo(Valor.de("495.15"));
    }

    @Test
    void debitarComSenhaCorretaESaldoSuficienteDiminuiOSaldo() {
        Cartao cartao = Cartao.novo(numero, senha);

        cartao.debitar(Senha.de("1234"), Valor.de("4.85"));

        assertThat(cartao.saldo()).isEqualTo(Valor.de("495.15"));
    }

    @Test
    void debitarOSaldoExatoZeraOCartao() {
        Cartao cartao = Cartao.reconstituir(numero, senha, Valor.de("10.00"));

        cartao.debitar(Senha.de("1234"), Valor.de("10.00"));

        assertThat(cartao.saldo()).isEqualTo(Valor.de("0.00"));
    }

    @Test
    void debitarComSenhaErradaRecusaComSenhaInvalidaEnaoMexeNoSaldo() {
        Cartao cartao = Cartao.novo(numero, senha);

        assertThatThrownBy(() -> cartao.debitar(Senha.de("9999"), Valor.de("10.00")))
                .isInstanceOfSatisfying(TransacaoRecusadaException.class,
                        e -> assertThat(e.motivo()).isEqualTo(MotivoRecusa.SENHA_INVALIDA));
        assertThat(cartao.saldo()).isEqualTo(Valor.de("500.00"));
    }

    @Test
    void debitarAlemDoSaldoRecusaComSaldoInsuficienteEnaoMexeNoSaldo() {
        Cartao cartao = Cartao.reconstituir(numero, senha, Valor.de("10.00"));

        assertThatThrownBy(() -> cartao.debitar(Senha.de("1234"), Valor.de("10.01")))
                .isInstanceOfSatisfying(TransacaoRecusadaException.class,
                        e -> assertThat(e.motivo()).isEqualTo(MotivoRecusa.SALDO_INSUFICIENTE));
        assertThat(cartao.saldo()).isEqualTo(Valor.de("10.00"));
    }

    // mesmo sem saldo, a senha errada tem prioridade (ordem do README)
    @Test
    void senhaEhVerificadaAntesDoSaldo() {
        Cartao cartao = Cartao.reconstituir(numero, senha, Valor.de("0.00"));

        assertThatThrownBy(() -> cartao.debitar(Senha.de("9999"), Valor.de("10.00")))
                .isInstanceOfSatisfying(TransacaoRecusadaException.class,
                        e -> assertThat(e.motivo()).isEqualTo(MotivoRecusa.SENHA_INVALIDA));
    }
}
