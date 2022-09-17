package br.com.controlefinanceiro.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.controlefinanceiro.model.AccountDTO;
import br.com.controlefinanceiro.model.AgencyDTO;
import br.com.controlefinanceiro.model.ImportDetailsDTO;
import br.com.controlefinanceiro.model.ImportPerformedDTO;
import br.com.controlefinanceiro.model.SuspectedAgency;
import br.com.controlefinanceiro.model.SuspiciousAccount;
import br.com.controlefinanceiro.model.Transaction;
import br.com.controlefinanceiro.model.User;
import br.com.controlefinanceiro.repository.FileRepository;
import groovy.util.logging.Slf4j;

@Slf4j
@Service
public class ImportsService {

	private static Logger log = LoggerFactory.getLogger(ImportsService.class);

	@Autowired
	private FileRepository fileRepository;

	private ArrayList<Transaction> transactions;
	private LocalDateTime dateFirstLine;
	private LocalDateTime importDate;
	private User user;

	public List<ImportPerformedDTO> findPerformedImports() {
		log.info("Carregando as Importações realizadas!");
		return fileRepository.findTransactionDateImportDate();
	}

	public void fileInfos(MultipartFile file) {
		double bytess = file.getSize();
		double kilobytes = (bytess / 1024);
		BigDecimal bigDecimal = BigDecimal.valueOf((kilobytes / 1024));

		log.info("Nome do Arquivo: {}", file.getOriginalFilename());
		log.info("Tamanho do Arquivo: {} MB", (bigDecimal.setScale(5, BigDecimal.ROUND_UP)));
	}

