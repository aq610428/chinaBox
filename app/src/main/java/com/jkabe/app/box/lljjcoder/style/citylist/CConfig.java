package com.jkabe.app.box.lljjcoder.style.citylist;


import com.jkabe.app.box.lljjcoder.style.citylist.bean.CityInfoBean;

/**
 * 作者：liji on 2017/5/21 16:23
 * 邮箱：lijiwork@sina.com
 * QQ ：275137657
 */

public class CConfig {

    private static CityInfoBean sCityInfoBean;

    public static void setCity(CityInfoBean city) {
        sCityInfoBean = city;
    }

    public static CityInfoBean getCitySelected() {

        return sCityInfoBean;
    }
}
