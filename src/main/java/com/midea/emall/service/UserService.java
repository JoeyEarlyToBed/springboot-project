package com.midea.emall.service;

import com.midea.emall.exception.MideaMallException;
import com.midea.emall.model.pojo.User;

import java.security.NoSuchAlgorithmException;

public interface UserService {
    User getUser();

    void register(String username, String password) throws MideaMallException, NoSuchAlgorithmException;

    User login(String username, String password) throws MideaMallException;

    void updateInformation(User user) throws MideaMallException;

    boolean checkAdminRole(User user);
}
