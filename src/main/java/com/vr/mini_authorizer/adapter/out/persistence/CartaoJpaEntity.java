package com.vr.mini_authorizer.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;

/**
 * entidade JPA — para "espelhar" a tabela cartao no banco.
 *
 */
@Entity
@Table(name = "cartao")
public class CartaoJpaEntity implements Persistable<String> {

    // Chave primária é o nº do cartão
    @Id
    @Column(name = "numero_cartao", length = 16, nullable = false)
    private String numeroCartao;

    @Column(name = "senha", length = 4, nullable = false)
    private String senha;

    @Column(name = "saldo", precision = 19, scale = 2, nullable = false)
    private BigDecimal saldo;

    // @Transient: campo que existe só em memória, o Hibernate NÃO o mapeia
    // para nenhuma coluna. Serve apenas para responder isNew() logo abaixo.
    @Transient
    private boolean novo;

    // Construtor sem argumentos exigido pelo JPA/Hibernate: ele instancia a
    // entidade via reflection e só depois preenche os campos, SEM passar
    // pelo construtor de baixo. Por isso "novo" permanece com o valor
    // padrão de boolean (false) quando o Hibernate RECARREGA uma entidade
    // já existente do banco. "protected" (e não "public") porque só o
    // próprio Hibernate deveria usar este construtor.
    protected CartaoJpaEntity() {
    }

    // Construtor usado pelo nosso código (CartaoMapper.toEntity) sempre que
    // vamos CRIAR um cartão novo. Aqui marcamos novo = true de propósito.
    public CartaoJpaEntity(String numeroCartao, String senha, BigDecimal saldo) {
        this.numeroCartao = numeroCartao;
        this.senha = senha;
        this.saldo = saldo;
        this.novo = true;
    }

    @Override
    public String getId() {
        return numeroCartao;
    }

    /**
     * para saber se vai ser um INSERT(return true) ou UPDATE (return false).
     */
    @Override
    public boolean isNew() {
        return novo;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getSenha() {
        return senha;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
