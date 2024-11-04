package com.finance.financial_management_app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Failed to send email to " + to, e);
        }
    }

    public void sendVerificationCode(String email, String code) {
        MimeMessage simpleMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper message = new MimeMessageHelper(simpleMessage, true);
            message.setTo(email);
            message.setSubject("PerFinancial - Your Verification Code");
            message.setText("Your verification code is: <strong>" + code + "</strong><br><br><strong>Kind Regards</strong><br><strong>PerFinancial</strong>", true);
            mailSender.send(simpleMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Failed to send email to " + email, e);
        }
        
    }

}
