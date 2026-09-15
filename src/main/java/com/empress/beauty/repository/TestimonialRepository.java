package com.empress.beauty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empress.beauty.entity.Testimonial;

public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {

    List<Testimonial> findByStatusOrderByCreatedAtDesc(String status);

}