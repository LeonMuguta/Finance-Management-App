package com.finance.financial_management_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@SpringBootApplication
@EnableScheduling
@EnableJdbcHttpSession
@EnableJpaRepositories(basePackages = {"com.finance.financial_management_app.security", "com.finance.financial_management_app.user", "com.finance.financial_management_app.revenue", "com.finance.financial_management_app.expense", "com.finance.financial_management_app.budget", "com.finance.financial_management_app.verify"})
public class FinancialManagementAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialManagementAppApplication.class, args);
	}

}
