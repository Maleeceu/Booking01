package com.ticketing.booking.repository;

import com.ticketing.booking.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

}
