package br.com.controlefinanceiro.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ImportDetailsDTO {

	public LocalDate getDataTransacoes();

	public LocalDateTime getDataImportacao();

	public String getNomeUsuario();
	
}
