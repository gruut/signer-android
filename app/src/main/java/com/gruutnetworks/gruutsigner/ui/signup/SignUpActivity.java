package com.gruutnetworks.gruutsigner.ui.signup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.gruut.MessageParser;

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

        String sample = "RxJQAAAAACgAAAAAAAAAAAAAAAAAAAAAAABBQkNERUZHSElKS0xNTg==";
        byte[] bytes = Base64.decode(sample, Base64.NO_WRAP);

        Log.d("Base64Test", MessageParser.byteArrToMsg(bytes).toString());
    }
}
