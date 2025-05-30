package com.example.quizz_project;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GradientParser {

    private static final String TAG = "GradientParser";

    public enum GradientType {
        LINEAR,
        RADIAL,
        REPEATING_LINEAR,
        REPEATING_RADIAL,
        SOLID
    }

    public static class GradientParams {
        public GradientType type;
        public String direction;
        public int[] colors;
        public float[] positions;
        public String shape;

        public GradientParams(GradientType type, String direction, int[] colors, float[] positions, String shape) {
            this.type = type;
            this.direction = direction;
            this.colors = colors;
            this.positions = positions;
            this.shape = shape;
        }
    }

    public static GradientDrawable parseGradient(String gradientString, String shape) {
        GradientParams params = parseGradientString(gradientString);
        if (params == null) {
            return createDefaultGradient(shape);
        }

        return createGradientDrawable(params, shape);
    }

    private static GradientParams parseGradientString(String gradientString) {
        if (gradientString == null || gradientString.isEmpty()) {
            return null;
        }

        try {
            if (gradientString.startsWith("linear-gradient")) {
                return parseLinearGradient(gradientString);
            } else if (gradientString.startsWith("radial-gradient")) {
                return parseRadialGradient(gradientString);
            } else if (gradientString.startsWith("repeating-linear-gradient")) {
                return parseLinearGradient(gradientString); // Same as linear for basic implementation
            } else if (gradientString.startsWith("repeating-radial-gradient")) {
                return parseRadialGradient(gradientString); // Same as radial for basic implementation
            } else if (gradientString.startsWith("#")) {
                return new GradientParams(GradientType.SOLID, null,
                        new int[]{Color.parseColor(gradientString)}, null, null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing gradient: " + gradientString, e);
        }

        return null;
    }

    private static GradientParams parseLinearGradient(String gradientString) {
        String content = gradientString.substring(gradientString.indexOf("(") + 1,
                gradientString.lastIndexOf(")"));

        String[] parts = content.split(",");
        String direction = parts[0].trim();

        List<Integer> colors = new ArrayList<>();
        List<Float> positions = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.contains("%")) {
                String[] colorParts = part.split(" ");
                if (colorParts.length >= 2) {
                    colors.add(parseColor(colorParts[0]));
                    positions.add(Float.parseFloat(colorParts[1].replace("%", "")) / 100f);
                }
            } else {
                colors.add(parseColor(part));
            }
        }

        // If positions were specified for some colors but not all, fill in the gaps
        if (!positions.isEmpty() && positions.size() < colors.size()) {
            positions = distributePositions(colors.size());
        }

        GradientType type = gradientString.startsWith("repeating") ?
                GradientType.REPEATING_LINEAR : GradientType.LINEAR;

        return new GradientParams(type, direction,
                toIntArray(colors), toFloatArray(positions), null);
    }

    private static GradientParams parseRadialGradient(String gradientString) {
        String content = gradientString.substring(gradientString.indexOf("(") + 1,
                gradientString.lastIndexOf(")"));

        // For basic implementation, we'll treat radial similar to linear
        // In a full implementation, you would parse the radial-specific parameters
        String[] parts = content.split(",");
        String direction = "center"; // Default for radial

        List<Integer> colors = new ArrayList<>();
        List<Float> positions = new ArrayList<>();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.contains("%")) {
                String[] colorParts = part.split(" ");
                if (colorParts.length >= 2) {
                    colors.add(parseColor(colorParts[0]));
                    positions.add(Float.parseFloat(colorParts[1].replace("%", "")) / 100f);
                }
            } else {
                colors.add(parseColor(part));
            }
        }

        // If positions were specified for some colors but not all, fill in the gaps
        if (!positions.isEmpty() && positions.size() < colors.size()) {
            positions = distributePositions(colors.size());
        }

        GradientType type = gradientString.startsWith("repeating") ?
                GradientType.REPEATING_RADIAL : GradientType.RADIAL;

        return new GradientParams(type, direction,
                toIntArray(colors), toFloatArray(positions), null);
    }

    private static GradientDrawable createGradientDrawable(GradientParams params, String shape) {
        GradientDrawable drawable = new GradientDrawable();

        // Set shape
        if ("oval".equalsIgnoreCase(shape)) {
            drawable.setShape(GradientDrawable.OVAL);
        } else {
            drawable.setShape(GradientDrawable.RECTANGLE);
        }

        // Set gradient type and orientation
        switch (params.type) {
            case LINEAR:
            case REPEATING_LINEAR:
                setLinearGradient(drawable, params);
                break;
            case RADIAL:
            case REPEATING_RADIAL:
                setRadialGradient(drawable, params);
                break;
            case SOLID:
                drawable.setColor(params.colors[0]);
                break;
        }

        return drawable;
    }

    private static void setLinearGradient(GradientDrawable drawable, GradientParams params) {
        GradientDrawable.Orientation orientation = parseOrientation(params.direction);
        drawable.setOrientation(orientation);
        drawable.setColors(params.colors);

        if (params.positions != null && params.positions.length == params.colors.length) {
            drawable.setGradientCenter(0.5f, 0.5f);
            drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        }
    }

    private static void setRadialGradient(GradientDrawable drawable, GradientParams params) {
        // For basic implementation, we'll use LINEAR_GRADIENT with center orientation
        // In a full implementation, you would use RADIAL_GRADIENT with proper parameters
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        drawable.setColors(params.colors);
        drawable.setGradientCenter(0.5f, 0.5f);
        drawable.setGradientRadius(Math.max(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()) / 2f);
    }

    private static GradientDrawable.Orientation parseOrientation(String direction) {
        if (direction == null) {
            return GradientDrawable.Orientation.LEFT_RIGHT;
        }

        switch (direction.toLowerCase()) {
            case "to right":
                return GradientDrawable.Orientation.LEFT_RIGHT;
            case "to left":
                return GradientDrawable.Orientation.RIGHT_LEFT;
            case "to bottom":
                return GradientDrawable.Orientation.TOP_BOTTOM;
            case "to top":
                return GradientDrawable.Orientation.BOTTOM_TOP;
            case "to bottom right":
                return GradientDrawable.Orientation.TL_BR;
            case "to bottom left":
                return GradientDrawable.Orientation.TR_BL;
            case "to top right":
                return GradientDrawable.Orientation.BL_TR;
            case "to top left":
                return GradientDrawable.Orientation.BR_TL;
            default:
                return GradientDrawable.Orientation.LEFT_RIGHT;
        }
    }

    private static int parseColor(String colorStr) {
        try {
            return Color.parseColor(colorStr.trim());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid color: " + colorStr);
            return Color.GRAY;
        }
    }

    private static GradientDrawable createDefaultGradient(String shape) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.GRAY);
        if ("oval".equalsIgnoreCase(shape)) {
            drawable.setShape(GradientDrawable.OVAL);
        } else {
            drawable.setShape(GradientDrawable.RECTANGLE);
        }
        return drawable;
    }

    // Helper methods to convert between List and array types
    private static int[] toIntArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static float[] toFloatArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static List<Float> distributePositions(int colorCount) {
        List<Float> positions = new ArrayList<>();
        if (colorCount == 1) {
            positions.add(0f);
            return positions;
        }

        float step = 1f / (colorCount - 1);
        for (int i = 0; i < colorCount; i++) {
            positions.add(i * step);
        }
        return positions;
    }
}