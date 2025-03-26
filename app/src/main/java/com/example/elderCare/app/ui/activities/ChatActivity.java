package com.example.elderCare.app.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.elderCare.app.R;
import com.example.elderCare.app.ReopenChatActivity;
import com.example.elderCare.app.ui.fragments.ChatFragment;
import com.example.elderCare.app.utils.Constants;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "Onyx/ChatActivity";

    private Toolbar mToolbar;

    public static void startActivity(Context context,
                                     String receiver,
                                     String receiverUid) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bindViews();
        init();
    }

    private void bindViews() {
        mToolbar = findViewById(R.id.toolbar);
    }

    private void init() {
        // set the toolbar
        setSupportActionBar(mToolbar);

        // set toolbar title
        mToolbar.setTitle((getIntent().getExtras()).getString(Constants.ARG_RECEIVER));

        // set the register screen fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_chat,
                ChatFragment.newInstance(getIntent().getExtras().getString(Constants.ARG_RECEIVER),
                        getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID)),
                ChatFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ReopenChatActivity.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ReopenChatActivity.setChatActivityOpen(false);
    }
}
