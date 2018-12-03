package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.databinding.DashboardFragmentBinding;

public class DashboardFragment extends Fragment implements SettingFragment.SettingDialogInterface {

    private DashboardViewModel viewModel;
    private DashboardFragmentBinding binding;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dashboard_fragment, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setModel(viewModel);

        getLifecycle().addObserver(viewModel);

        TextView tvLogMerger1 = binding.tvLogMerger1;
        tvLogMerger1.setMovementMethod(new ScrollingMovementMethod());

        viewModel.getLogMerger1().observe(this, text -> tvLogMerger1.append("\n" + text));
        viewModel.getRefreshMerger1().observe(this, o -> tvLogMerger1.setText(""));
        viewModel.getOpenSettingDialog().observe(this, o -> {
            SettingFragment settingFragment = SettingFragment.newInstance();
            settingFragment.setTargetFragment(this, 0);
            settingFragment.show(getFragmentManager(), "fragment_address_setting");
        });
    }

    @Override
    public void onOkBtnClicked(String ip, String port) {
        viewModel.ipMerger1.setValue(ip);
        viewModel.portMerger1.setValue(port);
        viewModel.onResume();
    }
}
