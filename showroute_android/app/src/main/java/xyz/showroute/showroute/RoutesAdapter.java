package xyz.showroute.showroute;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jdrc8 on 11/04/2018.
 */

public class RoutesAdapter extends BaseAdapter {

    public ArrayList<Route> routes;
    LayoutInflater inflater;

    public RoutesAdapter(Context context, Route[] routes){
        this.routes = new ArrayList<>(Arrays.asList(routes));
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return routes.size();
    }

    @Override
    public Object getItem(int i) {
        return routes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.route_spinner_item, null);

        TextView nameTextView = (TextView)view.findViewById(R.id.text_name);
        nameTextView.setText(routes.get(i).name);
        //nameTextView.setTextColor(routes.get(i).color);
        view.findViewById(R.id.layout_color).setBackgroundColor(routes.get(i).color);
        return view;
    }
}
