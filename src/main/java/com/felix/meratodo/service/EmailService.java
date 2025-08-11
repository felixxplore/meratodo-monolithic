package com.felix.meratodo.service;

import com.felix.meratodo.config.EnvConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final String baseUrl= EnvConfig.get("${app.base-url}");

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender){
        this.mailSender=mailSender;
    }

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("MeraTodo Password Reset");
        String resetUrl="http://localhost:8080/reset-password?token="+token;
        helper.setText("Click <a href=\"" + resetUrl + "\">here</a> to reset your password.", true);
        mailSender.send(message);
    }

    public void sendTeamInvitationEmail(String to, String inviteUrl, String teamName, String role) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("MeraTodo Team Invitation");
        helper.setText(
            "You are invited to join the team: "+teamName+" as a "+role+" role."+
                    "Click <a href=\""+inviteUrl+"\">here</a> to accept.",true
        );
        mailSender.send(message);
    }


    public void sendTaskAssignmentEmail(String to, String taskTitle, String projectName) throws MessagingException {

        MimeMessage message= mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("MeraTodo Task Assignment");
        helper.setText("You have been assigned to task: "+taskTitle+" in project: "+projectName, true);
        mailSender.send(message);
    }

    public void sendVerificationEmail(String email, String token) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("MeraTodo Email Verification");
        String verificationUrl= baseUrl + "/api/auth/verify-email?token="+token;
        helper.setText("<h1>Verify Your Email</h1><p>Click  <a href=\"" + verificationUrl + "\">to verify your email</p>", true);
        mailSender.send(message);
    }
}
