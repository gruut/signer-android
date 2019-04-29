package com.gruutnetworks.gruutuser.ui.dashboard;

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
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import com.gruutnetworks.gruutuser.R;
import com.gruutnetworks.gruutuser.databinding.HistoryFragmentBinding;

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
        binding.setModel(viewModel);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setFocusable(false);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        viewModel.getAllBlocks().observe(this, signedBlocks -> {
            if (signedBlocks == null || signedBlocks.isEmpty()) {
                viewModel.setEmptyVisible(true);
            } else {
                viewModel.setEmptyVisible(false);
            }
            HistoryListAdapter adapter = new HistoryListAdapter(signedBlocks);
            binding.recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        });

        builder.setView(binding.getRoot())
                .setTitle(R.string.signature_title)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}
