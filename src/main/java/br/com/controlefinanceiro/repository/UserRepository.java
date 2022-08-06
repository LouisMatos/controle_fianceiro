package br.com.controlefinanceiro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.controlefinanceiro.model.RegisteredUsersDTO;
import br.com.controlefinanceiro.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {


	@Query(value = "SELECT * FROM usuarios u WHERE u.email = :email and u.status = 1", nativeQuery = true)
	Optional<User> findByEmail(@Param("email") String email);

	@Cacheable("findAllUser")
	@Query(value = "SELECT u.id, u.nome, u.email FROM usuarios u WHERE status = true", nativeQuery = true)
	List<RegisteredUsersDTO> findUsuariosCadastrados();

	Optional<User> findByEmailAndSenha(String email, String senha);
}
