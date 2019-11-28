package com.you.common.utils.export.demo;

import com.you.common.utils.export.annotation.Export;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Export(sheetName = "测试导出列表")
public class DemoExport implements Serializable {

    /**
     * 以用户编号为维度合并行 【例如统计用户订单】
     * select
     *      u.user_no userNo,
     *      u.user_no userNoShow,
     *      u.user_name userName,
     *      o.order_no orderNo,
     *      o.pay_time payTime,
     *      o.date date,
     *      o.amt amt,
     *      o.status status
     * from
     *      user u left join order o on o.user_no = u.user_no;
     */

    @Export(merge = "{'startColumn':0,'endColumn':1}")//不包含带有merge属性列,即不包含userNo
    private String userNo;
    @Export(desc = "用户编号")
    private String userNoShow;
    @Export(desc = "用户名称")
    private String userName;
    @Export(desc = "订单编号")
    private String orderNo;
    @Export(desc = "支付时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;
    @Export(desc = "日期", format = "yyyy-MM-dd")
    private Date date;
    @Export(desc = "金额")
    private BigDecimal amt;
    @Export(desc = "状态", mapping = "{'0':'待支付','1':'已支付','2':'已完成'}")
    private String status;

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserNoShow() {
        return userNoShow;
    }

    public void setUserNoShow(String userNoShow) {
        this.userNoShow = userNoShow;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
