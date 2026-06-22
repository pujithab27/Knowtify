package com.knowtify.controller;

import com.knowtify.entity.Domain;
import com.knowtify.service.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/domains")
@RequiredArgsConstructor
public class DomainController {

    private final DomainService domainService;

    @GetMapping
    public ResponseEntity<List<Domain>> getAllDomains() {
        List<Domain> domains = domainService.getAllDomains();
        return ResponseEntity.ok(domains);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDomainById(@PathVariable Long id) {
        return domainService.getDomainById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getDomainByName(@PathVariable String name) {
        return domainService.getDomainByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createDomain(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String icon,
            @RequestParam String colorCode) {
        try {
            Domain domain = domainService.createDomain(name, description, icon, colorCode);
            return ResponseEntity.ok(domain);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDomain(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String icon,
            @RequestParam String colorCode) {
        try {
            Domain domain = domainService.updateDomain(id, name, description, icon, colorCode);
            return ResponseEntity.ok(domain);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDomain(@PathVariable Long id) {
        domainService.deleteDomain(id);
        return ResponseEntity.ok("Domain deleted successfully");
    }

    @GetMapping("/{id}/topic-count")
    public ResponseEntity<?> getTopicCount(@PathVariable Long id) {
        long count = domainService.getTopicCountForDomain(id);
        return ResponseEntity.ok(count);
    }
}
