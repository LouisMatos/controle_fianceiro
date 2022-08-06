package br.com.controlefinanceiro.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.controlefinanceiro.enums.EnumModule;

@Entity
@Table(name = "papeis")
public class Paper implements GrantedAuthority {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private EnumModule nome;

	@ManyToOne
	@JsonIgnore
	private User usuario;

	@Override
	public String getAuthority() {
		return nome.toString();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public EnumModule getNome() {
		return nome;
	}

	public void setNome(EnumModule nome) {
		this.nome = nome;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "Papel [id=" + id + ", nome=" + nome + "]";
	}

}
