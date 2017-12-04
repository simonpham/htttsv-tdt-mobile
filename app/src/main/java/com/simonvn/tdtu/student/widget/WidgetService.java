package com.simonvn.tdtu.student.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by bichan on 9/23/17.
 */

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListProvider(this.getApplicationContext(), intent);
    }
}
