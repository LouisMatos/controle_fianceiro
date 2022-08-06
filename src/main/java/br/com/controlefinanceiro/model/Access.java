package br.com.controlefinanceiro.model;

import java.time.Duration;
import java.time.LocalDateTime;


public class Access {

	private Integer id;
	private String path;
	private LocalDateTime date;
	private Duration duration;
	private String name;
	private String email;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Access [id=" + id + ", path=" + path + ", date=" + date + ", duration=" + duration + ", name=" + name
				+ ", email=" + email + "]";
	}

}
