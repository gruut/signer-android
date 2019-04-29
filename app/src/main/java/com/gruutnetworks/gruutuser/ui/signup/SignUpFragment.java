package com.gruutnetworks.gruutuser.ui.signup;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.gruutnetworks.gruutuser.R;
import com.gruutnetworks.gruutuser.databinding.SignUpFragmentBinding;
import com.gruutnetworks.gruutuser.ui.dashboard.DashboardActivity;
import com.gruutnetworks.gruutuser.util.PreferenceUtil;
import com.gruutnetworks.gruutuser.util.SnackbarMessage;
import com.gruutnetworks.gruutuser.util.SnackbarUtil;

public class SignUpFragment extends Fragment {

    private SignUpViewModel viewModel;
    private SignUpFragmentBinding binding;
    private PreferenceUtil preferenceUtil;

    private EditText editPhone;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.sign_up_fragment, container, false);
        preferenceUtil = PreferenceUtil.getInstance(getContext());

        editPhone = binding.editPhone;
        editPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        View rootView = binding.getRoot();
        rootView.setOnClickListener(v -> hideKeyboard());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setModel(viewModel);

        getLifecycle().addObserver(viewModel);

        viewModel.getSnackbarMessage().observe(this,
                (SnackbarMessage.SnackbarObserver) snackbarMessageResourceId -> SnackbarUtil.showSnackbar(SignUpFragment.this.getView(), getString(snackbarMessageResourceId)));

        viewModel.getNavigateToDashboard().observe(this, o -> {
            hideKeyboard();

            if (preferenceUtil.getBoolean(PreferenceUtil.Key.INIT_EXEC_BOOL, true)) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setTitle(R.string.join_guide_title)
                        .setMessage(R.string.join_guide_msg)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            startActivity(new Intent(getActivity(), DashboardActivity.class));
                            preferenceUtil.put(PreferenceUtil.Key.INIT_EXEC_BOOL, false);
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            } else {
                startActivity(new Intent(getActivity(), DashboardActivity.class));
            }
        });

        viewModel.getOpenWebBrowser().observe(this, o -> {
            String url = getString(R.string.gruut_enterprise_web_url);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            imm.hideSoftInputFromInputMethod(getView().getWindowToken(), 0);
        }
    }
}
