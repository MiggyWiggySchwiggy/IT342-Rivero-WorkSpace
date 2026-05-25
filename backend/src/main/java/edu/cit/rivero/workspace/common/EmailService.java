package edu.cit.rivero.workspace.common;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:hello@workspace.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String toEmail, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to WorkSpace!");
        message.setText("Hi " + firstName + ",\n\n" +
                "Welcome to WorkSpace! Your account has been successfully created.\n\n" +
                "You can now browse and book the best workspaces for your needs.\n\n" +
                "Best regards,\n" +
                "The WorkSpace Team");
        
        try {
            mailSender.send(message);
            System.out.println("Welcome email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email to " + toEmail + ": " + e.getMessage());
        }
    }

    public void sendLoginAlertEmail(String toEmail, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Security Alert: New Login to WorkSpace");
        message.setText("Hi " + firstName + ",\n\n" +
                "We detected a new login to your WorkSpace account.\n\n" +
                "If this was you, no further action is needed. If you did not authorize this login, please contact support immediately.\n\n" +
                "Best regards,\n" +
                "The WorkSpace Team");
        
        try {
            mailSender.send(message);
            System.out.println("Login alert email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send login alert to " + toEmail + ": " + e.getMessage());
        }
    }

    public void sendBookingConfirmation(String toEmail, String firstName, String spaceName, String date, String time) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("WorkSpace Booking Confirmed: " + spaceName);
        message.setText("Hi " + firstName + ",\n\n" +
                "Your booking is confirmed!\n\n" +
                "Details:\n" +
                "Workspace: " + spaceName + "\n" +
                "Date: " + date + "\n" +
                "Time: " + time + "\n\n" +
                "Thank you for choosing WorkSpace.\n\n" +
                "Best regards,\n" +
                "The WorkSpace Team");
        
        try {
            mailSender.send(message);
            System.out.println("Booking confirmation email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send booking confirmation to " + toEmail + ": " + e.getMessage());
        }
    }
}
