package com.extensivepro.alipay;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import com.extensivepro.mxl.app.bean.PaymentParameter;
import com.extensivepro.mxl.util.Logger;

public class AlixUtil
{
    private static final String TAG = AlixUtil.class.getSimpleName();
    
    /**
     * sign the order info. 对订单信息进行签名
     * 
     * @param signType
     *            签名方式
     * @param content
     *            待签名订单信息
     * @return
     */
    public static String sign(String signType, String content) {
        return Rsa.sign(content, PartnerConfig.RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     * 
     * @return
     */
    public static String getSignType() {
        String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
        return getSignType;
    }

    /**
     * get the char set we use. 获取字符集
     * 
     * @return
     */
    static String getCharset() {
        String charset = "charset=" + "\"" + "utf-8" + "\"";
        return charset;
    }
    
    public static String getOrderInfo(PaymentParameter paymentParameter)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("partner=" + "\"" + PartnerConfig.PARTNER + "\"");
        buffer.append("&");
        buffer.append("seller=" + "\"" + PartnerConfig.SELLER + "\"");
        buffer.append("&");
        buffer.append("out_trade_no=" + "\""
                + paymentParameter.getOut_trade_no() + "\"");
        buffer.append("&");
        buffer.append("subject=" + "\"" + paymentParameter.getSubject() + "\"");
        buffer.append("&");
        buffer.append("body=" + "\"" + paymentParameter.getBody() + "\"");
        buffer.append("&");
        buffer.append("total_fee=" + "\"" + paymentParameter.getTotal_fee()
                + "\"");
        buffer.append("&");
        buffer.append("notify_url=" + "\"");
        buffer.append(paymentParameter.getNotify_url() + "\"");
        Logger.d(TAG, "getOrderInfo()[" + buffer.toString() + "]");
        return buffer.toString();
    }

    /**
     * check some info.the partner,seller etc. 检测配置信息
     * partnerid商户id，seller收款帐号不能为空
     * 
     * @return
     */
    public static boolean checkInfo() {
        String partner = PartnerConfig.PARTNER;
        String seller = PartnerConfig.SELLER;
        if (partner == null || partner.length() <= 0 || seller == null
                || seller.length() <= 0)
            return false;

        return true;
    }
}
