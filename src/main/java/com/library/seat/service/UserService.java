package com.library.seat.service;

import com.library.seat.common.BusinessException;
import com.library.seat.common.ErrorCode;
import com.library.seat.common.JwtUtil;
import com.library.seat.dto.LoginRequest;
import com.library.seat.dto.LoginResponse;
import com.library.seat.entity.User;
import com.library.seat.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public UserService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setToken(token);
        return response;
    }
}
