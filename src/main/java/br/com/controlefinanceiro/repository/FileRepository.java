package br.com.controlefinanceiro.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.controlefinanceiro.model.ImportDetailsDTO;
import br.com.controlefinanceiro.model.ImportPerformedDTO;
import br.com.controlefinanceiro.model.Transaction;

public interface FileRepository extends JpaRepository<Transaction, Long> {

	@Query(value = "SELECT count(*) FROM transaction WHERE data_transacao LIKE %:dateTransaction%", nativeQuery = true)
	Integer findByDateTransaction(@Param("dateTransaction") LocalDate dateTransaction);
	
	@Query(value = "SELECT * FROM transaction WHERE data_transacao LIKE %:transactionDate%", nativeQuery = true)
	List<Transaction> findByDetailedTransactionDate(@Param("transactionDate") String transactionDate);
	
	@Query(value = "SELECT distinct  t.data_transacao as dataTransacoes, t.data_importacao_transacoes as dataImportacao, u.nome as nomeUsuario FROM transaction t JOIN usuarios as u ON t.usuario_id = u.id", nativeQuery = true)
	List<ImportPerformedDTO> findTransactionDateImportDate();
	
	@Query(value = "SELECT distinct  t.data_transacao as dataTransacoes, t.data_importacao_transacoes as dataImportacao, u.nome as nomeUsuario FROM transaction t JOIN usuarios as u ON t.usuario_id = u.id WHERE t.data_transacao LIKE %:transactionDate%", nativeQuery = true)
	ImportDetailsDTO findImportDatails(@Param("transactionDate") String transactionDate);

}
