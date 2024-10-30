package com.finance.financial_management_app.budget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.finance.financial_management_app.expense.ExpenseRepository;
import com.finance.financial_management_app.revenue.RevenueRepository;
import com.finance.financial_management_app.security.EmailService;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final RevenueRepository revenueRepository;
    private final ExpenseRepository expenseRepository;
    private final EmailService emailService;

    public BudgetService(BudgetRepository budgetRepository, RevenueRepository revenueRepository, ExpenseRepository expenseRepository, EmailService emailService) {
        this.budgetRepository = budgetRepository;
        this.revenueRepository = revenueRepository;
        this.expenseRepository = expenseRepository;
        this.emailService = emailService;
    }

    // Email cron to notify user's of their goals whether they were achieved or not
    @Scheduled(cron = "0 0 8 25 * ?")
    public void compareBudgetGoals() {
        LocalDate now = LocalDate.now();
        int currentMonthValue = now.getMonthValue(); // Get month as an integer (1-12)
        Month currentMonth = Month.fromInt(currentMonthValue); // Convert integer month to Enum
        int currentYear = now.getYear();

        // Fetch budget goals using the Enum representation
        List<Budget> budgetGoals = budgetRepository.findByMonthAndYear(currentMonth, currentYear);

        for (Budget goal : budgetGoals) {
            double totalRevenue = revenueRepository.sumByMonthAndYear(currentMonthValue, currentYear, goal.getUser());
            double totalExpense = expenseRepository.sumByMonthAndYear(currentMonthValue, currentYear, goal.getUser());

            String emailBody = "Budget Report for " + now.getMonth() + " " + currentYear + "<br><br>";
            boolean notify = false;

            BigDecimal totalRevenueBD = BigDecimal.valueOf(totalRevenue);
            BigDecimal minRevenueBD = goal.getMinRevenue();

            if (totalRevenueBD.compareTo(minRevenueBD) < 0) {
                emailBody += "<strong>Revenue goal not met</strong> <br>Goal set: R" + goal.getMinRevenue() + "<br>Actual revenue: R" + totalRevenueBD + "<br><br>";
                notify = true;
            }
            if (totalRevenueBD.compareTo(minRevenueBD) >= 0) {
                emailBody += "<strong>Revenue goal successfully met</strong> <br>Goal set: R" + goal.getMinRevenue() + "<br>Actual revenue: R" + totalRevenueBD + "<br><br>";
                notify = true;
            }

            BigDecimal totalExpenseBD = BigDecimal.valueOf(totalExpense);
            BigDecimal maxExpenseBD = goal.getMaxExpense();

            if (totalExpenseBD.compareTo(maxExpenseBD) > 0) {
                emailBody += "<strong>Expense limit exceeded</strong> <br>Goal set: R" + goal.getMaxExpense() + "<br>Actual Expense: R" + totalExpenseBD;
                notify = true;
            }
            else if (totalExpenseBD.compareTo(maxExpenseBD) <= 0) {
                emailBody += "<strong>Expense goal met</strong> <br>Goal set: R" + goal.getMaxExpense() + "<br>Actual Expense: R" + totalExpenseBD;
                notify = true;
            }

            emailBody += "<br><br><strong>Kind Regards</strong><br><strong>PerFinancial</strong>";

            if (notify) {
                emailService.sendEmail(goal.getUser().getEmail(), "Monthly Budget Report", emailBody);
                System.out.println("Email sent to user");
            }
        }
    }
}
