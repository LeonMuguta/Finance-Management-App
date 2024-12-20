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
            message.setText("Your verification code is: <strong>" + code + "</strong> 👀<br><br><strong>Kind Regards</strong><br><strong>PerFinancial</strong>", true);
            mailSender.send(simpleMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Failed to send email to " + email, e);
        }
    }

    public void sendRegistrationEmail(String email) {
        MimeMessage simpleMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper message = new MimeMessageHelper(simpleMessage, true);
            message.setTo(email);
            message.setSubject("PerFinancial - Successful Registration");
            message.setText("Good day, <br><br>You have successfully registered your account on PerFinancial! 😁<br><br>Enjoy, and be sure to be in touch with us if you experience any problems (perfinancial@helpdesk.com)." +
                            "<br><br><strong>Kind Regards</strong><br><strong>PerFinancial</strong>", true);
            mailSender.send(simpleMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Failed to send email to " + email, e);
        } 
    }

    public void sendDeletionEmail(String email) {
        MimeMessage simpleMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper message = new MimeMessageHelper(simpleMessage, true);
            message.setTo(email);
            message.setSubject("PerFinancial - Account Deleted");
            message.setText("Good day, <br><br>Your PerFinancial account has been deleted successfully!<br><br>We're really sad to see you go, but thank you for using our services. All the best! 😊" +
                            "<br><br><strong>Kind Regards</strong><br><strong>PerFinancial</strong>", true);
            mailSender.send(simpleMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Failed to send email to " + email, e);
        } 
    }

    public void sendFirstWarningEmail(String email) {
        MimeMessage simpleMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper message = new MimeMessageHelper(simpleMessage, true);
            message.setTo(email);
            message.setSubject("PerFinancial - Expenses Warning");
            message.setText("Good day, <br><br>Please note that your expenses have taken between 50%-74% or more of your total Revenue. ⚠️<br><br>You may want to reduce your spending just a bit in order to meet your goals. 💡" +
                            "<br><br><strong>Kind Regards</strong><br><strong>PerFinancial</strong>", true);
            mailSender.send(simpleMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Failed to send email to " + email, e);
        } 
    }

    public void sendSecondWarningEmail(String email) {
        MimeMessage simpleMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper message = new MimeMessageHelper(simpleMessage, true);
            message.setTo(email);
            message.setSubject("PerFinancial - Expenses Warning");
            message.setText("Good day, <br><br>Please note that your expenses have taken between 75%-89% of your total Revenue. ⚠️<br><br>You may want to reduce your spending just a bit in order to meet your goals. 💡" +
                            "<br><br><strong>Kind Regards</strong><br><strong>PerFinancial</strong>", true);
            mailSender.send(simpleMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Failed to send email to " + email, e);
        } 
    }

    public void sendThirdWarningEmail(String email) {
        MimeMessage simpleMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper message = new MimeMessageHelper(simpleMessage, true);
            message.setTo(email);
            message.setSubject("PerFinancial - Expenses Warning");
            message.setText("Good day, <br><br>Please note that your expenses have taken up 90% or more of your total Revenue. ⚠️<br><br>You may want to reduce your spending just a bit in order to meet your goals. 💡" +
                            "<br><br><strong>Kind Regards</strong><br><strong>PerFinancial</strong>", true);
            mailSender.send(simpleMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Failed to send email to " + email, e);
        } 
    }

}
