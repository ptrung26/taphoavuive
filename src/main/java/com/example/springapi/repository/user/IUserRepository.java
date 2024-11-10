package com.example.springapi.repository.user;

import com.example.springapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {

}
