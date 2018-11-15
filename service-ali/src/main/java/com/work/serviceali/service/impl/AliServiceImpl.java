package com.work.serviceali.service.impl;

import com.work.general.dicts.Dict;
import com.work.general.parameters.InputParam;
import com.work.general.parameters.OutputParam;
import com.work.general.pub.PubClz;
import com.work.general.util.StringUtil;
import com.work.serviceali.service.AliService;
import com.work.serviceali.service.YLAliPayService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AliServiceImpl extends PubClz implements AliService {

    @Autowired
    YLAliPayService ylAliPayService;

    @Value("${asdk.tradePay}")
    String asdk_tradePay;
    @Value("${asdk.tradePrecreate}")
    String asdk_tradePrecreate;
    @Value("${asdk.indirectCreate}")
    String asdk_indirectCreate;
    @Value("${asdk.merid}")
    String asdk_merid;

    @Override
    public OutputParam microPay(InputParam input) {
        OutputParam outputParam = new OutputParam();
//        try {
//            String outTradeNo = input.getParamsStr(Dict.outTradeNo);
//            String authCode = input.getParamsStr(Dict.authCode);
//            String orderAmount = input.getParamsStr(Dict.orderAmount);
//            String subject = input.getParamsStr(Dict.subject);
//            String body = input.getParamsStr(Dict.body);
//            String subMerId = input.getParamsStr(Dict.subMerId);
//
//            HashMap<String, Object> data = new HashMap<String, Object>();
//            // 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
//            data.put("out_trade_no", outTradeNo);
//            data.put("scene", "bar_code"); // 支付场景 条码支付，取值：bar_code  声波支付，取值：wave_code
//            data.put("auth_code", authCode); // 支付授权码，25~30开头的长度为16~24位的数字，实际字符串长度以开发者获取的付款码长度为准
//            data.put("subject", subject); // 订单标题
//            Map<String, Object> SubMerchant = new HashMap<String, Object>();
//            SubMerchant.put("merchant_id", subMerId);
//            SubMerchant.put("merchant_type", "alipay");
//            data.put("sub_merchant", SubMerchant);
//            data.put("total_amount", orderAmount);
//            data.put("trans_currency", "CNY");
//            data.put("settle_currency", "CNY");
//            if (!StringUtil.isEmpty(body)) {
//                data.put("body", body); // 订单描述
//            }
//            data.put("timeout_express", "5m");
//
//            Map<String, String> needData = new HashMap<String, String>();
//            needData.put(Dict.interfaceName, asdk_tradePay);
//
//            String returnData = ylAliPayService.aliSdk(data, needData); //  条码支付
//
//        } catch (Exception e) {

//        } finally {
//
//        }

        return outputParam;

    }

    @Override
    public OutputParam prePay(InputParam inputParam) {
        OutputParam outputParam = new OutputParam();
        try {
            String outTradeNo = inputParam.getParam(Dict.outTradeNo);
            String orderAmount = inputParam.getParam(Dict.orderAmount);
            String subject = inputParam.getParam(Dict.subject);
            String subMchId = inputParam.getParam(Dict.subMchId);
            String body = inputParam.getParam(Dict.body);

            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("out_trade_no", outTradeNo);
            data.put("total_amount", orderAmount);
            data.put("subject", subject); // 订单标题
            Map<String, Object> SubMerchant = new HashMap<String, Object>();
            SubMerchant.put("merchant_id", subMchId);
            SubMerchant.put("merchant_type", "alipay");
            data.put("sub_merchant", SubMerchant);
            data.put("body", body); // 对交易或商品的描述
            data.put("timeout_express", "5m");

            Map<String, String> needData = new HashMap<String, String>();
            needData.put(Dict.interfaceName, asdk_tradePrecreate);

            String returnMsg = ylAliPayService.aliSdk(data, needData); // 扫码支付
            JSONObject jsonObject = JSONObject.fromObject(returnMsg);
            outputParam.putParam(Dict.respContent,returnMsg);
            outputParam.putParam(Dict.subMerId,jsonObject.getString("sub_merchant_id"));
            outputParam.setRetMsg("成功");
            outputParam.setRetCode("success");
        } catch (Exception e) {

        } finally {

        }
        return outputParam;

    }

    @Override
    public OutputParam createMer(InputParam inputParam) {
        OutputParam outputParam = new OutputParam();
        try {
            String merId = inputParam.getParam(Dict.merId);
            String name = inputParam.getParam(Dict.name);
            String aliasName = inputParam.getParam(Dict.aliasName);
            String servicePhone = inputParam.getParam(Dict.servicePhone);
            String categoryId = inputParam.getParam(Dict.categoryId);
            String orgPid = inputParam.getParam(Dict.orgPid);
            String mcc = inputParam.getParam(Dict.mcc);
            String contactName = inputParam.getParam(Dict.contactName);
            String address = inputParam.getParam(Dict.address);

            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("external_id", merId); // 商户编号，由机构定义，需要保证在机构下唯一
            data.put("name", name); // 商户名称
            data.put("alias_name", aliasName); // 商户简称
            data.put("service_phone", servicePhone); // 商户客服电话
            data.put("category_id", categoryId); // 商户经营类目
            data.put("source", asdk_merid); // 商户来源机构标识，填写机构在支付宝的pid
            data.put("org_pid", orgPid);
            data.put("mcc", mcc);
            // 商户联系人信息
            List<Object> contactInfo = new LinkedList<Object>();
            Map<String, Object> infos = new LinkedHashMap<String, Object>();
            infos.put("name", contactName); // 联系人姓名
            /**
             * 商户联系人业务标识枚举，表示商户联系人的职责。 异议处理接口人:02;商户关键联系人:06;数据反馈接口人:11;服务联动接口人:08
             */
            String[] tag = {"06"};
            infos.put("tag", JSONArray.fromObject(tag));
            infos.put("type", "LEGAL_PERSON"); // 联系人类型，取值范围：LEGAL_PERSON：法人；CONTROLLER：实际控制人；AGENT：代理人；OTHER：其他
//        // 非必填
//        infos.put("phone", contactPhone); // 电话
//        infos.put("mobile", contactMobile); // 手机
//        infos.put("email", contactEmail); // 电子邮箱
//        infos.put("id_card_no", idCardNo); // 身份证号
            contactInfo.add(infos);
            data.put("contact_info", JSONArray.fromObject(contactInfo));
            List<Object> addressInfo = new LinkedList<Object>();
            Map<String, Object> addinfos = new LinkedHashMap<String, Object>();
            addinfos.put("city_code", "371000"); // 城市编码，城市编码是与国家统计局一致
            addinfos.put("district_code", "371002"); // 区县编码，区县编码是与国家统计局一致，
            addinfos.put("province_code", "370000"); // 省份编码，省份编码是与国家统计局一致
            addinfos.put("address", address); // 地址。商户详细经营地址或人员所在地点
//        addinfos.put("longitude", longitude); // 经度，浮点型, 小数点后最多保留6位。
//        addinfos.put("latitude", latitude); // 纬度，浮点型,小数点后最多保留6位如需要录入经纬度，请以高德坐标系为准，
//        addinfos.put("type", addressType); // 地址类型。取值范围：BUSINESS_ADDRESS：经营地址（默认）
            addressInfo.add(addinfos);
            data.put("address_info", JSONArray.fromObject(addressInfo));
            // data.put("business_license", "100000011234561"); //
            // 商户证件编号（企业或者个体工商户提供营业执照，事业单位提供事证号）
            /**
             * 商户证件类型，取值范围：NATIONAL_LEGAL：营业执照；
             * NATIONAL_LEGAL_MERGE:营业执照(多证合一)；INST_RGST_CTF：事业单位法人证书
             */
            // data.put("business_license_type", "NATIONAL_LEGAL");
            // 商户对应银行所开立的结算卡信息
//        if (!StringUtil.isEmptyMultipleStr(cardNo, cardName)) {
//            List<Object> bankcardInfo = new LinkedList<Object>();
//            Map<String, Object> bankinfos = new LinkedHashMap<String, Object>();
//            bankinfos.put("card_no", cardNo); // 银行卡号
//            bankinfos.put("card_name", cardName); // 银行卡持卡人姓名
//            bankcardInfo.add(bankinfos);
//            data.put("bankcard_info", JSONArray.fromObject(bankcardInfo));
//        }
            data.put("memo", merId); // 商户备注信息，可填写额外信息

            Map<String, String> needData = new HashMap<String, String>();
            needData.put(Dict.interfaceName, asdk_indirectCreate);

            String returnMsg = ylAliPayService.aliSdk(data, needData); // 商户入驻
            outputParam.putParam(Dict.respContent, returnMsg);
            outputParam.setRetMsg("成功");
            outputParam.setRetCode("success");

        } catch (Exception e) {

        } finally {

        }
        return outputParam;
    }
}
