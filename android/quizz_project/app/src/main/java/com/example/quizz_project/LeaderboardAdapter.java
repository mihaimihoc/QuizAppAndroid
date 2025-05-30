package com.example.quizz_project;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private List<LeaderboardActivity.LeaderboardEntry> entries;
    private int currentUserId;

    public LeaderboardAdapter(List<LeaderboardActivity.LeaderboardEntry> entries, int currentUserId) {
        this.entries = entries;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardActivity.LeaderboardEntry entry = entries.get(position);
        holder.rankText.setText(String.valueOf(entry.getRank()));
        holder.nameText.setText(entry.getUserName());
        holder.scoreText.setText(String.format("%.1f", entry.getTotalScore()));

        String category = entry.getCategory() != null ? entry.getCategory() : "Unknown";
        String cleanCategory = category.replaceAll(",\\s*$", "");
        holder.categoryText.setText(cleanCategory);

        // Apply background color
        if (entry.getColorCode() != null && !entry.getColorCode().equals("default")) {
            try {
                GradientDrawable drawable = GradientParser.parseGradient(entry.getColorCode(), "rectangle");
                holder.itemView.setBackground(drawable);

                // Set text color based on background brightness
                int textColor = isColorDark(entry.getColorCode()) ?
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.leaderboard_text_light) :
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.leaderboard_text_dark);

                holder.rankText.setTextColor(textColor);
                holder.nameText.setTextColor(textColor);
                holder.scoreText.setTextColor(textColor);
                holder.categoryText.setTextColor(textColor);

            } catch (Exception e) {
                // Fallback to default colors if gradient parsing fails
                holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white)
                );
                setDefaultTextColors(holder);
            }
        } else {
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white)
            );
            setDefaultTextColors(holder);
        }

        // Highlight current user's entry (keep this last to override colors if needed)
        if (entry.getUserId() == currentUserId) {
            GradientDrawable highlight = new GradientDrawable();
            highlight.setShape(GradientDrawable.RECTANGLE);
            highlight.setStroke(4, ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            if (holder.itemView.getBackground() != null) {
                // Preserve the existing background if it exists
                highlight.setColor(((GradientDrawable) holder.itemView.getBackground()).getColor());
            }
            holder.itemView.setBackground(highlight);

            // Force dark text for highlighted items for better visibility
            int highlightTextColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.leaderboard_text_dark);
            holder.rankText.setTextColor(highlightTextColor);
            holder.nameText.setTextColor(highlightTextColor);
            holder.scoreText.setTextColor(highlightTextColor);
            holder.categoryText.setTextColor(highlightTextColor);
        }
    }

    // Helper method to check if color is dark
    private boolean isColorDark(String colorCode) {
        if (colorCode == null || colorCode.equals("default")) return false;
        try {
            // For gradient, just check the first color
            if (colorCode.startsWith("linear-gradient") || colorCode.startsWith("radial-gradient")) {
                String firstColor = colorCode.split(",")[1].trim(); // Get first color after gradient definition
                firstColor = firstColor.split(" ")[0].trim(); // Remove any percentage
                int color = Color.parseColor(firstColor);
                return isColorActuallyDark(color);
            } else {
                // Regular color
                int color = Color.parseColor(colorCode);
                return isColorActuallyDark(color);
            }
        } catch (Exception e) {
            return false;
        }
    }

    // Helper method to calculate luminance
    private boolean isColorActuallyDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    // Helper method for default text colors
    private void setDefaultTextColors(ViewHolder holder) {
        int defaultTextColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.leaderboard_text_dark);
        holder.rankText.setTextColor(defaultTextColor);
        holder.nameText.setTextColor(defaultTextColor);
        holder.scoreText.setTextColor(defaultTextColor);
        holder.categoryText.setTextColor(defaultTextColor);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankText;
        TextView nameText;
        TextView scoreText;
        TextView categoryText;  // Fixed this line (removed BreakIterator)

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.rankText);
            nameText = itemView.findViewById(R.id.nameText);
            scoreText = itemView.findViewById(R.id.scoreText);
            categoryText = itemView.findViewById(R.id.categoryText);  // Added this line
        }
    }

    public void updateData(List<LeaderboardActivity.LeaderboardEntry> newEntries) {
        entries.clear();
        entries.addAll(newEntries);
        notifyDataSetChanged();
    }
}