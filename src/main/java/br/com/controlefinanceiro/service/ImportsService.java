package br.com.controlefinanceiro.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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

import br.com.controlefinanceiro.model.ImportDetailsDTO;
import br.com.controlefinanceiro.model.ImportPerformedDTO;
import br.com.controlefinanceiro.model.Transaction;
import br.com.controlefinanceiro.model.User;
import br.com.controlefinanceiro.repository.FileRepository;
import groovy.util.logging.Slf4j;

@Slf4j
@Service
public class ImportsService {

	private static Logger Log = LoggerFactory.getLogger(ImportsService.class);
	
	@Autowired
	private FileRepository fileRepository;

	private ArrayList<Transaction> transactions;
	private LocalDateTime dateFirstLine;
	private LocalDateTime importDate;
	private User user;

	

	public List<ImportPerformedDTO> findPerformedImports() {
		Log.info("Carregando as Importações realizadas!");
		return fileRepository.findTransactionDateImportDate();
	}
	
	public void fileInfos(MultipartFile file) {
		double bytess = file.getSize();
		double kilobytes = (bytess / 1024);
		BigDecimal bigDecimal = new BigDecimal((kilobytes / 1024));

		Log.info("Nome do Arquivo: {}", file.getOriginalFilename());
		Log.info("Tamanho do Arquivo: {}", bigDecimal.setScale(5, BigDecimal.ROUND_UP) + " MB");
	}

	@SuppressWarnings("resource")
	public void readFile(MultipartFile file) {
		try {
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
			CSVParser csvParser;
			csvParser = new CSVParser(fileReader, CSVFormat.EXCEL);
			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				Log.info(csvRecord.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("resource")
	public void processTransactions(MultipartFile file, Object session) {
		try {
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
			CSVParser csvParser;
			csvParser = new CSVParser(fileReader, CSVFormat.EXCEL);
			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			importDate = LocalDateTime.now();
			transactions = new ArrayList<>();
			user = (User) session;

			for (CSVRecord csvRecord : csvRecords) {

				Transaction transaction = convertTransactionToObject(csvRecord, user);

				if (csvRecord.getRecordNumber() == 1) {
					dateFirstLine = LocalDateTime.parse(csvRecord.get(7));
				}

				if (transaction != null) {
					if (transaction.getDataTransacao().equals(dateFirstLine.toLocalDate())) {
						transactions.add(transaction);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveBankTransaction() throws SQLIntegrityConstraintViolationException {
		if (fileRepository.findByDateTransaction(dateFirstLine.toLocalDate()) == 0) {
			fileRepository.saveAll(transactions);
			Log.info("Transações salvas nos banco: {}", transactions.toString());
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

	private Transaction convertTransactionToObject(CSVRecord csvRecord, User user2) {
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
			field.setAccessible(true);
			try {
				Object objectValue = field.get(myObject);
				if ((objectValue == null || objectValue.toString().length() == 0) && !field.getName().equals("id")) {
					return false;
				}
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				System.out.println(ex);
			}
		}
		return true;
	}	

}
