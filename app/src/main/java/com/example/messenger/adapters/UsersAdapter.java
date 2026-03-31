package com.example.messenger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.messenger.R;
import com.example.messenger.network.ApiService;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<ApiService.UserSearchResult> users;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(ApiService.UserSearchResult user);
    }

    public UsersAdapter(List<ApiService.UserSearchResult> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApiService.UserSearchResult user = users.get(position);
        String displayName = user.displayName != null ? user.displayName : user.username;
        holder.tvDisplayName.setText(displayName);
        holder.tvUsername.setText("@" + user.username);
        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    @Override
    public int getItemCount() { return users.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDisplayName, tvUsername;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }
    }
}
