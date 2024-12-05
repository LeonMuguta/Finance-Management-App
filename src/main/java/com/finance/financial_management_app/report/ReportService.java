package com.finance.financial_management_app.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.finance.financial_management_app.budget.*;
import com.finance.financial_management_app.expense.*;
import com.finance.financial_management_app.revenue.*;
import com.finance.financial_management_app.user.User;

@Service
public class ReportService {
    private final RevenueRepository revenueRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public ReportService(RevenueRepository revenueRepository, ExpenseRepository expenseRepository, BudgetRepository budgetRepository) {
        this.revenueRepository = revenueRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    public ByteArrayInputStream generateMonthlyReport(User user, int year, int month) throws IOException {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Revenue> revenues = revenueRepository.findByUserAndDateBetween(user, startDate, endDate);
        List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, startDate, endDate);
        Optional<Budget> budgetOpt = budgetRepository.findByUserAndMonthAndYear(user, Month.fromInt(month), year);

        if (revenues.isEmpty() && expenses.isEmpty() && budgetOpt.isEmpty()) {
            return null;
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Monthly Report");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            Font greenFont = workbook.createFont();
            greenFont.setColor(IndexedColors.GREEN.getIndex());

            Font redFont = workbook.createFont();
            redFont.setColor(IndexedColors.RED.getIndex());

            // Define a style for headers and cells with borders
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFont(headerFont);

            CellStyle greenCellStyle = workbook.createCellStyle();
            greenCellStyle.cloneStyleFrom(headerStyle); // Inherit borders and alignment
            greenCellStyle.setFont(greenFont);

            CellStyle redCellStyle = workbook.createCellStyle();
            redCellStyle.cloneStyleFrom(headerStyle);
            redCellStyle.setFont(redFont);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setWrapText(true);

            // Headers
            String[] columns = {"Type", "Date", "Amount", "Category", "Description", "Recurring"};
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(col, 5000);
            }

            DecimalFormat df = new DecimalFormat("#.00");

            // Revenue data with cell style
            int rowIdx = 1;
            for (Revenue revenue : revenues) {
                Row row = sheet.createRow(rowIdx++);
                createStyledCell(row, 0, "Revenue", cellStyle);
                createStyledCell(row, 1, revenue.getDate().toString(), cellStyle);
                createStyledCell(row, 2, "R" + df.format(revenue.getAmount().doubleValue()), greenCellStyle);
                createStyledCell(row, 3, revenue.getCategory(), cellStyle);
                createStyledCell(row, 4, revenue.getDescription(), cellStyle);
                if (!revenue.getIsRecurring()) {
                    createStyledCell(row, 5, "No", cellStyle);
                } else {
                    createStyledCell(row, 5, "Yes", cellStyle);
                }
                row.setHeight((short) -1);
            }

            // Expense data with cell style
            for (Expense expense : expenses) {
                Row row = sheet.createRow(rowIdx++);
                createStyledCell(row, 0, "Expense", cellStyle);
                createStyledCell(row, 1, expense.getDate().toString(), cellStyle);
                createStyledCell(row, 2, "R" + df.format(expense.getAmount().doubleValue()), redCellStyle);
                createStyledCell(row, 3, expense.getCategory(), cellStyle);
                createStyledCell(row, 4, expense.getDescription(), cellStyle);
                if (!expense.getIsRecurring()) {
                    createStyledCell(row, 5, "No", cellStyle);
                } else {
                    createStyledCell(row, 5, "Yes", cellStyle);
                }
                row.setHeight((short) -1);
            }

            // Add a blank row for spacing before budget goals
            rowIdx++;

            // Budget goals data
            Row budgetHeaderRow = sheet.createRow(rowIdx++);
            createMergedStyledCell(budgetHeaderRow, 0, "Budget Goals", headerStyle, sheet, 0, 1);

            if (budgetOpt.isPresent()) {
                Budget budget = budgetOpt.get();
                createStyledCell(sheet.createRow(rowIdx++), 0, "Minimum Revenue Goal", cellStyle);
                createStyledCell(sheet.getRow(rowIdx - 1), 1, "R" + df.format(budget.getMinRevenue().doubleValue()), cellStyle);

                createStyledCell(sheet.createRow(rowIdx++), 0, "Maximum Expense Goal", cellStyle);
                createStyledCell(sheet.getRow(rowIdx - 1), 1, "R" + df.format(budget.getMaxExpense().doubleValue()), cellStyle);

                createStyledCell(sheet.createRow(rowIdx++), 0, "Net Balance Goal", cellStyle);
                createStyledCell(sheet.getRow(rowIdx - 1), 1, "R" + df.format(budget.getNetBalanceGoal().doubleValue()), cellStyle);
            } else {
                Row noDataRow = sheet.createRow(rowIdx++);
                createMergedStyledCell(noDataRow, 0, "No budget goals set for this month.", cellStyle, sheet, 0, 1);

            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // Custom method to create a styled cell and apply borders to merged regions
    private void createMergedStyledCell(Row row, int startCol, String value, CellStyle style, Sheet sheet, int mergeStart, int mergeEnd) {
        Cell cell = row.createCell(startCol);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        // Merge the cells
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), mergeStart, mergeEnd));

        // Apply the border style to each cell in the merged region
        for (int i = mergeStart + 1; i <= mergeEnd; i++) {
            Cell borderCell = row.getCell(i);
            if (borderCell == null) { 
                borderCell = row.createCell(i); 
            }
            borderCell.setCellStyle(style);
        }
    }

    // Helper method to create a cell with specified style and value
    private void createStyledCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
