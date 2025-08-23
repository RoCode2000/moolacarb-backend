package com.uow.moolacarb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.uow.moolacarb.model.MealLog;
import java.util.List;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Integer> {

    @Query(value = """
        SELECT m.*
        FROM meallog m
        JOIN `user` u ON u.userId = m.userId
        WHERE u.firebaseId = :firebaseId
        ORDER BY m.timeConsumed DESC
    """, nativeQuery = true)
    List<MealLog> findByFirebaseId(String firebaseId);

}
