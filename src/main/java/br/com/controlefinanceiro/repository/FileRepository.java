package br.com.controlefinanceiro.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.controlefinanceiro.model.AccountDTO;
import br.com.controlefinanceiro.model.AgencyDTO;
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
	
	@Query(value = "Select sum(valor_transacao) as total from transaction where data_transacao like %:dataTransacao% and conta_origem = :conta and agencia_origem = :agencia and banco_origem = :banco HAVING total > 1000000000.00", nativeQuery = true)
	BigDecimal getAmountSuspiciousTransactionsOrigin(@Param("dataTransacao") String dataTransacao, @Param("conta") String conta,  @Param("agencia") String agencia, @Param("banco") String banco);
	
	@Query(value = "SELECT DISTINCT banco_origem as banco, t.conta_origem as conta, t.agencia_origem as agencia from transaction t where t.data_transacao like %:dataTransacao%", nativeQuery = true)
	List<AccountDTO> findAllAgencyAndAccountOrigin(@Param("dataTransacao") String dataTransacao);
	
	@Query(value = "SELECT DISTINCT banco_destino as banco, t.conta_destino as conta, t.agencia_destino as agencia from transaction t where t.data_transacao like %:dataTransacao%", nativeQuery = true)
	List<AccountDTO> findAllAgencyAndAccountDestination(@Param("dataTransacao") String dataTransacao);
	
	@Query(value = "Select sum(valor_transacao) as total from transaction where data_transacao like %:dataTransacao% and conta_destino = :conta and agencia_destino = :agencia and banco_destino = :banco HAVING total > 1000000000.00", nativeQuery = true)
	BigDecimal getValueSuspiciousTransactionsOutput(@Param("dataTransacao") String dataTransacao, @Param("conta") String conta,  @Param("agencia") String agencia, @Param("banco") String banco);

	@Query(value = "SELECT DISTINCT banco_origem as banco,  t.agencia_origem as agencia from transaction t where t.data_transacao like %:dataTransacao%", nativeQuery = true)
	List<AgencyDTO> findAllAgencyOrigin(@Param("dataTransacao") String dataTransacao);
	
	@Query(value = "Select sum(valor_transacao) as total from transaction where banco_origem = :banco and agencia_origem = :agencia and data_transacao like %:dataTransacao% HAVING total > 1000000000.00", nativeQuery = true)
	BigDecimal getValueAgencySuspectsOrigin(@Param("dataTransacao") String dataTransacao, @Param("agencia") String agencia, @Param("banco") String banco);
	
	@Query(value = "SELECT DISTINCT banco_destino as banco,  t.agencia_destino as agencia from transaction t where t.data_transacao like %:dataTransacao%", nativeQuery = true)
	List<AgencyDTO> findAllDestinationAgency(@Param("dataTransacao") String dataTransacao);
	
	@Query(value = "Select sum(valor_transacao) as total from transaction where banco_destino = :banco and agencia_destino = :agencia and data_transacao like %:dataTransacao% HAVING total > 1000000000.00", nativeQuery = true)
	BigDecimal getValueAgencySuspectsDestination(@Param("dataTransacao") String dataTransacao, @Param("agencia") String agencia, @Param("banco") String banco);

	
	
}
