package com.philosofy.nvn.philosofy.custom;

public class ToolModel {

    private String mToolName;
    private int mToolIcon;
    private ToolType mToolType;

    public ToolModel(String toolName, ToolType toolType, int toolIcon) {
        mToolName = toolName;
        mToolIcon = toolIcon;
        mToolType = toolType;
    }

    public int getToolIcon() {
        return mToolIcon;
    }

    public String getToolName() {
        return mToolName;
    }

    public ToolType getToolType() {
        return mToolType;
    }
}

