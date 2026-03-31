package com.example.messenger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.messenger.R;
import com.example.messenger.models.Message;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messages;
    private String currentUser;

    private static final int TYPE_OWN = 0;
    private static final int TYPE_OTHER = 1;

    public MessagesAdapter(List<Message> messages, String currentUser) {
        this.messages = messages;
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getFromUser().equals(currentUser)) return TYPE_OWN;
        else return TYPE_OTHER;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_OWN) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_own, parent, false);
            return new OwnMessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_other, parent, false);
            return new OtherMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messages.get(position);
        if (holder instanceof OwnMessageHolder) {
            ((OwnMessageHolder) holder).tvMessage.setText(msg.getMessage());
        } else {
            ((OtherMessageHolder) holder).tvMessage.setText(msg.getMessage());
        }
    }

    @Override public int getItemCount() { return messages.size(); }

    static class OwnMessageHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        public OwnMessageHolder(@NonNull View itemView) { super(itemView); tvMessage = itemView.findViewById(R.id.tvMessage); }
    }

    static class OtherMessageHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        public OtherMessageHolder(@NonNull View itemView) { super(itemView); tvMessage = itemView.findViewById(R.id.tvMessage); }
    }
}
