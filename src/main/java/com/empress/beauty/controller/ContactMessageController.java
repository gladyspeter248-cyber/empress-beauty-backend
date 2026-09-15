
package com.empress.beauty.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empress.beauty.entity.ContactMessage;
import com.empress.beauty.entity.MessageStatus;
import com.empress.beauty.repository.ContactMessageRepository;

@RestController
@RequestMapping("/api/contact-messages")
@CrossOrigin(origins = "http://localhost:5173")
public class ContactMessageController {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageController(
            ContactMessageRepository contactMessageRepository) {

        this.contactMessageRepository = contactMessageRepository;
    }

    // =========================================================
    // CUSTOMER SENDS A CONTACT MESSAGE
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createMessage(
            @RequestBody ContactMessage contactMessage) {

        try {

            // -----------------------------
            // VALIDATE NAME
            // -----------------------------

            if (contactMessage.getName() == null ||
                    contactMessage.getName().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Name is required.");
            }


            // -----------------------------
            // VALIDATE EMAIL
            // -----------------------------

            if (contactMessage.getEmail() == null ||
                    contactMessage.getEmail().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Email is required.");
            }


            // -----------------------------
            // VALIDATE SUBJECT
            // -----------------------------

            if (contactMessage.getSubject() == null ||
                    contactMessage.getSubject().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Subject is required.");
            }


            // -----------------------------
            // VALIDATE MESSAGE
            // -----------------------------

            if (contactMessage.getMessage() == null ||
                    contactMessage.getMessage().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Message is required.");
            }


            // =================================================
            // CLEAN USER INPUT
            // =================================================

            contactMessage.setName(
                    contactMessage.getName().trim()
            );

            contactMessage.setEmail(
                    contactMessage.getEmail().trim()
            );

            if (contactMessage.getPhone() != null) {

                contactMessage.setPhone(
                        contactMessage.getPhone().trim()
                );
            }

            contactMessage.setSubject(
                    contactMessage.getSubject().trim()
            );

            contactMessage.setMessage(
                    contactMessage.getMessage().trim()
            );


            // =================================================
            // NEW MESSAGES ARE UNREAD
            // =================================================

            contactMessage.setStatus(
                    MessageStatus.UNREAD
            );


            // =================================================
            // SAVE TO DATABASE
            // =================================================

            ContactMessage savedMessage =
                    contactMessageRepository.save(contactMessage);


            // =================================================
            // RETURN SUCCESS
            // =================================================

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedMessage);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Unable to send your message. Please try again."
                    );
        }
    }


    // =========================================================
    // ADMIN GETS ALL CONTACT MESSAGES
    // =========================================================

    @GetMapping
    public ResponseEntity<List<ContactMessage>> getAllMessages() {

        List<ContactMessage> messages =
                contactMessageRepository
                        .findAllByOrderByCreatedAtDesc();

        return ResponseEntity.ok(messages);
    }


    // =========================================================
    // GET ONE CONTACT MESSAGE
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getMessage(
            @PathVariable Long id) {

        ContactMessage message =
                contactMessageRepository
                        .findById(id)
                        .orElse(null);

        if (message == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Message not found.");
        }

        return ResponseEntity.ok(message);
    }


    // =========================================================
    // MARK ONE MESSAGE AS READ
    // =========================================================

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id) {

        ContactMessage message =
                contactMessageRepository
                        .findById(id)
                        .orElse(null);

        if (message == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Message not found.");
        }

        message.setStatus(
                MessageStatus.READ
        );

        ContactMessage updatedMessage =
                contactMessageRepository.save(message);

        return ResponseEntity.ok(updatedMessage);
    }


    // =========================================================
    // DELETE ONE CONTACT MESSAGE
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable Long id) {

        boolean exists =
                contactMessageRepository.existsById(id);

        if (!exists) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Message not found.");
        }

        contactMessageRepository.deleteById(id);

        return ResponseEntity.ok(
                "Message deleted successfully."
        );
    }
}




