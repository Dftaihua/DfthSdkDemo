package com.example.administrator.mybluetooth.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2016/11/25 15:16
* 计算listview在ScrollView中的高度,避免只显示一条数据
*/
public class MeasureListHeightUtils {

    public static void setListViewHeightBasedOnChildren(ListView listView , ArrayAdapter arrayAdapter) {

        int totalHeight = 0;
        for (int i = 0, len = arrayAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = arrayAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (arrayAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView , BaseAdapter baseAdapter) {

        int totalHeight = 0;
        for (int i = 0, len = baseAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = baseAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (baseAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
