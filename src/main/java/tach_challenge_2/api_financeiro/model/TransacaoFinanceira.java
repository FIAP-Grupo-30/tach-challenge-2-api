package tach_challenge_2.api_financeiro.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transacoes_financeiras")
@Data
public class TransacaoFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipoTransacao;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    private String chavePix;

    private LocalDate data;

    private String agencia;

    private String numeroConta;

    private String descricao;

}
