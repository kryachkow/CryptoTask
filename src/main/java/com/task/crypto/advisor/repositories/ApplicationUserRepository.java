package com.task.crypto.advisor.repositories;

import com.task.crypto.advisor.entities.db.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findByUserName(String username);
}
