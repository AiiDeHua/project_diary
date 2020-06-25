package com.xuanyuetech.tocoach.util.videoeditor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuanyuetech.tocoach.R;

import org.jetbrains.annotations.NotNull;

public class PaintSelectorPanel extends LinearLayout {
    private Context mContext;
    private OnPaintSelectorListener mOnPaintSelectorListener;
    private ImageView mCurColorView;
    private SeekBar mSizeSeekBar;
    private ImageView mSizeImage;
    private PaintColorListAdapter mAdapter;

    private static int PAINT_MAX_SIZE = 100;

    public static int[] colors = {R.color.paint1, R.color.paint2, R.color.paint3, R.color.paint4,
            R.color.paint5, R.color.paint6, R.color.paint7};

    public PaintSelectorPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.panel_video_editor_graffiti_paint_selector, this);
        mSizeImage = view.findViewById(R.id.paint_size_image);
        mSizeSeekBar = view.findViewById(R.id.paint_size_seek);
        Button mUndoText = view.findViewById(R.id.button_undo);
        Button mClearText = view.findViewById(R.id.button_clear);

        mUndoText.setOnClickListener(v -> {
            if (mOnPaintSelectorListener != null) {
                mOnPaintSelectorListener.onPaintUndoSelected();
            }
        });

        mClearText.setOnClickListener(v -> {
            if (mOnPaintSelectorListener != null) {
                mOnPaintSelectorListener.onPaintClearSelected();
            }
        });

        final int step = 1;
        final int max = 50;
        final int min = 3;

        mSizeSeekBar.setMax((max - min) / step);
        mSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float scale = ((float) (min + (progress * step))) / 100;

                mSizeImage.setScaleX(scale);
                mSizeImage.setScaleY(scale);
                if (mOnPaintSelectorListener != null) {
                    mOnPaintSelectorListener.onPaintSizeSelected((int) (PAINT_MAX_SIZE * scale));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        RecyclerView mColorListView = view.findViewById(R.id.recycler_paint_color);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mColorListView.setLayoutManager(layoutManager);
        mAdapter = new PaintColorListAdapter(colors);
        mColorListView.setAdapter(mAdapter);

    }

    public void setup() {
        mSizeSeekBar.setProgress(10);
        mAdapter.setPosition(1);
        mAdapter.notifyDataSetChanged();
    }

    public void setOnPaintSelectorListener(OnPaintSelectorListener listener) {
        mOnPaintSelectorListener = listener;
    }

    public interface OnPaintSelectorListener {
        void onPaintColorSelected(int color);

        void onPaintSizeSelected(int size);

        void onPaintUndoSelected();

        void onPaintClearSelected();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        ItemViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.paint_color_view);
        }
    }

    private class PaintColorListAdapter extends RecyclerView.Adapter<PaintSelectorPanel.ItemViewHolder> {
        private int[] mColors;
        private int mPosition = 0;

        PaintColorListAdapter(int[] colors) {
            this.mColors = colors;
        }

        public void setPosition(int position) {
            mPosition = position;
        }

        @NotNull
        @Override
        public PaintSelectorPanel.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.component_video_editor_graffiti_paint_color_selection, parent, false);
            return new ItemViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(final PaintSelectorPanel.ItemViewHolder holder, int position) {
            final int color = mColors[position];
            final int colorInt = ContextCompat.getColor(mContext,color);
            holder.mImageView.setColorFilter(colorInt);
            holder.mImageView.setOnClickListener(v -> {
                if (mCurColorView != null) {
                    mCurColorView.setSelected(false);
                }
                mCurColorView = holder.mImageView;
                mCurColorView.setSelected(true);
                mSizeImage.setColorFilter(colorInt);
                if (mOnPaintSelectorListener != null) {
                    mOnPaintSelectorListener.onPaintColorSelected(colorInt);
                }
            });
            if (mPosition == position) {
                if (mCurColorView != null) {
                    mCurColorView.setSelected(false);
                }
                mCurColorView = holder.mImageView;
                mCurColorView.setSelected(true);
                mSizeImage.setColorFilter(colorInt);
                if (mOnPaintSelectorListener != null) {
                    mOnPaintSelectorListener.onPaintColorSelected(colorInt);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mColors.length;
        }
    }
}
