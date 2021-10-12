package com.example.homesitter;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class PushNotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token){

    }

}
