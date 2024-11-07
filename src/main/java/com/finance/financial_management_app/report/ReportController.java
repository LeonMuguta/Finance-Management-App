package com.finance.financial_management_app.report;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.finance.financial_management_app.user.*;
import org.springframework.core.io.InputStreamResource;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/reports")
public class ReportController {
    public final ReportService reportService;
    public final UserRepository userRepository;

    public ReportController(ReportService reportService, UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadReport(@RequestParam int year, @RequestParam int month, @RequestParam int userId) throws IOException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        ByteArrayInputStream reportStream = reportService.generateMonthlyReport(user, year, month);

        if (reportStream == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No info present for the selected Month and Year");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Monthly_Report_" + year + "_" + month + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(reportStream));
    }
}
