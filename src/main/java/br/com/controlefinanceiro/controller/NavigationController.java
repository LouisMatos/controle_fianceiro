package br.com.controlefinanceiro.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class NavigationController {

	@GetMapping
	public String home(Model model, Principal principal) {
		return "home";
	}

	@GetMapping("transaction/imports")
	public String imports(Model model, Principal principal) {
		return "transaction/imports";
	}
	
	@GetMapping("transaction/importDetails")
	public String importDetails(Model model, Principal principal) {
		return "transaction/importDetails";
	}
	
	@GetMapping("transaction/analysis")
	public String analysis(Model model, Principal principal) {
		return "transaction/suspiciousTransactions";
	}

	@GetMapping("login")
	public String login() {
		return "index";
	}

	@PostMapping("error")
	public String error() {
		return "error";
	}
}
