package com.gruutnetworks.gruutuser.ui.dashboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.gruutnetworks.gruutuser.databinding.PresetItemBinding;
import com.gruutnetworks.gruutuser.gruut.Merger;

import java.util.List;

public class PresetListAdapter extends RecyclerView.Adapter<PresetListAdapter.PresetViewHolder> {

    private List<Merger> mergerList;
    private final PresetSelectedListener selectedListener;

    private int selectedPosition;
    private Merger selectedMerger;

    PresetListAdapter(List<Merger> mergerList, PresetSelectedListener selectedListener, Merger selectedMerger) {
        this.mergerList = mergerList;
        this.selectedListener = selectedListener;
        this.selectedMerger = selectedMerger;
    }

    @NonNull
    @Override
    public PresetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PresetItemBinding itemBinding = PresetItemBinding.inflate(layoutInflater, parent, false);
        return new PresetViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PresetViewHolder holder, int position) {
        Merger merger = mergerList.get(position);
        holder.bind(merger);

        if (merger.getUri().equals(selectedMerger.getUri()) && merger.getPort() == selectedMerger.getPort()) {
            selectedPosition = holder.getAdapterPosition();
        } else {
            selectedPosition = RecyclerView.NO_POSITION;
        }

        holder.itemView.setSelected(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            selectedListener.onPresetSelected(merger);
            selectedPosition = holder.getAdapterPosition();
            selectedMerger = merger;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        if (mergerList != null) {
            return mergerList.size();
        } else {
            return 0;
        }
    }

    static final class PresetViewHolder extends RecyclerView.ViewHolder {
        private final PresetItemBinding binding;

        PresetViewHolder(PresetItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Merger merger) {
            binding.setMerger(merger);
            binding.executePendingBindings();
        }
    }
}
