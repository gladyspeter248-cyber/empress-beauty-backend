package com.empress.beauty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.empress.beauty.entity.ContactMessage;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    List<ContactMessage> findAllByOrderByCreatedAtDesc();
}