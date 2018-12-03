package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.databinding.SettingFragmentBinding;

public class SettingFragment extends DialogFragment implements View.OnClickListener {

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

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.setting_fragment, null, false);

        viewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        binding.setModel(viewModel);

        builder.setView(binding.getRoot())
                .setTitle("Merger Address Setting")
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    ((SettingDialogInterface) getTargetFragment()).onOkBtnClicked(
                            binding.inputIp.getText().toString(),
                            binding.inputPort.getText().toString());

                    dialog.dismiss();
                });

        return builder.create();
    }

    @Override
    public void onClick(View v) {

    }

    public interface SettingDialogInterface {
        void onOkBtnClicked(String ip, String port);
    }
}
