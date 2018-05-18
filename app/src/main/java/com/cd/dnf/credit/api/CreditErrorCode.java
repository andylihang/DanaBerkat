package com.cd.dnf.credit.api;

import android.content.Context;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.view.ToastUtil;

/**
 * Created by jack on 2018/2/23.
 */

public class CreditErrorCode {
    public static void handleErrorCode(int code){
        switch (code){
            case 1:
                //银行账号格式不正确
                ToastUtil.showShort(R.string.code_1_str);
                break;
            case 1100:
                //账号或密码错误
                ToastUtil.showShort(R.string.code_1100_str);
                break;
            case 1102:
                //验证码错误
                ToastUtil.showShort(R.string.code_1102_str);
                break;
            case 1103:
                //用户已被禁用
                ToastUtil.showShort(R.string.code_1103_str);
                break;
            case 1104:
                //用户名已存在
                ToastUtil.showShort(R.string.code_1104_str);
                break;
            case 1105:
                //角色名已存在
                ToastUtil.showShort(R.string.code_1105_str);
                break;
            case 1701:
                //银行卡号不属于你
                ToastUtil.showShort(R.string.code_1701_str);
                break;
            case 1201:
                //申请贷款的用户未通过认证
                ToastUtil.showShort(R.string.code_1201_str);
                break;
            case 1202:
                //审批状态为空
                ToastUtil.showShort(R.string.code_1202_str);
                break;
            case 1203:
                //银行帐号不匹配
                ToastUtil.showShort(R.string.code_1203_str);
                break;
            case 1204:
                //客户姓名不匹配
                ToastUtil.showShort(R.string.code_1204_str);
                break;
            case 1205:
                //金融方案错误
                ToastUtil.showShort(R.string.code_1205_str);
                break;
            case 1206:
                //已有借款申请,请勿重复提交
                ToastUtil.showShort(R.string.code_1206_str);
                break;
            case 1207:
                //短期内请勿重复提交
                ToastUtil.showShort(R.string.code_1207_str);
                break;
            case 1211:
                //没有借款申请单
                ToastUtil.showShort(R.string.code_1211_str);
                break;
            case 1212:
                //还款期数错误
                ToastUtil.showShort(R.string.code_1212_str);
                break;
            case 1241:
                //没有还款订单
                ToastUtil.showShort(R.string.code_1241_str);
                break;
            case 1901:
                //订单不存在
                ToastUtil.showShort(R.string.code_1901_str);
                break;
            case 1902:
                //已放款，请勿重复提交
                ToastUtil.showShort(R.string.code_1902_str);
                break;
            case 1903:
                //已有正在进行中的订单,请勿重复提交
                ToastUtil.showShort(R.string.code_1903_str);
                break;
            case 1410:
                //查詢用戶銀行賬戶信息失敗
                ToastUtil.showShort(R.string.code_1410_str);
                break;
            case 1411:
                //Xendit创建虚拟账户失败
                ToastUtil.showShort(R.string.code_1411_str);
                break;
            case 1412:
                //Xendit支付失败
                ToastUtil.showShort(R.string.code_1412_str);
                break;
            case 1413:
                //Xendit回调订单id出错
                ToastUtil.showShort(R.string.code_1413_str);
                break;
            case 1414:
                //Xendit回调虚拟账户还款出错
                ToastUtil.showShort(R.string.code_1414_str);
                break;
            case 1415:
                //BluePay创建虚拟账户失败
                ToastUtil.showShort(R.string.code_1415_str);
                break;
            case 1421:
                //BluePay还款回调MD5校验失败
                ToastUtil.showShort(R.string.code_1421_str);
                break;
            case 1422:
                //BluePay回调订单id出错
                ToastUtil.showShort(R.string.code_1422_str);
                break;
            case 1423:
                //BluePay回调订单已被记录
                ToastUtil.showShort(R.string.code_1423_str);
                break;
            case 1450:
                //只支持"还款"类型
                ToastUtil.showShort(R.string.code_1450_str);
                break;
            case 1451:
                //只支持"加息"类型
                ToastUtil.showShort(R.string.code_1451_str);
                break;
            case 1010:
                //用户JWT签名错误，请检查JWT签名！
                ToastUtil.showShort(R.string.code_1010_str);
                break;
            case 1012:
                //手机号已经被注册
                ToastUtil.showShort(R.string.code_1012_str);
                break;
            case 1013:
                //用户未注册
                ToastUtil.showShort(R.string.code_1013_str);
                break;
            case 1014:
                //验证码错误
                ToastUtil.showShort(R.string.code_1014_str);
                break;
            case 1015:
                //密码错误
                ToastUtil.showShort(R.string.code_1015_str);
                break;
            case 1016:
                //你的账户被限制登录，请联系管理员
                ToastUtil.showShort(R.string.code_1016_str);
                break;
            case 1017:
                //图片验证码错误
                ToastUtil.showShort(R.string.code_1017_str);
                break;
            case 1018:
                //短信发送失败
                ToastUtil.showShort(R.string.code_1018_str);
                break;
            case 1210:
                //审核失败,请30天后再次申请
                ToastUtil.showShort(R.string.code_1210_str);
                break;
            default:
                ToastUtil.showShort(R.string.code_other_str);
                break;
        }
    }
}
