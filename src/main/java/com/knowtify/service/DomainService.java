package com.knowtify.service;

import com.knowtify.entity.Domain;
import com.knowtify.repository.DomainRepository;
import com.knowtify.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DomainService {

    private final DomainRepository domainRepository;
    private final TopicRepository topicRepository;

    public Domain createDomain(String name, String description, String icon, String colorCode) {
        if (domainRepository.existsByName(name)) {
            throw new IllegalArgumentException("Domain already exists: " + name);
        }
        Domain domain = new Domain(name, description, icon, colorCode);
        return domainRepository.save(domain);
    }

    public List<Domain> getAllDomains() {
        return domainRepository.findAll();
    }

    public Optional<Domain> getDomainById(Long id) {
        return domainRepository.findById(id);
    }

    public Optional<Domain> getDomainByName(String name) {
        return domainRepository.findByName(name);
    }

    public Domain updateDomain(Long id, String name, String description, String icon, String colorCode) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Domain not found: " + id));

        if (!domain.getName().equals(name) && domainRepository.existsByName(name)) {
            throw new IllegalArgumentException("Domain name already exists: " + name);
        }

        domain.setName(name);
        domain.setDescription(description);
        domain.setIcon(icon);
        domain.setColorCode(colorCode);
        return domainRepository.save(domain);
    }

    public void deleteDomain(Long id) {
        domainRepository.deleteById(id);
    }

    public long getTopicCountForDomain(Long domainId) {
        return getDomainById(domainId)
                .map(topicRepository::countByDomain)
                .orElse(0L);
    }
}
