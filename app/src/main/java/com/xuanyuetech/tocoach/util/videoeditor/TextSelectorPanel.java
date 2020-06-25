package com.xuanyuetech.tocoach.util.videoeditor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.xuanyuetech.tocoach.R;

import org.jetbrains.annotations.NotNull;

public class TextSelectorPanel extends LinearLayout {
    private Context mContext;
    private OnTextSelectorListener mOnTextSelectorListener;

    public static int[] colors = {R.color.white, R.color.khaki, R.color.red_watermelon, R.color.blue_flat_90,
            R.color.sky_blue_70, R.color.blue_flat_90, R.color.orange, R.color.blue};


    public TextSelectorPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.panel_video_editor_graffiti_text_selector, this);

        RecyclerView mTextViews = view.findViewById(R.id.recycler_text);
        TextInfo[] infos = initTextInfos();

        mTextViews.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mTextViews.setAdapter(new TextEffectListAdapter(infos));

    }

    public void setOnTextSelectorListener(OnTextSelectorListener listener) {
        mOnTextSelectorListener = listener;
    }

    private TextInfo[] initTextInfos() {
        TextInfo[] infos = new TextInfo[colors.length];
        for (int i = 0; i < infos.length; i++) {
            TextInfo textInfo = new TextInfo();
            textInfo.text = "趣练";
            infos[i] = textInfo;
            textInfo.colorID = colors[i];
            textInfo.alpha = 0.8f;

            if (i > 4) {
                textInfo.colorID = R.color.white;
                textInfo.shadowRadius = 20;
                textInfo.shadowColor = mContext.getColor(colors[i]);
            }
        }
        return infos;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        StrokedTextView mText;

        ItemViewHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.video_editor_text_select_panel_item_text);
            mText.setClickable(true);
            mText.setOnClickListener(v -> {
                if (mOnTextSelectorListener != null) {
                    mOnTextSelectorListener.onTextSelected(mText);
                }
            });
        }
    }

    private class TextEffectListAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private TextInfo[] mInfos;

        TextEffectListAdapter(TextInfo[] infos) {
            this.mInfos = infos;
        }

        @NotNull
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.component_video_editor_graffiti_sticker_text, parent, false);

            // Return a new holder instance
            return new ItemViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            // Get the data model based on position
            final TextInfo info = mInfos[position];

            // Set item views based on your views and data model
            holder.mText.setText(info.text);
            holder.mText.setTextColor(mContext.getColor(info.colorID));
            holder.mText.setTypeface(info.typeface, info.style);
            holder.mText.setStrokeColor(info.strokeColor);
            if (info.shadowRadius > 0) {
                holder.mText.setShadowLayer(info.shadowRadius, info.shadowDx, info.shadowDy, info.shadowColor);
            }
        }

        @Override
        public int getItemCount() {
            return mInfos.length;
        }
    }

    public interface OnTextSelectorListener {
        void onTextSelected(StrokedTextView textView);
    }

    private static class TextInfo {
        String text;
        int colorID;
        Typeface typeface = Typeface.MONOSPACE;
        int style = Typeface.BOLD;
        float alpha = 1;
        int shadowColor = Color.TRANSPARENT;
        int shadowRadius;
        int shadowDx;
        int shadowDy;
        int strokeColor;
        float strokeWidth;
    }
}
