package com.felix.meratodo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

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
        helper.setSubject("Meratodo Team Invitation");
        helper.setText(
            "You are invited to join the team: "+teamName+" as a "+role+" role."+
                    "Click <a href=\""+inviteUrl+"\">here</a> to accept.",true
        );

        mailSender.send(message);
    }
}
