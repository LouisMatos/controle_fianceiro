package br.com.controlefinanceiro.enums;

public enum EnumModule {
	
	ADM("ROLE_ADMINISTRADOR"), USUARIO("ROLE_USUARIO");

	private String modulo;

	private EnumModule(String modulo) {
		this.modulo = modulo;
	}

	@Override
	public String toString() {
		return this.modulo;
	}

}
