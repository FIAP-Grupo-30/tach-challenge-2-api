package tach_challenge_2.api_financeiro.dto;

import lombok.Data;
import tach_challenge_2.api_financeiro.model.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransacaoRequestDTO {

    private TipoTransacao tipoTransacao;

    private BigDecimal valor;

    private String chavePix;

    private String agencia;

    private String numeroConta;

    private String descricao;

    private LocalDate data;

}
