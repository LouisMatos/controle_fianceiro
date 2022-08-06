package br.com.controlefinanceiro.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Transaction")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	private User usuario;

	private String bancoOrigem;

	private String agenciaOrigem;

	private String contaOrigem;

	private String bancoDestino;

	private String agenciaDestino;

	private String contaDestino;

	private BigDecimal valorTransacao;

	private LocalDate dataTransacao;

	private LocalDateTime dataImportacaoTransacoes;

	public LocalDateTime getDataImportacaoTransacoes() {
		return dataImportacaoTransacoes;
	}

	public void setDataImportacaoTransacoes(LocalDateTime dataImportacaoTransacoes) {
		this.dataImportacaoTransacoes = dataImportacaoTransacoes;
	}

	public String getBancoOrigem() {
		return bancoOrigem;
	}

	public void setBancoOrigem(String bancoOrigem) {
		this.bancoOrigem = bancoOrigem;
	}

	public String getAgenciaOrigem() {
		return agenciaOrigem;
	}

	public void setAgenciaOrigem(String agenciaOrigem) {
		this.agenciaOrigem = agenciaOrigem;
	}

	public String getContaOrigem() {
		return contaOrigem;
	}

	public void setContaOrigem(String contaOrigem) {
		this.contaOrigem = contaOrigem;
	}

	public String getBancoDestino() {
		return bancoDestino;
	}

	public void setBancoDestino(String bancoDestino) {
		this.bancoDestino = bancoDestino;
	}

	public String getAgenciaDestino() {
		return agenciaDestino;
	}

	public void setAgenciaDestino(String agenciaDestino) {
		this.agenciaDestino = agenciaDestino;
	}

	public String getContaDestino() {
		return contaDestino;
	}

	public void setContaDestino(String contaDestino) {
		this.contaDestino = contaDestino;
	}

	public BigDecimal getValorTransacao() {
		return valorTransacao;
	}

	public void setValorTransacao(BigDecimal valorTransacao) {
		this.valorTransacao = valorTransacao;
	}

	public LocalDate getDataTransacao() {
		return dataTransacao;
	}

	public void setDataTransacao(LocalDate dataTransacao) {
		this.dataTransacao = dataTransacao;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "Transacao [id=" + id + ", usuario=" + usuario + ", bancoOrigem=" + bancoOrigem + ", agenciaOrigem="
				+ agenciaOrigem + ", contaOrigem=" + contaOrigem + ", bancoDestino=" + bancoDestino
				+ ", agenciaDestino=" + agenciaDestino + ", contaDestino=" + contaDestino + ", valorTransacao="
				+ valorTransacao + ", dataTransacao=" + dataTransacao + ", dataImportacaoTransacoes="
				+ dataImportacaoTransacoes + "]";
	}

}
