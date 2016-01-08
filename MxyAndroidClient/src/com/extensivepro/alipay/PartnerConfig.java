package com.extensivepro.alipay;

public class PartnerConfig
{

    // 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
    public static final String PARTNER = "2088901690968736";

    // 商户收款的支付宝账号
    public static final String SELLER = "2088901690968736";

    // 商户（RSA）私钥
    public static final String RSA_PRIVATE = "MIICXQIBAAKBgQCfQ61tbCmxc/wms9bw8OCO2W7AfP8Ax+58AzqbtTNYJkEEorZ/"
            + "gogy2v0vCZcaXb1bWGbHJJjNGrRKVgPjl67lKcUl5HBbLC9fUO2yl9NwovRzrFFh"
            + "tVuriFffcLGZKxPVmAUOjEwa10Tbch9xTIR8AcntpG3IXpvZucveMxay8QIDAQAB"
            + "AoGBAIbGmujSc6X75RJLdkWHUkEQt6ylGpbqmmLPyLUj7kNWfAcUOyRErkV7FG7N"
            + "ytVBJpE6ih9UVQqc4p1VlQzwu8tUduuRIgzP38fOWYZnycK7VyzGWsZG2LbUDp0h"
            + "weV9MSlNSHYoD57oG5P0AJ9PCuQoIx/ExKPPdOrfMmb2d7YRAkEAzRrFoK6X3KjH"
            + "tey0HshgoBoc/YbwZFPXf3G8zztKT913Tbi1qQSU41ADkHWGdP0P+/PF98qVXgA0"
            + "DcBDO0GOTwJBAMbI6jnCshM4awroFTuKJ8mRy0GBggTMYrzSVi4H/ZVCaeSEkmsg"
            + "pLJE9Q5hW6N4MWxefeaOcXu4kbGY19bOmr8CQAld7KKe9p6EMsKzMbKqMgd9yc8m"
            + "R3o9ffz7A8VvDsDA+37YLauvYuFSuSbyxxY2rHE0O9lKLjIhzj7GhguBve8CQFFN"
            + "2ec4B8euurFiTVTl4/IgzIaFZt15jyT1zsPTBtfmqxMJWi2w00wKwkRYc7e/sE9H"
            + "sqz7M9VLn2OJRTaZmFcCQQCoM7NSLUI0LuHgWapQj92+Nr03mkmDKzVav6ePxrCj"
            + "REJE1sItozvF7vH20ik7C0DBNqDIAO+WUkxyhZ1myppR";

    // 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
    public static final String RSA_ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD3IZWq9/ewdRMAidK+K"
            + "7EeAgKSVsjYTxT9f8AL HxGSd3p0CHiMrH4ahTknTZaMVv/rF7MApI0lghu4H9MqQH"
            + "mpD8nONycb7Z1WZpq+R3tS0Cxi/QoZ MTFQxgdSZGiIeeo12azIqwcUMN49BERfCjynG/"
            + "3d8aenu9nrrNcN6BnJzwIDAQAB";

    // 支付宝安全支付服务apk的名称，必须与assets目录下的apk名称一致
    public static final String ALIPAY_PLUGIN_NAME = "alipay_plugin_20120428msp.apk";

}
