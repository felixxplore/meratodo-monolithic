package com.felix.meratodo.service;

import com.felix.meratodo.model.Task;
import com.felix.meratodo.model.User;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private EmailService emailService;


    public void notifyAssigneesForTask(Task task) throws MessagingException {

        for(User assignee:task.getAssignees()){
            emailService.sendTaskAssignmentEmail(
                assignee.getEmail(),
                    task.getTitle(),
                    task.getProject().getName()
            );
        }
    }
}
