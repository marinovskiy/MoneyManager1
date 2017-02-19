package com.example.alex.moneymanager.utils;

import com.example.alex.moneymanager.entities.User;
import com.google.firebase.auth.FirebaseUser;

public class ModelConverter {

    public static User convertFirebaseUserToUser(FirebaseUser firebaseUser) {
        User user = new User();

        user.setId(firebaseUser.getUid());
        user.setName(firebaseUser.getDisplayName());
        user.setEmail(firebaseUser.getEmail());
        user.setPhotoUrl(firebaseUser.getPhotoUrl());

        return user;
    }
}