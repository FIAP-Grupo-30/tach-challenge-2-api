package tach_challenge_2.api_financeiro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tach_challenge_2.api_financeiro.model.TransacaoFinanceira;

public interface TransacaoRepository extends JpaRepository<TransacaoFinanceira, Long> {
}


