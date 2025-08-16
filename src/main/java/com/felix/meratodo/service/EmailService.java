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


    public void sendEmail(String to, String subject, String bodyHtml) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(bodyHtml, true);
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        String resetUrl="http://localhost:8080/reset-password?token="+token;
        sendEmail(to,"MeraTodo Password Reset", "Click <a href=\"" + resetUrl + "\">here</a> to reset your password.");
    }

    public void sendTeamInvitationEmail(String to, String inviteUrl, String teamName, String role) throws MessagingException {
        sendEmail(to,"MeraTodo Team Invitation",    "You are invited to join the team: "+teamName+" as a "+role+" role."+
                "Click <a href=\""+inviteUrl+"\">here</a> to accept.");
    }


    public void sendTaskAssignmentEmail(String to, String taskTitle, String projectName) throws MessagingException {
        sendEmail(to, "MeraTodo Task Assignment","You have been assigned to task: "+taskTitle+" in project: "+projectName);
    }

    public void sendVerificationEmail(String to, String token) throws MessagingException {
        String verificationUrl= baseUrl + "/api/auth/verify-email?token="+token;
        sendEmail(to, "MeraTodo Email Verification", "<h1>Verify Your Email</h1><p>Click  <a href=\"" + verificationUrl + "\">to verify your email</p>" );
    }

    public void sendOtpEmail(String to, String otp) throws MessagingException {
        sendEmail(to,"MeraTodo OTP Verification", "<h2>Your OTP code </h2> <p> <b>"+otp+"</b> </p>"+"<p> Your OTP code expire within  5 minutes </p>");

    }
}
