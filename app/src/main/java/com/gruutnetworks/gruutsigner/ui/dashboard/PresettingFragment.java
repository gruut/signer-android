package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.databinding.PresettingFragmentBinding;
import com.gruutnetworks.gruutsigner.gruut.Merger;

import static com.gruutnetworks.gruutsigner.gruut.MergerList.MERGER_LIST;

public class PresettingFragment extends DialogFragment implements PresetSelectedListener {

    private PresettingViewModel viewModel;
    private DashboardViewModel.MergerNum mergerNum;

    public static PresettingFragment newInstance(DashboardViewModel.MergerNum merger) {
        PresettingFragment fragment = new PresettingFragment();

        Bundle args = new Bundle();
        args.putString("MERGER", merger.name());
        fragment.setArguments(args);

        return fragment;
    }

    public PresettingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String arg = getArguments().getString("MERGER");
            this.mergerNum = Enum.valueOf(DashboardViewModel.MergerNum.class, arg);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        PresettingFragmentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.presetting_fragment, null, false);

        viewModel = ViewModelProviders.of(this).get(PresettingViewModel.class);
        viewModel.setMergerNum(mergerNum);
        viewModel.getMergerNum().observe(this, o -> {
            viewModel.fetchPreference();
        });

        binding.setModel(viewModel);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setFocusable(false);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        PresetListAdapter adapter = new PresetListAdapter(MERGER_LIST, this, viewModel.getMerger());

        binding.recyclerView.setAdapter(adapter);

        builder.setView(binding.getRoot())
                .setTitle("Merger Setting")
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    viewModel.pullPreference();

                    ((PresettingDialogInterface) getTargetFragment()).onOkBtnClicked(mergerNum);
                    dialog.dismiss();
                });

        return builder.create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPresetSelected(Merger merger) {
        viewModel.setMerger(merger);
    }

    public interface PresettingDialogInterface {
        void onOkBtnClicked(DashboardViewModel.MergerNum merger);
    }
}
