package com.empress.beauty.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empress.beauty.entity.Service;
import com.empress.beauty.repository.ServiceRepository;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "http://localhost:5173")
public class ServiceController {

    private final ServiceRepository serviceRepository;

    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    // Get all services
    @GetMapping
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    // Get one service by ID
    @GetMapping("/{id}")
    public Service getServiceById(@PathVariable Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    // Create a new service
    @PostMapping
    public Service createService(@RequestBody Service service) {
        return serviceRepository.save(service);
    }

    // Update a service
    @PutMapping("/{id}")
    public Service updateService(
            @PathVariable Long id,
            @RequestBody Service updatedService) {

        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setName(updatedService.getName());
        service.setDescription(updatedService.getDescription());
        service.setPrice(updatedService.getPrice());
        service.setDurationMinutes(updatedService.getDurationMinutes());
        service.setCategory(updatedService.getCategory());
        service.setAvailable(updatedService.getAvailable());

        return serviceRepository.save(service);
    }

    // Delete a service
    @DeleteMapping("/{id}")
    public String deleteService(@PathVariable Long id) {

        serviceRepository.deleteById(id);

        return "Service deleted successfully";
    }
}