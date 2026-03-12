package com.matrimony.repository;

import com.matrimony.entity.Interest;
import com.matrimony.entity.Interest.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {

    List<Interest> findByToUserIdAndStatus(Long toUserId, Status status);

    List<Interest> findByFromUserId(Long fromUserId);

    List<Interest> findByToUserId(Long toUserId);

    Optional<Interest> findByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

    boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

    long countByToUserIdAndStatus(Long toUserId, Status status);
}
