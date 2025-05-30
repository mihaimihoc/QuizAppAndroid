package com.example.quizz_project;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ColorSelectionDialog extends Dialog {
    private SoundManager soundManager;
    private final int userId;
    private RequestQueue requestQueue;
    private String selectedColorCode = "default";
    private GridLayout colorsGrid;
    private final int COLUMN_COUNT = 4;

    public ColorSelectionDialog(Context context, int userId) {
        super(context);
        this.userId = userId;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_color_selection);
        soundManager = SoundManager.getInstance(getContext());

        colorsGrid = findViewById(R.id.colorsGrid);
        Button confirmButton = findViewById(R.id.confirmButton);
        ImageView closeButton = findViewById(R.id.closeButton);

        // Initialize grid
        colorsGrid.setColumnCount(COLUMN_COUNT);

        loadUserColors();

        closeButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            dismiss();
        });
        confirmButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            confirmColorSelection();
        });
    }

    private void loadUserColors() {
        String url = DatabaseHelper.SERVER_URL + "get_user_colors.php?user_id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray colors = jsonResponse.getJSONArray("colors");

                            // First check current background color
                            String currentColor = getCurrentBackgroundColor();
                            if (currentColor != null) {
                                selectedColorCode = currentColor;
                            }

                            populateColorsGrid(colors);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Error loading colors", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    private String getCurrentBackgroundColor() {
        if (getContext() instanceof ProfileActivity) {
            ProfileActivity activity = (ProfileActivity) getContext();
            ConstraintLayout profileLayout = activity.findViewById(R.id.profileLayout);
            Drawable background = profileLayout.getBackground();

            if (background instanceof ColorDrawable) {
                // Default white background
                return "default";
            } else if (background instanceof GradientDrawable) {
                // Try to get the color from ProfileActivity
                return activity.getCurrentColorCode();
            }
        }
        return null;
    }

    private void populateColorsGrid(JSONArray colors) throws JSONException {
        // Clear any existing views
        colorsGrid.removeAllViews();

        // First check if we have a selected color from server
        boolean hasSelectedColor = false;

        // Check colors array first
        for (int i = 0; i < colors.length(); i++) {
            JSONObject color = colors.getJSONObject(i);
            if (color.getString("is_selected").equals("1")) {
                selectedColorCode = color.getString("color_code");
                hasSelectedColor = true;
                break;
            }
        }

        // Add default color option (white with X mark)
        // Only set as selected if no color is selected
        addColorOption("default", 0, 0, !hasSelectedColor && selectedColorCode.equals("default"));

        // Add user's unlocked colors
        for (int i = 0; i < colors.length(); i++) {
            JSONObject color = colors.getJSONObject(i);
            String colorCode = color.getString("color_code");
            boolean isSelected = colorCode.equals(selectedColorCode);

            // Calculate position (+1 because we added default first)
            int position = i + 1;
            int row = position / COLUMN_COUNT;
            int column = position % COLUMN_COUNT;

            addColorOption(colorCode, row, column, isSelected);
        }
    }

    private void addColorOption(String colorCode, int row, int column, boolean isSelected) {
        View colorView = new View(getContext());

        // Fixed size for color squares
        int itemSize = dpToPx(80); // 80dp size for each color square

        GridLayout.Spec rowSpec = GridLayout.spec(row);
        GridLayout.Spec colSpec = GridLayout.spec(column);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.width = itemSize;
        params.height = itemSize;
        params.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));

        colorView.setLayoutParams(params);

        if (colorCode.equals("default")) {
            colorView.setBackgroundResource(R.drawable.default_color_background);
        } else {
            GradientDrawable drawable = GradientParser.parseGradient(colorCode, "rectangle");
            colorView.setBackground(drawable);
        }

        if (isSelected) {
            // Apply selected border
            GradientDrawable border = new GradientDrawable();
            border.setShape(GradientDrawable.RECTANGLE);
            border.setStroke(dpToPx(3), Color.BLUE);
            if (colorView.getBackground() instanceof GradientDrawable) {
                border.setColor(((GradientDrawable) colorView.getBackground()).getColor());
            }
            colorView.setBackground(border);
        }

        final String finalColorCode = colorCode;
        colorView.setOnClickListener(v -> {
            soundManager.playRadioButtonSound();
            selectedColorCode = finalColorCode;
            updateSelectionUI(colorView);
        });

        colorsGrid.addView(colorView);
    }

    private void updateSelectionUI(View selectedView) {
        // Update all views to show selection state
        for (int i = 0; i < colorsGrid.getChildCount(); i++) {
            View child = colorsGrid.getChildAt(i);
            Object background = child.getBackground();

            if (background instanceof GradientDrawable) {
                GradientDrawable gd = (GradientDrawable) background;
                gd.setStroke(0, Color.TRANSPARENT);

                // Restore original color if needed
                if (child.getTag() != null && child.getTag() instanceof GradientDrawable) {
                    child.setBackground((GradientDrawable) child.getTag());
                }
            }
        }

        // Highlight the selected view
        GradientDrawable selectedBackground = new GradientDrawable();
        selectedBackground.setShape(GradientDrawable.RECTANGLE);
        selectedBackground.setStroke(dpToPx(3), Color.BLUE);

        // Keep original background as tag
        if (selectedView.getBackground() instanceof GradientDrawable) {
            selectedView.setTag(selectedView.getBackground());
            GradientDrawable original = (GradientDrawable) selectedView.getBackground();
            selectedBackground.setColor(original.getColor());
        }

        selectedView.setBackground(selectedBackground);
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void confirmColorSelection() {
        String url = DatabaseHelper.SERVER_URL + "update_selected_color.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            // Update the UI immediately
                            updateProfileBackground(selectedColorCode);
                            dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error updating color", Toast.LENGTH_SHORT).show();
                    dismiss();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("color_code", selectedColorCode);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void updateProfileBackground(String colorCode) {
        if (getContext() instanceof ProfileActivity) {
            ProfileActivity activity = (ProfileActivity) getContext();
            activity.runOnUiThread(() -> {
                ConstraintLayout profileLayout = activity.findViewById(R.id.profileLayout);
                if (colorCode.equals("default")) {
                    profileLayout.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
                } else {
                    try {
                        GradientDrawable drawable = GradientParser.parseGradient(colorCode, "rectangle");
                        profileLayout.setBackground(drawable);
                    } catch (Exception e) {
                        // Fallback to default if gradient parsing fails
                        profileLayout.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
                    }
                }
            });
        }
    }

}