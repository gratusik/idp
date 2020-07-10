package com.gratus.idp.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gratus.idp.R;
import com.gratus.idp.databinding.ItemReportListBinding;
import com.gratus.idp.model.common.CyclePathNew;
import com.gratus.idp.model.common.Properties;
import com.gratus.idp.view.base.BaseViewHolder;
import com.gratus.idp.view.interfaces.adapter.ReportListListener;
import com.gratus.idp.view.interfaces.viewModel.ReportListItemViewModelListener;
import com.gratus.idp.viewModel.adapter.RepostItemListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReportListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    public static final int VIEW_TYPE_NORMAL = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    private ReportListListener mListener;
    private List<CyclePathNew> cyclePathNews;
    private List<CyclePathNew> cyclePathNewsFilter;

    public ReportListAdapter(ArrayList<CyclePathNew> cyclePathNews) {
        this.cyclePathNews = cyclePathNews;
        this.cyclePathNewsFilter = cyclePathNews;
    }

    public void setmListener(ReportListListener mListener) {
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                 ItemReportListBinding itemReportListBinding = ItemReportListBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new ReportListViewHolder(itemReportListBinding);
            case VIEW_TYPE_EMPTY:
            default:
                ItemReportListBinding itemEmptyReportListBinding = ItemReportListBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new EmptyViewHolder(itemEmptyReportListBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
       // if (cyclePathNewsFilter != null && cyclePathNewsFilter.size() > 0) {
            return cyclePathNewsFilter.size();
      /*  } else {
            return 1;
        }*/
    }

    @Override
    public int getItemViewType(int position) {
        //if(cyclePathNewsFilter.size()>0) {
            return cyclePathNewsFilter.get(position).getLayoutId();
       // }
    }

    public void clearItems() {
        cyclePathNewsFilter.clear();
        cyclePathNews.clear();
    }

    public void addItems(List<CyclePathNew> cyclePathNew) {
        cyclePathNews.clear();
        cyclePathNews.addAll(cyclePathNew);
        cyclePathNewsFilter.clear();
        cyclePathNewsFilter.addAll(cyclePathNew);
        notifyDataSetChanged();
    }

    private class ReportListViewHolder extends BaseViewHolder implements ReportListItemViewModelListener {
        private ItemReportListBinding mBinding;
        private RepostItemListViewModel repostItemListViewModel;

        public ReportListViewHolder(ItemReportListBinding itemReportListBinding) {
            super(itemReportListBinding.getRoot());
            this.mBinding = itemReportListBinding;
        }

        @Override
        public void onBind(int position) {
            final CyclePathNew cyclePathNew = cyclePathNewsFilter.get(position);
            repostItemListViewModel = new RepostItemListViewModel(cyclePathNew, this);
            mBinding.setRepostItemListViewModel(repostItemListViewModel);
        }
        @Override
        public void onItemClick() {
            mListener.onItemClick(cyclePathNewsFilter.get(getAdapterPosition()));
        }
    }

    private class EmptyViewHolder extends BaseViewHolder implements ReportListItemViewModelListener{
        private ItemReportListBinding mBinding;
        private RepostItemListViewModel repostItemListViewModel;
        public EmptyViewHolder(ItemReportListBinding itemEmptyReportListBinding) {
            super(itemEmptyReportListBinding.getRoot());
            this.mBinding = itemEmptyReportListBinding;
        }
        @Override
        public void onBind(int position) {
            final CyclePathNew cyclePathNew = cyclePathNewsFilter.get(position);
            repostItemListViewModel = new RepostItemListViewModel(cyclePathNew, this);
            mBinding.setRepostItemListViewModel(repostItemListViewModel);
        }

        @Override
        public void onItemClick() {
            mListener.onItemClick(cyclePathNewsFilter.get(getAdapterPosition()));
        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    cyclePathNewsFilter = cyclePathNews;
                } else {
                    List<CyclePathNew> filteredList = new ArrayList<>();
                    for (CyclePathNew row : cyclePathNews) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getProperties().getStreetName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    if(filteredList.size()>0){
                        cyclePathNewsFilter = filteredList;
                    }
                    else{
                        CyclePathNew cyclePathNewCheck = new CyclePathNew();
                        Properties properties = new Properties();
                        properties.setStreetName("No data found");
                        cyclePathNewCheck.setProperties(properties);
                        cyclePathNewCheck.setCount(0);
                        filteredList.add(cyclePathNewCheck);
                        cyclePathNewsFilter = filteredList;
                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = cyclePathNewsFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    cyclePathNewsFilter = (ArrayList<CyclePathNew>) filterResults.values;

                    // refresh the list with filtered data
                    notifyDataSetChanged();
            }
        };
    }
}
