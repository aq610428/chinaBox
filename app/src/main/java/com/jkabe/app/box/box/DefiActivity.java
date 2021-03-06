package com.jkabe.app.box.box;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jkabe.app.box.base.BaseActivity;
import com.jkabe.app.box.bean.CommonalityModel;
import com.jkabe.app.box.bean.TabBean;
import com.jkabe.app.box.bean.UsdtBean;
import com.jkabe.app.box.config.Api;
import com.jkabe.app.box.config.NetWorkListener;
import com.jkabe.app.box.config.okHttpModel;
import com.jkabe.app.box.util.Constants;
import com.jkabe.app.box.util.JsonParse;
import com.jkabe.app.box.util.Md5Util;
import com.jkabe.app.box.util.SaveUtils;
import com.jkabe.app.box.util.ToastUtil;
import com.jkabe.app.box.util.Utility;
import com.jkabe.app.box.weight.ClearEditText;
import com.jkabe.box.R;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author: zt
 * @date: 2020/10/22
 * @name:DefiActivity
 */
public class DefiActivity extends BaseActivity implements NetWorkListener {
    private TextView title_text_tv, title_left_btn, text_ensure, title_right_btn, text_name, text_box;
    private ClearEditText et_num;
    private TabBean tabBean;
    private TextView text_user, text_deif, text_pay;
    private String type;
    private UsdtBean usdtBean;


    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_defi);
    }

    @Override
    protected void initView() {
        text_box = getView(R.id.text_box);
        text_name = getView(R.id.text_name);
        text_pay = getView(R.id.text_pay);
        text_deif = getView(R.id.text_deif);
        text_user = getView(R.id.text_user);
        title_right_btn = getView(R.id.title_right_btn);
        et_num = getView(R.id.et_num);
        title_text_tv = getView(R.id.title_text_tv);
        title_left_btn = getView(R.id.title_left_btn);
        text_ensure = getView(R.id.text_ensure);
        title_left_btn.setOnClickListener(this);
        text_ensure.setOnClickListener(this);
        title_text_tv.setText("理财");
        title_right_btn.setText("收益记录");
        title_right_btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_left_btn:
                finish();
                break;
            case R.id.title_right_btn:
                startActivity(new Intent(this, TabDefiActivity.class));
                break;
            case R.id.text_ensure:
                query();
                break;
        }
    }

    @Override
    protected void initData() {
        String mouth = getIntent().getStringExtra("mouth");
        String lv = getIntent().getStringExtra("lv");
        type = getIntent().getStringExtra("type");
        tabBean = (TabBean) getIntent().getSerializableExtra("tabBean");
        if (tabBean != null) {
            text_user.setText(tabBean.getManage_amount() + "");
            text_deif.setText(tabBean.getManage_num() + "");
            text_pay.setText(tabBean.getProfit_amount() + "");
            String str = "本方案是BOXDefi项目定投" + mouth + "的理财方案，所有理财资产锁仓" + mouth +
                    "，到期后一次性释放理财收益。\n本方案收益率为" + lv + "\nBOX DeFi项目定投500BOX起，按100BOX的整数倍递增，最高接受10万BOX定投。" +
                    "\n本方案可在投资期内赎回，收益超过本金不予提前赎回。\n不满一个月内赎回将产生违约金 \n提前赎回不计投资收益；超过一个月赎回计算当月投资收益+零存日收益\n理财收益按月、对应存入日期发送；到期后一次性释放理财本金。";
            text_name.setText(str);
        }
        load();
    }


    public void query() {
        String amount = et_num.getText().toString();
        if (Utility.isEmpty(amount)) {
            ToastUtil.showToast("理财金额不能为空");
            return;
        }

        if (new BigDecimal(amount).doubleValue() < 500) {
            ToastUtil.showToast("理财金额不能小于500");
            return;
        }

        String sign = "amount=" + amount + "&memberid=" + SaveUtils.getSaveInfo().getId() + "&partnerid=" + Constants.PARTNERID + "&type=" + type + Constants.SECREKEY;
        Map<String, String> params = okHttpModel.getParams();
        params.put("apptype", Constants.TYPE);
        params.put("amount", amount + "");
        params.put("memberid", SaveUtils.getSaveInfo().getId());
        params.put("partnerid", Constants.PARTNERID);
        params.put("type", type + "");
        params.put("sign", Md5Util.encode(sign));
        okHttpModel.post(Api.LIST_MEMBER_BOX, params, Api.LIST_MEMBER_BOX_ID, this);
    }


    public void load() {
        String sign = "memberid=" + SaveUtils.getSaveInfo().getId() + "&partnerid=" + Constants.PARTNERID + Constants.SECREKEY;
        showProgressDialog(this, false);
        Map<String, String> params = okHttpModel.getParams();
        params.put("apptype", Constants.TYPE);
        params.put("memberid", SaveUtils.getSaveInfo().getId());
        params.put("partnerid", Constants.PARTNERID);
        params.put("limit", "10");
        params.put("page", "1");
        params.put("sign", Md5Util.encode(sign));
        okHttpModel.get(Api.MINING_BAL_BOX, params, Api.GETRECORD_BOX_ID, this);
    }


    @Override
    public void onSucceed(JSONObject object, int id, CommonalityModel commonality) {
        if (object != null && commonality != null && !Utility.isEmpty(commonality.getStatusCode())) {
            if (Constants.SUCESSCODE.equals(commonality.getStatusCode())) {
                switch (id) {
                    case Api.LIST_MEMBER_BOX_ID:
                        ToastUtil.showToast(commonality.getErrorDesc());
                        startActivity(new Intent(this, TabActivity.class));
                        finish();
                        break;
                    case Api.GETRECORD_BOX_ID:
                        usdtBean = JsonParse.getJSONObjectUsdtBean(object);
                        if (usdtBean != null) {
                            updateView(usdtBean);
                        }
                        break;
                }
            } else {
                ToastUtil.showToast(commonality.getErrorDesc());
            }
        }
        stopProgressDialog();
    }

    private void updateView(UsdtBean usdtBean) {
        text_box.setText("可投资余额" + usdtBean.getBox().getUserable() + "BOX");
    }

    @Override
    public void onFail() {
        stopProgressDialog();
    }

    @Override
    public void onError(Exception e) {
        stopProgressDialog();
    }
}
