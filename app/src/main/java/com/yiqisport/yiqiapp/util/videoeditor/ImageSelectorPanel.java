package com.yiqisport.yiqiapp.util.videoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yiqisport.yiqiapp.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class ImageSelectorPanel extends LinearLayout {
    private Context mContext;
    private OnImageSelectedListener mOnImageSelectedListener;

    private static String[] imagePaths = {
            "arrow_narrow_long", "arrow_narrow_long_blue", "circle","line", "line_yellow",
            "rect", "wrong", "correct", "star", "heart", "notes", "arrow_solid","arrow_solid_narrow",
            "arrow_top","arrow_top2"
    };

    public ImageSelectorPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.panel_video_editor_graffiti_image_selector, this);
        RecyclerView mImageListView = view.findViewById(R.id.recycler_paint_image);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mImageListView.setLayoutManager(layoutManager);
        mImageListView.setAdapter(new ImageListAdapter());
    }

    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        mOnImageSelectedListener = listener;
    }

    public interface OnImageSelectedListener {
        void onImageSelected(Drawable drawable);
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView mIcon;
        TextView mName;

        ItemViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.item_sticker_image_image);
            mName = itemView.findViewById(R.id.item_sticker_image_text);
        }
    }

    private class ImageListAdapter extends RecyclerView.Adapter<ImageSelectorPanel.ItemViewHolder> {

        @NotNull
        @Override
        public ImageSelectorPanel.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.component_video_editor_graffiti_sticker_image, parent, false);
            return new ItemViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(final ImageSelectorPanel.ItemViewHolder holder, int position) {
            try {
                final String imagePath = "sticker_image/" + imagePaths[position] + ".png";
                InputStream is = mContext.getAssets().open(imagePath);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                holder.mName.setVisibility(GONE);
                holder.mIcon.setImageBitmap(bitmap);
                holder.mIcon.setOnClickListener(v -> {
                    if (mOnImageSelectedListener != null) {
                        mOnImageSelectedListener.onImageSelected(holder.mIcon.getDrawable());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return imagePaths.length;
        }
    }
}
