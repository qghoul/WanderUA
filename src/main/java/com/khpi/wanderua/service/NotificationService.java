package com.khpi.wanderua.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.khpi.wanderua.enums.VerifyRequestType;

/**
 * Service for sending WebSocket notifications.
 * <p>
 * Provides a simple interface to send real-time notifications to connected clients
 * via WebSocket using Spring's SimpMessagingTemplate.
 * </p>
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send a notification message to all subscribed clients.
     */
    public void sendNotification(String message) {
        logger.info("Sending WebSocket notification: {}", message);
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }

    public String createNotificationMessage(String businessName, boolean status, String adminComment, VerifyRequestType type) {
        String statusMapping = status ? "approved" : "rejected";
        return String.format("Your %s request for %s has been %s. Administration comment: %s",
                         type.getDescription(),
                         businessName,
                         statusMapping, 
                         adminComment);
    }
} 