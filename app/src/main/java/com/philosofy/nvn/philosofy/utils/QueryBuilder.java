package com.philosofy.nvn.philosofy.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class QueryBuilder {

    static final int WIDTH_DEFAULT = 100;
    static final int WIDTH_MAX = 1000;
    static final int WIDTH_MIN = 0;

    static final int WEIGHT_DEFAULT = 400;
    static final int WEIGHT_MAX = 1000;
    static final int WEIGHT_MIN = 0;

    static final float ITALIC_DEFAULT = 0f;
    static final float ITALIC_MAX = 1f;
    static final float ITALIC_MIN = 0f;

    @NonNull
    private String mFamilyName;

    @Nullable
    private Float mWidth = null;

    @Nullable
    private Integer mWeight = null;

    @Nullable
    private Float mItalic = null;

    @Nullable
    private Boolean mBesteffort = null;

    public QueryBuilder(@NonNull String familyName) {
        mFamilyName = familyName;
    }

    public QueryBuilder withFamilyName(@NonNull String familyName) {
        mFamilyName = familyName;
        return this;
    }

    public QueryBuilder withWidth(float width) {
        if (width <= WIDTH_MIN) {
            throw new IllegalArgumentException("Width must be more than 0");
        }
        mWidth = width;
        return this;
    }

    public QueryBuilder withWeight(int weight) {
        if (weight <= WEIGHT_MIN || weight >= WEIGHT_MAX) {
            throw new IllegalArgumentException(
                    "Weight must be between 0 and 1000 (exclusive)");
        }
        mWeight = weight;
        return this;
    }

    public QueryBuilder withItalic(float italic) {
        if (italic < ITALIC_MIN || italic > ITALIC_MAX) {
            throw new IllegalArgumentException("Italic must be between 0 and 1 (inclusive)");
        }
        mItalic = italic;
        return this;
    }

    public QueryBuilder withBestEffort(boolean bestEffort) {
        mBesteffort = bestEffort;
        return this;
    }

    public String build() {
        if (mWeight == null && mWidth == null && mItalic == null && mBesteffort == null) {
            return mFamilyName;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("name=").append(mFamilyName);
        if (mWeight != null) {
            builder.append("&weight=").append(mWeight);
        }
        if (mWidth != null) {
            builder.append("&width=").append(mWidth);
        }
        if (mItalic != null) {
            builder.append("&italic=").append(mItalic);
        }
        if (mBesteffort != null) {
            builder.append("&besteffort=").append(mBesteffort);
        }
        return builder.toString();
    }
}
