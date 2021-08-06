package com.midea.emall.service.impl;

import com.midea.emall.exception.MideaMallException;
import com.midea.emall.exception.MideaMallExceptionEnum;
import com.midea.emall.model.dao.UserMapper;
import com.midea.emall.model.pojo.User;
import com.midea.emall.service.UserService;
import com.midea.emall.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User getUser() {
        User user = userMapper.selectByPrimaryKey(1);
        return user;
    }

    @Override
    public void register(String username, String password) throws MideaMallException, NoSuchAlgorithmException {

        //查询用户名是否存在，不允许重名
        User result = userMapper.selectByName(username);
        if (result != null) {
            throw new MideaMallException(MideaMallExceptionEnum.NAME_EXIT);
        }
        //通过校验，插入数据库
        User user = new User();
        user.setUsername(username);
        user.setPassword(MD5Utils.getMD5Str(password));

        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new MideaMallException(MideaMallExceptionEnum.INSERT_FAIL);
        }
    }

    @Override
    public User login(String username, String password) throws MideaMallException {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            throw new MideaMallException(MideaMallExceptionEnum.SYSTEM_ERROR);
        }

        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            throw new MideaMallException(MideaMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    @Override
    public void updateInformation(User user) throws MideaMallException {

        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 1) {
            throw new MideaMallException(MideaMallExceptionEnum.UPDATE_FAIL);
        }

    }

    @Override
    public boolean checkAdminRole(User user) {
        return user.getRole().equals(2);
    }


}
