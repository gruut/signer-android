package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.databinding.SettingFragmentBinding;

public class SettingFragment extends DialogFragment {

    public static final String MERGER_1 = "MERGER_1";
    public static final String MERGER_2 = "MERGER_2";

    private SettingViewModel viewModel;
    private SettingFragmentBinding binding;
    private String merger;

    public static SettingFragment newInstance(String merger) {
        SettingFragment fragment = new SettingFragment();

        Bundle args = new Bundle();
        args.putString("MERGER", merger);
        fragment.setArguments(args);

        return fragment;
    }

    public SettingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            merger = getArguments().getString("MERGER");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.setting_fragment, null, false);

        viewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        viewModel.setMerger(merger);
        viewModel.getMerger().observe(this, o -> {
            viewModel.fetchPreference();
        });

        binding.setModel(viewModel);

        builder.setView(binding.getRoot())
                .setTitle("Merger Address Setting")
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    ((SettingDialogInterface) getTargetFragment()).onOkBtnClicked(
                            binding.inputIp.getText().toString(),
                            binding.inputPort.getText().toString());

                    viewModel.pullPreference();
                    dialog.dismiss();
                });

        // IP 주소를 위한 Filter
        binding.inputIp.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            if (end > start) {
                String destTxt = dest.toString();
                String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                if (!resultingTxt.matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                    return "";
                } else {
                    String[] splits = resultingTxt.split("\\.");
                    for (String split : splits) {
                        if (Integer.valueOf(split) > 255) {
                            return "";
                        }
                    }
                }
            }
            return null;
        }
        });

        return builder.create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public interface SettingDialogInterface {
        void onOkBtnClicked(String ip, String port);
    }
}
