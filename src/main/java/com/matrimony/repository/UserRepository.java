package com.matrimony.repository;

import com.matrimony.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    List<User> findByBannedFalse();

    long countByPremiumTrue();

    long countByBannedFalse();

    @Query("SELECT u FROM User u WHERE u.banned = false AND u.id != :userId " +
           "ORDER BY u.premium DESC, u.lastActive DESC")
    List<User> findPotentialMatches(@Param("userId") Long userId);

    @Query("SELECT u FROM User u WHERE u.banned = false AND u.gender = :gender " +
           "AND u.id != :userId ORDER BY u.premium DESC, u.lastActive DESC")
    List<User> findMatchesByGender(@Param("userId") Long userId, @Param("gender") String gender);
}
