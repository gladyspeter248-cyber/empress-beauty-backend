package com.empress.beauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empress.beauty.entity.Service;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}