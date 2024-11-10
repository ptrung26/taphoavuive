package com.example.springapi.api.user;

import com.example.springapi.model.User;
import com.example.springapi.services.user.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/users")
@Tag(name = "User")
public class UserController {

    private final UserService _userServices;

    @Autowired
    public UserController(UserService userServices) {
        _userServices = userServices;
    }

    @GetMapping("/")
    public Optional<List<User>> pagingList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit) {
        return _userServices.PagingList(page, limit);
    }

}
