package com.example.electronics_store.adapter;

public class UserAdapter {
        //extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
//    private final Context context;
//    private List<User> userList;
//    private final OnUserClickListener clickListener;
//
//    public interface OnUserClickListener {
//        void onUserClick(int userId);
//    }
//
//    public UserAdapter(Context context, OnUserClickListener listener) {
//        this.context = context;
//        this.clickListener = listener;
//    }
//
//    public void setUserList(List<User> users) {
//        this.userList = users;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
//        return new UserViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
//        User user = userList.get(position);
//
//        holder.tvUserName.setText(user.getYourName());
//        holder.tvUserPhone.setText(user.getPhone());
////        holder.tvUserGender.setText(user.getGender());
//
//        holder.btnMore.setOnClickListener(v -> {
//        });
//
////        holder.btnEdit.setOnClickListener(v -> {
////            clickListener.onUserClick(user.getId());
////        });
////
////        holder.itemView.setOnClickListener(v -> {
////            clickListener.onUserClick(user.getId());
////        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return userList != null ? userList.size() : 0;
//    }
//
//    static class UserViewHolder extends RecyclerView.ViewHolder {
//        CircleImageView ivUserAvatar;
//        TextView tvUserName, tvUserPhone, tvUserGender;
//        ImageButton btnMore, btnEdit, btnDelete;
//
//        public UserViewHolder(@NonNull View itemView) {
//            super(itemView);
//            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
//            tvUserName = itemView.findViewById(R.id.tvUserName);
//            tvUserPhone = itemView.findViewById(R.id.tvUserPhone);
//            tvUserGender = itemView.findViewById(R.id.tvUserGender);
//            btnMore = itemView.findViewById(R.id.btnMore);
//            btnEdit = itemView.findViewById(R.id.btnEdit);
//            btnDelete = itemView.findViewById(R.id.btnDelete);
//        }
//    }
}