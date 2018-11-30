package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.databinding.SettingFragmentBinding;

public class SettingFragment extends DialogFragment {

    private SettingViewModel viewModel;
    private SettingFragmentBinding binding;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    public SettingFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.setting_fragment, null, false);

        viewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        binding.setModel(viewModel);

        builder.setView(inflater.inflate(R.layout.setting_fragment, null))
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    dialog.dismiss();
                });

        return builder.create();
    }
}
