package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.gruutnetworks.gruutsigner.databinding.PresetItemBinding;
import com.gruutnetworks.gruutsigner.gruut.Merger;

import java.util.List;

public class PresetListAdapter extends RecyclerView.Adapter<PresetListAdapter.PresetViewHolder> {

    private List<Merger> mergerList;
    private final PresetSelectedListner selectedListner;

    public PresetListAdapter(List<Merger> mergerList, PresetSelectedListner selectedListner) {
        this.mergerList = mergerList;
        this.selectedListner = selectedListner;
    }

    @NonNull
    @Override
    public PresetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PresetItemBinding itemBinding = PresetItemBinding.inflate(layoutInflater, parent, false);
        return new PresetViewHolder(itemBinding, selectedListner);
    }

    @Override
    public void onBindViewHolder(@NonNull PresetViewHolder holder, int position) {
        Merger merger = mergerList.get(position);
        holder.bind(merger);
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

        PresetViewHolder(PresetItemBinding binding, PresetSelectedListner selectedListner) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                selectedListner.onPresetSelected(binding.getMerger());
            });
        }

        void bind(Merger merger) {
            binding.setMerger(merger);
            binding.executePendingBindings();
        }
    }
}
