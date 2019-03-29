package com.android.reseller.core.users.getall;

import com.android.reseller.models.ChatUser;

import java.util.List;



public interface GetUsersContract {
    interface View {
        void onGetAllUsersSuccess(List<ChatUser> users);

        void onGetAllUsersFailure(String message);

        void onGetChatUsersSuccess(List<ChatUser> users);

        void onGetChatUsersFailure(String message);
    }

    interface Presenter {
        void getAllUsers();

        void getChatUsers();
    }

    interface Interactor {
        void getAllUsersFromFirebase();

        void getChatUsersFromFirebase();
    }

    interface OnGetAllUsersListener {
        void onGetAllUsersSuccess(List<ChatUser> users);

        void onGetAllUsersFailure(String message);
    }

    interface OnGetChatUsersListener {
        void onGetChatUsersSuccess(List<ChatUser> users);

        void onGetChatUsersFailure(String message);
    }
}
