package com.empress.beauty.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.empress.beauty.entity.Testimonial;
import com.empress.beauty.repository.TestimonialRepository;

@RestController
@RequestMapping("/api/testimonials")
@CrossOrigin(origins = "http://localhost:5173")
public class TestimonialController {

    private final TestimonialRepository testimonialRepository;

    public TestimonialController(
            TestimonialRepository testimonialRepository
    ) {
        this.testimonialRepository = testimonialRepository;
    }


    // ==========================================
    // GET ALL TESTIMONIALS
    // ==========================================

    @GetMapping
    public List<Testimonial> getAllTestimonials() {

        return testimonialRepository.findAll();
    }


    // ==========================================
    // GET APPROVED TESTIMONIALS
    // ==========================================

    @GetMapping("/approved")
    public List<Testimonial> getApprovedTestimonials() {

        return testimonialRepository
                .findByStatusOrderByCreatedAtDesc("APPROVED");
    }


    // ==========================================
    // GET PENDING TESTIMONIALS
    // ==========================================

    @GetMapping("/pending")
    public List<Testimonial> getPendingTestimonials() {

        return testimonialRepository
                .findByStatusOrderByCreatedAtDesc("PENDING");
    }


    // ==========================================
    // GET SINGLE TESTIMONIAL
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Testimonial> getTestimonialById(
            @PathVariable Long id
    ) {

        Optional<Testimonial> testimonial =
                testimonialRepository.findById(id);

        if (testimonial.isPresent()) {
            return ResponseEntity.ok(testimonial.get());
        }

        return ResponseEntity.notFound().build();
    }


    // ==========================================
    // CREATE TESTIMONIAL
    // ==========================================

    @PostMapping
    public ResponseEntity<?> createTestimonial(
            @RequestBody Testimonial testimonial
    ) {

        // Basic validation

        if (testimonial.getCustomerName() == null ||
                testimonial.getCustomerName().trim().isEmpty()) {

            return ResponseEntity.badRequest().body(
                    Map.of("error", "Customer name is required.")
            );
        }


        if (testimonial.getComment() == null ||
                testimonial.getComment().trim().isEmpty()) {

            return ResponseEntity.badRequest().body(
                    Map.of("error", "Comment is required.")
            );
        }


        if (testimonial.getRating() == null ||
                testimonial.getRating() < 1 ||
                testimonial.getRating() > 5) {

            return ResponseEntity.badRequest().body(
                    Map.of("error", "Rating must be between 1 and 5.")
            );
        }


        // Every new testimonial starts as PENDING

        testimonial.setStatus("PENDING");


        Testimonial savedTestimonial =
                testimonialRepository.save(testimonial);


        return ResponseEntity.ok(savedTestimonial);
    }


    // ==========================================
    // UPDATE TESTIMONIAL
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTestimonial(
            @PathVariable Long id,
            @RequestBody Testimonial updatedTestimonial
    ) {

        Optional<Testimonial> existing =
                testimonialRepository.findById(id);

        if (existing.isEmpty()) {

            return ResponseEntity.notFound().build();
        }


        Testimonial testimonial = existing.get();


        if (updatedTestimonial.getCustomerName() != null) {

            testimonial.setCustomerName(
                    updatedTestimonial.getCustomerName()
            );
        }


        if (updatedTestimonial.getRating() != null) {

            if (updatedTestimonial.getRating() < 1 ||
                    updatedTestimonial.getRating() > 5) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "error",
                                "Rating must be between 1 and 5."
                        )
                );
            }

            testimonial.setRating(
                    updatedTestimonial.getRating()
            );
        }


        if (updatedTestimonial.getComment() != null) {

            testimonial.setComment(
                    updatedTestimonial.getComment()
            );
        }


        if (updatedTestimonial.getServiceName() != null) {

            testimonial.setServiceName(
                    updatedTestimonial.getServiceName()
            );
        }


        if (updatedTestimonial.getStatus() != null) {

            String status =
                    updatedTestimonial.getStatus().toUpperCase();


            if (!status.equals("PENDING") &&
                    !status.equals("APPROVED") &&
                    !status.equals("REJECTED")) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "error",
                                "Invalid testimonial status."
                        )
                );
            }


            testimonial.setStatus(status);
        }


        Testimonial saved =
                testimonialRepository.save(testimonial);


        return ResponseEntity.ok(saved);
    }


    // ==========================================
    // APPROVE TESTIMONIAL
    // ==========================================

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveTestimonial(
            @PathVariable Long id
    ) {

        Optional<Testimonial> existing =
                testimonialRepository.findById(id);

        if (existing.isEmpty()) {

            return ResponseEntity.notFound().build();
        }


        Testimonial testimonial = existing.get();

        testimonial.setStatus("APPROVED");


        Testimonial saved =
                testimonialRepository.save(testimonial);


        return ResponseEntity.ok(saved);
    }


    // ==========================================
    // REJECT TESTIMONIAL
    // ==========================================

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectTestimonial(
            @PathVariable Long id
    ) {

        Optional<Testimonial> existing =
                testimonialRepository.findById(id);

        if (existing.isEmpty()) {

            return ResponseEntity.notFound().build();
        }


        Testimonial testimonial = existing.get();

        testimonial.setStatus("REJECTED");


        Testimonial saved =
                testimonialRepository.save(testimonial);


        return ResponseEntity.ok(saved);
    }


    // ==========================================
    // DELETE TESTIMONIAL
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTestimonial(
            @PathVariable Long id
    ) {

        if (!testimonialRepository.existsById(id)) {

            return ResponseEntity.notFound().build();
        }


        testimonialRepository.deleteById(id);


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Testimonial deleted successfully."
                )
        );
    }
}