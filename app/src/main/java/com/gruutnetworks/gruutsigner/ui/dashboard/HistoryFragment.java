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
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.databinding.HistoryFragmentBinding;

public class HistoryFragment extends DialogFragment {

    private HistoryViewModel viewModel;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();

        return fragment;
    }

    public HistoryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        HistoryFragmentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.history_fragment, null, false);

        viewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setFocusable(false);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        HistoryListAdapter adapter = new HistoryListAdapter(viewModel.getAllBlocks().getValue());
        binding.recyclerView.setAdapter(adapter);

        builder.setView(binding.getRoot())
                .setTitle("Your signature history")
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}
