package tach_challenge_2.api_financeiro.service;

import org.springframework.stereotype.Service;
import tach_challenge_2.api_financeiro.dto.TransacaoRequestDTO;
import tach_challenge_2.api_financeiro.model.TransacaoFinanceira;
import tach_challenge_2.api_financeiro.repository.TransacaoRepository;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository ) {
        this.transacaoRepository = transacaoRepository;
    }

    public TransacaoFinanceira criar(TransacaoRequestDTO dto) {
        TransacaoFinanceira transacao = new TransacaoFinanceira();
        transacao.setTipoTransacao(dto.getTipoTransacao());
        transacao.setValor(dto.getValor());
        transacao.setChavePix(dto.getChavePix());
        transacao.setData(dto.getData());
        transacao.setAgencia(dto.getAgencia());
        transacao.setNumeroConta(dto.getNumeroConta());
        transacao.setDescricao(dto.getDescricao());
        return transacaoRepository.save(transacao);
    }

}
