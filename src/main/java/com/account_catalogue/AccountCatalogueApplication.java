package com.account_catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccountCatalogueApplication {

	/**
	 * Punto de entrada principal para la aplicación. Este método es invocado cuando
	 * la aplicación se inicia. Es responsable de iniciar el contexto de la
	 * aplicación
	 * de Spring y de transferir el control al marco de trabajo.
	 *
	 * @param args los argumentos de la línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(AccountCatalogueApplication.class, args);
	}

}
