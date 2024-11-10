package com.example.springapi.services.user;

import com.example.springapi.model.User;
import com.example.springapi.repository.user.IUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    final IUserRepository _userRepository;
    final ModelMapper _modelMapper;

    public UserService(IUserRepository userRepository, ModelMapper modelMapper) {
        this._userRepository = userRepository;
        _modelMapper = modelMapper;
    }

    @Override
    public Optional<List<User>> PagingList(int page, int limit) {
        Page<User> userPage = _userRepository.findAll(PageRequest.of(page, limit));
        return Optional.of(userPage.getContent());
    }

}
