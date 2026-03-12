package com.matrimony.repository;

import com.matrimony.entity.PartnerPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerPreferenceRepository extends JpaRepository<PartnerPreference, Long> {

    Optional<PartnerPreference> findByUserId(Long userId);
}
