package com.beginsecure.tunisairaeroplan.Services;

import com.beginsecure.tunisairaeroplan.Model.User;
import com.beginsecure.tunisairaeroplan.dao.UserDao;
import com.beginsecure.tunisairaeroplan.utils.PasswordUtils;

public class RegistrationService {
    private UserDao userDao;

    public RegistrationService() {
        this.userDao = new UserDao();
    }

    public boolean registerUser(User user, String plainPassword) {
        if (!user.getEmail().endsWith("@tunisair.com")) {
            return false;
        }

        if (userDao.getUserByEmail(user.getEmail()) != null ||
                userDao.getUserByCin(user.getCin()) != null ||
                userDao.getUserByMatricule(user.getMatricule()) != null) {
            return false;
        }

        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(plainPassword, salt);

        user.setSalt(salt);
        user.setEncryptedPassword(hashedPassword);
        user.setApproved(false);
        user.setAdmin(false);

        return userDao.createUser(user);
    }
}