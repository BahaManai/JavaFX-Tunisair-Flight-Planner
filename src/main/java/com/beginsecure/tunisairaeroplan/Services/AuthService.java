package com.beginsecure.tunisairaeroplan.Services;

import com.beginsecure.tunisairaeroplan.dao.UserDao;
import com.beginsecure.tunisairaeroplan.Model.User;
import com.beginsecure.tunisairaeroplan.utils.PasswordUtils;

public class AuthService {
    private UserDao userDao;

    public AuthService() {
        this.userDao = new UserDao();
    }

    public User authenticate(String email, String password) {
        User user = userDao.getUserByEmail(email);
        if (user == null) {
            return null;
        }
        if (!PasswordUtils.verifyPassword(password, user.getSalt(), user.getEncryptedPassword())) {
            return null;
        }
        if (!user.isApproved()) {
            return null;
        }
        return user;
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
}