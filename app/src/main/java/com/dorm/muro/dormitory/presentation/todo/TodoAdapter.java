package com.dorm.muro.dormitory.presentation.todo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dorm.muro.dormitory.R;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.dorm.muro.dormitory.Constants.TODO_MAX_CHARACTERS;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    interface OnTodoClicked {
        void onClick(TodoItem item);

        void onLongClick(TodoItem item);
    }

    private List<TodoItem> items;
    private WeakReference<Context> context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());
    private OnTodoClicked listener;
    private int pinnedItems = 0;

    TodoAdapter(Context context) {
        this.items = new LinkedList<>();
        this.context = new WeakReference<>(context);
    }

    void setItems(List<TodoItem> items) {
        this.items = items;
    }

    void setListener(OnTodoClicked listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context.get()).inflate(R.layout.todo_item, viewGroup, false);
        return new ViewHolder(v);
    }

    void unpinItem(TodoItem item) {
        int fromPosition, toPosition;

        for (fromPosition = 0; fromPosition < items.size() &&
                !items.get(fromPosition).equals(item); fromPosition++) {
        }

        for (toPosition = pinnedItems; toPosition < items.size() &&
                items.get(toPosition).getDeadline().before(item.getDeadline()); toPosition++) {
        }


        items.add(toPosition, item);
        items.remove(fromPosition);

        notifyItemMoved(fromPosition, toPosition - 1);
        notifyItemChanged(toPosition - 1);
        pinnedItems--;
    }

    void pinItem(TodoItem item) {
        int fromPosition, toPosition;

        for (fromPosition = 0; fromPosition < items.size() &&
                !items.get(fromPosition).equals(item); fromPosition++) {
        }

        for (toPosition = 0; toPosition < items.size() &&
                items.get(toPosition).isPinned() &&
                items.get(toPosition).getDeadline().before(item.getDeadline()); toPosition++) {
        }

        items.add(toPosition, item);
        items.remove(fromPosition + 1);

        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(toPosition);
        pinnedItems++;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        TodoItem item = items.get(i);
        viewHolder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView title;
        private TextView commentary;
        private TextView deadline;
        private ImageView pin;
        private View view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_todo_check);
            title = itemView.findViewById(R.id.tv_todo_title);
            commentary = itemView.findViewById(R.id.tv_todo_commentary);
            deadline = itemView.findViewById(R.id.tv_todo_deadline);
            pin = itemView.findViewById(R.id.iv_todo_pin);
            this.view = itemView;
        }

        void bind(TodoItem item) {
            checkBox.setChecked(item.isChecked());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setChecked(isChecked);
            });
            title.setText(item.getTitle());
            if (item.getCommentary().length() > TODO_MAX_CHARACTERS) {
                commentary.setText(item.getCommentary().substring(0, TODO_MAX_CHARACTERS / 2) + "...");
            } else {
                commentary.setText(item.getCommentary());
            }
            deadline.setText(item.getDeadlineString(dateFormat));
            pin.setVisibility(item.isPinned() ? View.VISIBLE : View.INVISIBLE);
            view.setOnClickListener(v -> listener.onClick(item));
            view.setOnLongClickListener(v -> {
                listener.onLongClick(item);
                return true;
            });
        }
    }
}
