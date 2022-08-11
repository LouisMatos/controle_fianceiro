package br.com.controlefinanceiro.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.controlefinanceiro.model.UserDTO;
import br.com.controlefinanceiro.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	
	@GetMapping("/list")
	public String loadTransactions(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("usuarios", userService.searchForRegisteredUsers());
		return "redirect:/user/list";
	}

	@PostMapping("/newUser")
	public String novo(@Valid UserDTO userDTO, RedirectAttributes redirectAttributes) {

		if (userService.emailAlreadyRegistered(userDTO)) {
			redirectAttributes.addFlashAttribute("messageError", "Email já cadastrado!");
			return "redirect:/user/register";
		} else {
			userService.createNewUser(userDTO);
		}

		return "redirect:/user/list";
	}

}
