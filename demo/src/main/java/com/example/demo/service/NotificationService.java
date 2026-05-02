package com.example.demo.service;

import com.example.demo.model.Appointment;
import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification sendNotification(Appointment appointment, String channel, String template) {
        Notification n = new Notification();
        n.setAppointment(appointment);
        n.setChannel(channel);
        n.setTemplate(template);
        n.setSentAt(LocalDateTime.now());
        n.setStatus("SENT");
        return notificationRepository.save(n);
    }
}
