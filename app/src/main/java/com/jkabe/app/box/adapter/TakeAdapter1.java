package com.jkabe.app.box.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jkabe.app.box.bean.OrderBean;
import com.jkabe.app.box.box.OrderDetileActivity;
import com.jkabe.app.box.box.OrderDetileActivity1;
import com.jkabe.app.box.box.fragement.TakeFragment;
import com.jkabe.app.box.ui.PreviewActivity;
import com.jkabe.app.box.util.Utility;
import com.jkabe.box.R;

import java.util.List;


/**
 * @author: zt
 * @date: 2020/9/22
 * @name:TakeAdapter
 */
public class TakeAdapter1 extends AutoRVAdapter {
    private List<OrderBean> list;
    private TakeFragment allFragment;

    public TakeAdapter1(TakeFragment allFragment, List<OrderBean> list) {
        super(allFragment.getContext(), list);
        this.list = list;
        this.allFragment = allFragment;
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.item_take_vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        OrderBean orderBean = list.get(position);
        if (orderBean.getItems() != null && orderBean.getItems().size() > 0) {
            RecyclerView recyclerView = vh.getRecyclerView(R.id.recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(allFragment.getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            OrderListAdapter adapter = new OrderListAdapter(allFragment.getContext(), orderBean.getItems());
            recyclerView.setAdapter(adapter);
            vh.getTextView(R.id.text_num).setText("共" + orderBean.getItems().size() + "件");
            adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int stats = orderBean.getOrderStatus();
                    Intent intent = null;
                    if (3 == stats) {
                        intent = new Intent(allFragment.getContext(), OrderDetileActivity1.class);
                    } else {
                        intent = new Intent(allFragment.getContext(), OrderDetileActivity.class);
                    }
                    intent.putExtra("orderStatus", orderBean.getOrderStatus()+"");
                    intent.putExtra("id", orderBean.getId());
                    allFragment.getContext().startActivity(intent);
                }
            });
        }
        vh.getTextView(R.id.text_count).setText("实付 ￥" + orderBean.getGoodMoney());

        if (!Utility.isEmpty(orderBean.getStringOrdertime())) {
            String stringOrdertime = orderBean.getStringOrdertime();
            vh.getTextView(R.id.text_date).setText(stringOrdertime.substring(0,10) + " " + stringOrdertime.substring(stringOrdertime.length()-8, stringOrdertime.length()));
        }
        vh.getTextView(R.id.text_stats).setText("待发货");
        vh.getTextView(R.id.text_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allFragment.showUrge(orderBean.getId());
            }
        });
        vh.getTextView(R.id.text_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(allFragment.getContext(), PreviewActivity.class);
                intent.putExtra("name", "加入社群");
                intent.putExtra("url", "http://openapi.jkabe.com/golo/about");
                allFragment.getContext().startActivity(intent);
            }
        });
    }

    public void setData(List<OrderBean> beanList) {
        this.list = beanList;

    }
}
