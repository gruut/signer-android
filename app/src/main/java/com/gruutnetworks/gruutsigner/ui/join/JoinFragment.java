package com.gruutnetworks.gruutsigner.ui.join;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
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
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.databinding.JoinFragmentBinding;
import com.gruutnetworks.gruutsigner.util.SnackbarMessage;
import com.gruutnetworks.gruutsigner.util.SnackbarUtil;

public class JoinFragment extends Fragment {

    private JoinViewModel viewModel;
    private JoinFragmentBinding binding;

    private EditText editPhone;

    public static JoinFragment newInstance() {
        return new JoinFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.join_fragment, container, false);

        editPhone = binding.editPhone;
        editPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        View rootView = binding.getRoot();
        rootView.setOnClickListener(v -> hideKeyboard());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(JoinViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setModel(viewModel);

        getLifecycle().addObserver(viewModel);

        viewModel.getSnackbarMessage().observe(this,
                (SnackbarMessage.SnackbarObserver) snackbarMessageResourceId
                        -> SnackbarUtil.showSnackbar(getView(), getString(snackbarMessageResourceId)));
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            imm.hideSoftInputFromInputMethod(getView().getWindowToken(), 0);
        }
    }
}
