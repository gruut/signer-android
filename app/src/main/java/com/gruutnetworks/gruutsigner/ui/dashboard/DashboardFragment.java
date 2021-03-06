package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
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

import static com.gruutnetworks.gruutsigner.gruut.GruutConfigs.AUTO_REFRESH_TIMEOUT;

public class DashboardFragment extends Fragment implements PresettingFragment.PresettingDialogInterface {

    private DashboardViewModel viewModel;
    private DashboardFragmentBinding binding;

    private Handler waitForAutoRefresh = new Handler();

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
        viewModel.getRefreshTriggerMerger1().observe(this, o -> tvLogMerger1.setText(""));
        viewModel.getOpenSetting1Dialog().observe(this, o -> {
            PresettingFragment settingFragment = PresettingFragment.newInstance(DashboardViewModel.MergerNum.MERGER_1);
            settingFragment.setTargetFragment(this, 0);
            settingFragment.show(getFragmentManager(), "fragment_address_setting");
            waitForAutoRefresh.removeCallbacksAndMessages(null);
        });
        viewModel.getErrorMerger1().observe(this, err -> {
            if (err) {
                waitForAutoRefresh.postDelayed(() -> viewModel.refreshMerger1(), AUTO_REFRESH_TIMEOUT);
            }
        });

        TextView tvLogMerger2 = binding.tvLogMerger2;
        tvLogMerger2.setMovementMethod(new ScrollingMovementMethod());

        viewModel.getLogMerger2().observe(this, text -> tvLogMerger2.append("\n" + text));
        viewModel.getRefreshTriggerMerger2().observe(this, o -> tvLogMerger2.setText(""));
        viewModel.getOpenSetting2Dialog().observe(this, o -> {
            PresettingFragment settingFragment = PresettingFragment.newInstance(DashboardViewModel.MergerNum.MERGER_2);
            settingFragment.setTargetFragment(this, 0);
            settingFragment.show(getFragmentManager(), "fragment_address_setting");
            waitForAutoRefresh.removeCallbacksAndMessages(null);
        });
        viewModel.getErrorMerger2().observe(this, err -> {
            if (err) {
                waitForAutoRefresh.postDelayed(() -> viewModel.refreshMerger2(), AUTO_REFRESH_TIMEOUT);
            }
        });

        viewModel.getOpenHistoryDialog().observe(this, o -> {
            HistoryFragment historyFragment = HistoryFragment.newInstance();
            historyFragment.setTargetFragment(this, 0);
            historyFragment.show(getFragmentManager(), "fragment_block_history");
        });
    }

    @Override
    public void onPause() {
        waitForAutoRefresh.removeCallbacksAndMessages(null);
        viewModel.onCleared();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onOkBtnClicked(DashboardViewModel.MergerNum merger) {
        switch (merger) {
            case MERGER_1:
                viewModel.refreshMerger1();
                break;
            case MERGER_2:
                viewModel.refreshMerger2();
                break;
        }
    }
}
