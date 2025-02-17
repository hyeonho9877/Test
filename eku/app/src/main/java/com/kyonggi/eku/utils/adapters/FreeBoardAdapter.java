package com.kyonggi.eku.utils.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kyonggi.eku.DetailFreeCommunity;
import com.kyonggi.eku.databinding.BoardLoadingBinding;
import com.kyonggi.eku.databinding.FreeBoardItemBinding;
import com.kyonggi.eku.model.FreeBoardPreview;

import java.util.List;

public class FreeBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private static final String TAG = "FreeBoardAdapter";
    private List<FreeBoardPreview> list;
    private final Context context;

    public FreeBoardAdapter(List<FreeBoardPreview> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public boolean insertFromHead(List<FreeBoardPreview> newList) {
        if (newList.size() != 0 && !(newList.get(0) == list.get(0))) {
            list.addAll(0, newList);
            return true;
        }
        return false;
    }

    public boolean insertFromTail(List<FreeBoardPreview> oldList) {
        if (oldList.size() < 20) {
            list.addAll(oldList);
            return true;
        } else {
            list.addAll(oldList);
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            FreeBoardItemBinding binding = FreeBoardItemBinding.inflate(layoutInflater, parent, false);
            return new FreeBoardAdapter.FreeBoardViewHolder(binding);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            BoardLoadingBinding binding = BoardLoadingBinding.inflate(layoutInflater, parent, false);
            return new FreeBoardAdapter.LoadingViewHolder(binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FreeBoardAdapter.FreeBoardViewHolder && list.get(position) != null) {
            ((FreeBoardAdapter.FreeBoardViewHolder) holder).bind(list.get(position));
        } else if (holder instanceof FreeBoardAdapter.LoadingViewHolder) {
            showLoadingView((FreeBoardAdapter.LoadingViewHolder) holder, position);
        }
    }

    private void showLoadingView(FreeBoardAdapter.LoadingViewHolder holder, int position) {

    }

    public List<FreeBoardPreview> getCurrentList() {
        return list;
    }

    class FreeBoardViewHolder extends RecyclerView.ViewHolder {
        private final FreeBoardItemBinding binding;
        private int writerNo;
        private long id;


        public FreeBoardViewHolder(FreeBoardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FreeBoardPreview item) {
            this.writerNo = item.getNo();
            this.id = item.getId();
            // 각각의 UI 에 알람 정보를 업데이트한다
            binding.textFreeBoardWriter.setText(item.getWriter());
            binding.textFreeBoardTitle.setText(item.getTitle());
            binding.textFreeBoardTime.setText(item.getTime());
            String view = "조회 " + item.getView();
            binding.textFreeBoardView.setText(view);
            binding.textFreeBoardComment.setText(String.valueOf(item.getComments()));
            binding.constraintLayoutFreeBoard.setOnClickListener(v->{
                Intent intent = new Intent(context, DetailFreeCommunity.class);
                intent.putExtra("id", String.valueOf(id));
                context.startActivity(intent);
            });
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        private BoardLoadingBinding binding;

        public LoadingViewHolder(@NonNull BoardLoadingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
