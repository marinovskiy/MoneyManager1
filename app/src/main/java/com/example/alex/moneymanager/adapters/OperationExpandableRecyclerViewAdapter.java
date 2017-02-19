package com.example.alex.moneymanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alex.moneymanager.R;
import com.example.alex.moneymanager.entities.CategoryUi;
import com.example.alex.moneymanager.entities.Operation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OperationExpandableRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CATEGORY = 0;
    private static final int VIEW_TYPE_OPERATION = 1;

    private OnItemClickListener onItemClickListener;

    //    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private DateFormat df = new SimpleDateFormat("MM.dd.yyyy");

    private List<Object> items = new ArrayList<>();
    private List<CategoryUi> categoryUis = new ArrayList<>();
    private List<List<Operation>> operations = new ArrayList<>();

    public OperationExpandableRecyclerViewAdapter(List<Object> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CATEGORY) {
            return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_category, parent, false));
        } else if (viewType == VIEW_TYPE_OPERATION) {
            return new OperationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_operation, parent, false));
        }
        throw new IllegalStateException("Incorrect view type");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            ((CategoryViewHolder) holder).bindCategory((CategoryUi) items.get(position));
        } else if (holder instanceof OperationViewHolder) {
            ((OperationViewHolder) holder).bindOperation((Operation) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof CategoryUi) {
            return VIEW_TYPE_CATEGORY;
        } else if (item instanceof Operation) {
            return VIEW_TYPE_OPERATION;
        }
        throw new IllegalStateException("Incorrect view type");
    }

    public void update(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String operationId);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_category_name)
        TextView tvCategoryName;
        @BindView(R.id.tv_sum)
        TextView tvCategorySum;

        @BindColor(R.color.red)
        int red;
        @BindColor(R.color.green)
        int green;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindCategory(CategoryUi categoryUi) {
            tvCategoryName.setText(categoryUi.getName());

            if (categoryUi.getType().equals("income")) {
                tvCategorySum.setTextColor(green);
                tvCategorySum.setText(String.format("+%s", categoryUi.getSum()));
            } else if (categoryUi.getType().equals("expense")) {
                tvCategorySum.setTextColor(red);
                tvCategorySum.setText(String.format("-%s", categoryUi.getSum()));
            }
        }
    }

    class OperationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_sum)
        TextView tvSum;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.tv_date)
        TextView tvDate;

        @BindColor(R.color.red)
        int red;
        @BindColor(R.color.green)
        int green;

        public OperationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindOperation(Operation operation) {
            if (operation.getType().equals("income")) {
                tvSum.setTextColor(green);
                tvSum.setText(String.format("+%s", operation.getSum()));
            } else if (operation.getType().equals("expense")) {
                tvSum.setTextColor(red);
                tvSum.setText(String.format("-%s", operation.getSum()));
            }

            tvDescription.setText(operation.getDescription());
            tvDate.setText(df.format(new Date(operation.getCreatedAt())));
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, ((Operation) items.get(getAdapterPosition())).getId());
            }
        }
    }
}