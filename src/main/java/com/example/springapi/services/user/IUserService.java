package com.example.springapi.services.user;

import com.example.springapi.model.User;

import java.util.List;
import java.util.Optional;


public interface IUserService {
    Optional<List<User>> PagingList(int page, int limit);

}
