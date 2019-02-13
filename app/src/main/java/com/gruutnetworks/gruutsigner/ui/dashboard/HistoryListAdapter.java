package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.gruutnetworks.gruutsigner.databinding.HistoryItemBinding;
import com.gruutnetworks.gruutsigner.model.SignedBlock;

import java.util.List;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder> {

    private List<SignedBlock> blockList;

    public HistoryListAdapter(List<SignedBlock> blockList) {
        this.blockList = blockList;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        HistoryItemBinding itemBinding = HistoryItemBinding.inflate(layoutInflater, parent, false);
        return new HistoryViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(blockList.get(position));
    }

    @Override
    public int getItemCount() {
        if (blockList != null) {
            return blockList.size();
        } else {
            return 0;
        }
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final HistoryItemBinding binding;

        HistoryViewHolder(HistoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SignedBlock block) {
            binding.setBlock(block);
            binding.executePendingBindings();
        }
    }
}