	@SuppressWarnings("resource")
	public void readFile(MultipartFile file) {
		try {
			BufferedReader fileReader = new BufferedReader(
					new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
			CSVParser csvParser;
			csvParser = new CSVParser(fileReader, CSVFormat.EXCEL);
			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				String csvLine = csvRecord.toString();
				log.info("Line: {}", csvLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("resource")
	public void processTransactions(MultipartFile file, Object session) {
		try {
			BufferedReader fileReader = new BufferedReader(
					new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
			CSVParser csvParser;
			csvParser = new CSVParser(fileReader, CSVFormat.EXCEL);
			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			importDate = LocalDateTime.now();
			transactions = new ArrayList<>();
			user = (User) session;

			for (CSVRecord csvRecord : csvRecords) {

				Transaction transaction = convertTransactionToObject(csvRecord);

				if (csvRecord.getRecordNumber() == 1) {
					dateFirstLine = LocalDateTime.parse(csvRecord.get(7));
				}

				if (transaction != null && transaction.getDataTransacao().equals(dateFirstLine.toLocalDate())) {
					transactions.add(transaction);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveBankTransaction() throws SQLIntegrityConstraintViolationException {
		if (fileRepository.findByDateTransaction(dateFirstLine.toLocalDate()) == 0) {
			fileRepository.saveAll(transactions);
			log.info("Transações salvas nos banco");
		} else {
			throw new SQLIntegrityConstraintViolationException();
		}
	}

	public ImportDetailsDTO fetchImportDetails(String transactionDate) {
		return fileRepository.findImportDatails(transactionDate);
	}

	public List<Transaction> searchTransactionsDetailedDate(String transactionDate) {
		return fileRepository.findByDetailedTransactionDate(transactionDate);
	}

	public List<Transaction> analisarTransacoesSuspeitasData(String analysisDate) {
		List<Transaction> transactionsDetails = fileRepository.findByDetailedTransactionDate(handleSurveyDate(analysisDate));

		List<Transaction> suspiciousTransactions = new ArrayList<>();

		for (Transaction transaction : transactionsDetails) {
			if (transaction.getValorTransacao().compareTo(new BigDecimal("100000.00")) >= 0) {
				suspiciousTransactions.add(transaction);
			}
		}

		return suspiciousTransactions;
	}

	public List<SuspiciousAccount> analyzeSuspiciousAccounts(String analysisDate) {
		ArrayList<SuspiciousAccount> suspiciousAccounts = new ArrayList<>();

		// Aqui estou verificando as contas suspeitas de origem,quer dizer que estão
		// mandando valores suspeitos, então vai virar valores de saida no front(SAIDA)
		List<AccountDTO> originAccounts = fileRepository.findAllAgencyAndAccountOrigin(handleSurveyDate(analysisDate));

		for (AccountDTO conta : originAccounts) {
			SuspiciousAccount suspiciousAccount = new SuspiciousAccount();
			suspiciousAccount.setBanco(conta.getBanco());
			suspiciousAccount.setAgencia(conta.getAgencia());
			suspiciousAccount.setConta(conta.getConta());
			suspiciousAccount.setValorSuspeito(String.valueOf(fileRepository.getAmountSuspiciousTransactionsOrigin(
					handleSurveyDate(analysisDate), conta.getConta(), conta.getAgencia(), conta.getBanco())));
			suspiciousAccount.setTipoMovimentacao("SAIDA");
			if (!suspiciousAccount.getValorSuspeito().equals("null")) {
				suspiciousAccounts.add(suspiciousAccount);
			}
		}

		// Aqui estou verificando as contas suspeitas de destino,quer dizer que estão
		// recebendo valores suspeitos, então vai virar valores de Entrada no
		// front(Entrada)
		List<AccountDTO> destinyAccounts = fileRepository
				.findAllAgencyAndAccountDestination(handleSurveyDate(analysisDate));

		for (AccountDTO conta : destinyAccounts) {

			SuspiciousAccount suspiciousAccount = new SuspiciousAccount();
			suspiciousAccount.setBanco(conta.getBanco());
			suspiciousAccount.setAgencia(conta.getAgencia());
			suspiciousAccount.setConta(conta.getConta());
			suspiciousAccount.setValorSuspeito(String.valueOf(fileRepository.getValueSuspiciousTransactionsOutput(
					handleSurveyDate(analysisDate), conta.getConta(), conta.getAgencia(), conta.getBanco())));
			suspiciousAccount.setTipoMovimentacao("ENTRADA");
			if (!suspiciousAccount.getValorSuspeito().equals("null")) {
				suspiciousAccounts.add(suspiciousAccount);
			}
		}
		return suspiciousAccounts;
	}

	public List<SuspectedAgency> analisarAgenciasSuspeitas(String analysisDate) {

		ArrayList<SuspectedAgency> suspectedAgencies = new ArrayList<>();

		List<AgencyDTO> agencyOrigin = fileRepository.findAllAgencyOrigin(handleSurveyDate(analysisDate));

		for (AgencyDTO agency : agencyOrigin) {
			SuspectedAgency suspectedAgency = new SuspectedAgency();
			suspectedAgency.setAgencia(agency.getAgencia());
			suspectedAgency.setBanco(agency.getBanco());
			suspectedAgency.setTipoMovimentacao("SAIDA");
			suspectedAgency.setValorSuspeito(String.valueOf(fileRepository.getValueAgencySuspectsOrigin(
					handleSurveyDate(analysisDate), agency.getAgencia(), agency.getBanco())));

			if (!suspectedAgency.getValorSuspeito().equals("null")) {
				suspectedAgencies.add(suspectedAgency);

			}
		}

		List<AgencyDTO> agencyDestination = fileRepository.findAllDestinationAgency(handleSurveyDate(analysisDate));

		for (AgencyDTO agency : agencyDestination) {
			SuspectedAgency suspectedAgency = new SuspectedAgency();
			suspectedAgency.setAgencia(agency.getAgencia());
			suspectedAgency.setBanco(agency.getBanco());
			suspectedAgency.setTipoMovimentacao("ENTRADA");
			suspectedAgency.setValorSuspeito(String.valueOf(fileRepository.getValueAgencySuspectsDestination(
					handleSurveyDate(analysisDate), agency.getAgencia(), agency.getBanco())));

			if (!suspectedAgency.getValorSuspeito().equals("null")) {
				suspectedAgencies.add(suspectedAgency);

			}
		}

		return suspectedAgencies;
	}

	private Transaction convertTransactionToObject(CSVRecord csvRecord) {
		Transaction transaction = new Transaction();

		transaction.setBancoOrigem(csvRecord.get(0));
		transaction.setAgenciaOrigem(csvRecord.get(1));
		transaction.setContaOrigem(csvRecord.get(2));
		transaction.setBancoDestino(csvRecord.get(3));
		transaction.setAgenciaDestino(csvRecord.get(4));
		transaction.setContaDestino(csvRecord.get(5));
		transaction.setValorTransacao(
				(csvRecord.get(6).trim().isEmpty()) ? null : new BigDecimal(csvRecord.get(6)).setScale(2));
		transaction.setDataTransacao(LocalDateTime.parse(csvRecord.get(7)).toLocalDate());
		transaction.setDataImportacaoTransacoes(this.importDate);
		transaction.setUsuario(user);

		if (validator(transaction)) {
			return transaction;
		}

		return null;
	}

	private static boolean validator(Object myObject) {
		Field[] fields = myObject.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				Object objectValue = field.get(myObject);
				if ((objectValue == null || objectValue.toString().length() == 0) && !field.getName().equals("id")) {
					return false;
				}
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				log.error(String.valueOf(ex));
			}
		}
		return true;
	}

	private String handleSurveyDate(String analysisDate) {

		String[] dataSplit = analysisDate.split("/");

		return dataSplit[1] + "-" + dataSplit[0];
	}

}
