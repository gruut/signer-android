package com.gruutnetworks.gruutsigner.ui.join;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.gruutnetworks.gruutsigner.R;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, JoinFragment.newInstance())
                    .commitNow();
        }
    }
}
