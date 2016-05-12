package com.wings.zilizili.ui.fragment;

/**
 * Created by wing on 2016/5/12.
 */
public class LeftMenuClickEvent {
    private int itemId;
    private boolean isButton;

    public LeftMenuClickEvent(boolean isButton, int itemId) {

        this.isButton = isButton;
        this.itemId = itemId;
    }

    public boolean isButton() {
        return isButton;
    }

    public void setButton(boolean button) {
        isButton = button;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
