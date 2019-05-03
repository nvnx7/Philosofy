package com.philosofy.nvn.philosofy.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.philosofy.nvn.philosofy.R;
import com.philosofy.nvn.philosofy.custom.ToolModel;
import com.philosofy.nvn.philosofy.custom.ToolType;

import java.util.ArrayList;

public class ToolsAdapter extends RecyclerView.Adapter<ToolsAdapter.ToolsViewHolder> {

    private ArrayList<ToolModel> mToolsList;
    final private OnToolSelectedListener mOnToolSelectedListener;

    public ToolsAdapter(Context context, OnToolSelectedListener onToolSelectedListener) {
        mOnToolSelectedListener = onToolSelectedListener;

        mToolsList = new ArrayList<>();
        mToolsList.add(new ToolModel(context.getString(R.string.text_tool_name),
                ToolType.TEXT, R.drawable.ic_add_text));
        mToolsList.add(new ToolModel(context.getString(R.string.image_tool_name),
                ToolType.PHOTO, R.drawable.ic_tool_photo));
        mToolsList.add(new ToolModel(context.getString(R.string.background_tool_name),
                ToolType.BACKGROUND, R.drawable.ic_tool_background));
        mToolsList.add(new ToolModel(context.getString(R.string.effects_tool_name),
                ToolType.EFFECTS, R.drawable.ic_tool_effects));
        mToolsList.add(new ToolModel(context.getString(R.string.quote_tool_name),
                ToolType.QUOTE, R.drawable.ic_opening_quote));
    }

    public interface OnToolSelectedListener{
        void onToolSelected(ToolType toolType);
    }

    @NonNull
    @Override
    public ToolsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.itemview_tools, viewGroup, false);

        return new ToolsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolsViewHolder toolsViewHolder, int i) {
        ToolModel tool = mToolsList.get(i);
        toolsViewHolder.toolIcon.setImageResource(tool.getToolIcon());
        toolsViewHolder.toolName.setText(tool.getToolName());
    }

    @Override
    public int getItemCount() {
        return mToolsList.size();
    }

    class ToolsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView toolIcon;
        TextView toolName;

        ToolsViewHolder(View itemView) {
            super(itemView);
            toolIcon = itemView.findViewById(R.id.tool_icon_imageview);
            toolName = itemView.findViewById(R.id.tool_name_textview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedToolIndex = getAdapterPosition();
            mOnToolSelectedListener.onToolSelected(mToolsList.get(clickedToolIndex).getToolType());
        }
    }
}
