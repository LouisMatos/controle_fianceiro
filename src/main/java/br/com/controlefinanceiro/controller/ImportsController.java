package br.com.controlefinanceiro.controller;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.controlefinanceiro.service.ImportsService;

@Controller
@RequestMapping("/imports")
public class ImportsController {

	@Autowired
	private ImportsService importsService;

	@GetMapping
	public String loadTransactions(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("importacoesRealizadas", importsService.findPerformedImports());
		return "redirect:/transaction/imports";
	}

	@PostMapping("/upload/file")
	public String singleFileUpload(@RequestParam("arquivo") MultipartFile file, RedirectAttributes redirectAttributes) {

		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("messageError",
					"Arquivo vazio ou selecione um arquivo para fazer upload!");
			return "redirect:/transaction/imports";
		}

		importsService.fileInfos(file);

		importsService.readFile(file);

		importsService.processTransactions(file, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		try {
			importsService.saveBankTransaction();
			redirectAttributes.addFlashAttribute("message",
					"Você carregou o arquivo '" + file.getOriginalFilename() + "' com sucesso!");
			redirectAttributes.addFlashAttribute("importacoesRealizadas", importsService.findPerformedImports());
		} catch (SQLIntegrityConstraintViolationException e) {
			redirectAttributes.addFlashAttribute("messageError",
					"As transações já foram processadas e registradas no sitema para o dia informado!");
			return "redirect:/transaction/imports";
		}

		return "redirect:/transaction/imports";
	}
	
	@GetMapping("/details/{transaction_date}")
	public String detalhar(@PathVariable("transaction_date") String transactionDate, RedirectAttributes redirectAttributes) {
		
		redirectAttributes.addFlashAttribute("detalhesImportacao", importsService.fetchImportDetails(transactionDate));
		
		redirectAttributes.addFlashAttribute("transacoesDetalhadas", importsService.searchTransactionsDetailedDate(transactionDate));
		return "redirect:/transaction/importDetails";
	}

}