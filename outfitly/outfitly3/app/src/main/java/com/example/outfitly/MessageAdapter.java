package com.example.outfitly;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messageList;
    private final Context context;

    public MessageAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.isUser()) {
            holder.showUserMessage(message.getContent());
        } else {
            holder.showBotMessage(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userMessageTextView, botMessageTextView, shopLinkTextView;
        ImageView botImageView;
        FlexboxLayout quickRepliesLayout;
        View botMessageLayout;

        MessageViewHolder(View itemView) {
            super(itemView);
            userMessageTextView = itemView.findViewById(R.id.userMessageTextView);
            botMessageLayout = itemView.findViewById(R.id.botMessageLayout);
            botMessageTextView = itemView.findViewById(R.id.botMessageTextView);
            botImageView = itemView.findViewById(R.id.botImageView);
            shopLinkTextView = itemView.findViewById(R.id.shopLinkTextView);
            quickRepliesLayout = itemView.findViewById(R.id.quickRepliesLayout);
        }

        void showUserMessage(String content) {
            userMessageTextView.setVisibility(View.VISIBLE);
            botMessageLayout.setVisibility(View.GONE);
            userMessageTextView.setText(content);
        }

        void showBotMessage(Message message) {
            userMessageTextView.setVisibility(View.GONE);
            botMessageLayout.setVisibility(View.VISIBLE);

            String content = message.getContent();
            String imageUrl = extractUrl(content, "Lihat gambar: ");
            String shopLink = extractUrl(content, "Cek di Shopee: ");
            String cleanedText = cleanText(content);

            botMessageTextView.setText(cleanedText);

            if (!imageUrl.isEmpty()) {
                botImageView.setVisibility(View.VISIBLE);
                Glide.with(context).load(imageUrl).into(botImageView);
            } else {
                botImageView.setVisibility(View.GONE);
            }

            if (!shopLink.isEmpty()) {
                shopLinkTextView.setVisibility(View.VISIBLE);
                shopLinkTextView.setText("Lihat di Shopee");
                shopLinkTextView.setTextColor(Color.parseColor("#FF5722"));
                shopLinkTextView.setPaintFlags(shopLinkTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                shopLinkTextView.setOnClickListener(v -> openLink(shopLink));
            } else {
                shopLinkTextView.setVisibility(View.GONE);
            }

            setupQuickReplies(message.getQuickReplies());
        }

        private void setupQuickReplies(List<String> quickReplies) {
            quickRepliesLayout.removeAllViews();

            if (quickReplies != null && !quickReplies.isEmpty()) {
                quickRepliesLayout.setVisibility(View.VISIBLE);

                for (String reply : quickReplies) {
                    TextView chip = createQuickReplyChip(reply);
                    quickRepliesLayout.addView(chip);
                }
            } else {
                quickRepliesLayout.setVisibility(View.GONE);
            }
        }

        private TextView createQuickReplyChip(String text) {
            TextView chip = new TextView(context);
            chip.setText(text);
            chip.setPadding(30, 15, 30, 15);
            chip.setBackgroundResource(R.drawable.quick_reply_background);
            chip.setTextColor(Color.WHITE);
            chip.setTextSize(14);
            chip.setClickable(true);
            chip.setFocusable(true);
            chip.setOnClickListener(v -> {
                if (context instanceof AiActivity) {
                    ((AiActivity) context).sendQuickReply(text);
                }
            });
            return chip;
        }

        private String extractUrl(String text, String prefix) {
            int start = text.indexOf(prefix);
            if (start == -1) return "";
            start += prefix.length();
            int end = text.indexOf("\n", start);
            if (end == -1) end = text.length();
            return text.substring(start, end).trim();
        }

        private String cleanText(String text) {
            return text.replaceAll("Lihat gambar: .*", "")
                    .replaceAll("Cek di Shopee: .*", "")
                    .trim();
        }

        private void openLink(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }
}
