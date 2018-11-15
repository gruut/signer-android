package com.gruutnetworks.gruutsigner.ui.signup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.gruutnetworks.gruutsigner.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SignUpFragment.newInstance())
                    .commitNow();
        }
    }
}
