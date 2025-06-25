package com.example.filemanager.DemoPage;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class HeaderItem extends AbstractItem<HeaderItem, HeaderItem.ViewHolder> {

    private final String title;

    public HeaderItem(String title) {
        this.title = title;
    }

    @Override
    public int getType() {
        return R.id.header_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_header;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<HeaderItem> {
        TextView headerText;

        public ViewHolder(View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.header_text);
        }

        @Override
        public void bindView(HeaderItem item, @NonNull List<Object> payloads) {
            headerText.setText(item.title);
        }

        @Override
        public void unbindView(@NonNull HeaderItem item) {
            headerText.setText(null);
        }

    }
}
