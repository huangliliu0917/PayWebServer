package com.work.payweb.service.combination.ali.impl;

import com.work.general.constants.StringConstans;
import com.work.general.dicts.Dict;
import com.work.general.parameters.InputParam;
import com.work.general.parameters.OutputParam;
import com.work.general.pub.PubClz;
import com.work.general.service.idworker.SnowflakeIdWorker;
import com.work.general.util.DateUtil;
import com.work.general.util.StringUtil;
import com.work.general.util.TransUtil;
import com.work.generaldb.constants.DbConstants;
import com.work.generaldb.model.TblMerchant;
import com.work.generaldb.model.TblOrder;
import com.work.generaldb.service.MerchantService;
import com.work.generaldb.service.OrderService;
import com.work.generaldb.service.SeqService;
import com.work.payweb.service.combination.ali.AliService;
import com.work.payweb.service.micro.ali.AliMicroService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Service
public class AliServiceImpl extends PubClz implements AliService{

    @Autowired
    OrderService orderService;
    @Autowired
    MerchantService merchantService;
    @Autowired
    SeqService seqService;
    @Autowired
    AliMicroService aliMicroService;



    @Override
    public String microPay(String params) {
        return null;
    }

    @Override
    public String prePay(Map<String, String> map) {
        logger.info("支付宝扫码请求报文:"+map.toString());

        String merId = map.get(Dict.merId);
        String orderAmount = map.get(Dict.orderAmount);
        String subject = map.get(Dict.subject);
        String body = map.get(Dict.body);

        TblMerchant tblMerchant = merchantService.queryMerchant(merId);
        String subMerId = tblMerchant.getAliSubMerId();
        //订单入库
        String txnSeqId = seqService.getOrderSeq();
        TblOrder tblOrder = new TblOrder();
        tblOrder.setTxnSeqId(txnSeqId);
        tblOrder.setTxnTime(DateUtil.getDateStr(DateUtil.YYYYMMDDHHMMSS));
        tblOrder.setOrderAmount(orderAmount);
        tblOrder.setOutNumber(map.get(Dict.outTradeNo));
        tblOrder.setPayChannel("ALI");
        tblOrder.setMerId(merId);
        tblOrder.setSubMerId(subMerId);
        tblOrder.setStatus(StringConstans.ORDER_STATUS.STATUS_01);
        tblOrder.setMsg("订单初始化");
        orderService.insertOrder(tblOrder);

        //去支付宝生成二维码
        InputParam inputParam = new InputParam();
        inputParam.putParam(Dict.txnSeqId,txnSeqId);
        inputParam.putParam(Dict.subMerId,subMerId);
        inputParam.putParam(Dict.orderAmount,orderAmount);
        inputParam.putParam(Dict.subject,subject);
        inputParam.putParam(Dict.body,body);
        logger.info("请求支付宝报文："+inputParam.toString());
        OutputParam outputParam = aliMicroService.prePay(inputParam);
        logger.info(outputParam.toString());

        return outputParam.toString();
    }

    @Override
    public String createMer(Map<String, String> map) {
        logger.info("商户新增请求报文:"+map.toString());
        String merId = seqService.getMerSeq();

        map.put(Dict.merId, merId);
        InputParam inputParam = new InputParam();
        inputParam.setParams(map);
        OutputParam outputParam = aliMicroService.createMer(inputParam);
        logger.info(outputParam.toString());
        String subMerId = outputParam.getParam(Dict.subMerId);

        if (StringUtil.isEmpty(subMerId)) {
            return "支付宝模块未返回有效参数";
        }

        //商户入库
        TblMerchant tblMerchant = new TblMerchant();
        tblMerchant.setMerId(merId);
        tblMerchant.setName(map.get(Dict.name));
        tblMerchant.setAliasName(map.get(Dict.aliasName));
        tblMerchant.setContactName(map.get(Dict.contactName));
        tblMerchant.setServicePhone(map.get(Dict.servicePhone));
        tblMerchant.setAddress(map.get(Dict.address));
        tblMerchant.setMcc(map.get(Dict.mcc));
        tblMerchant.setAliSubMerId(subMerId);
        tblMerchant.setCreateTime(DateUtil.getDateStr(DateUtil.YYYYMMDDHHMMSS));
        merchantService.insertMerchant(tblMerchant);

        return outputParam.getParam(Dict.respContent);
    }

    @Override
    public String queryMer(Map<String, String> map) {
        String merId = map.get(Dict.merId);
        TblMerchant tblMerchant = merchantService.queryMerchant(merId);
        return tblMerchant.toString();


//        String merId = map.get(Dict.merId);
//        TblMerchant t = new TblMerchant();
//        t.setMerId(merId);
//        TblMerchant tblMerchant = merchantService.queryMerchant(t);
//        return tblMerchant.toString();
    }

    @Override
    public boolean deleteMer(Map<String, String> map) {
        String merId = map.get(Dict.merId);
        return merchantService.deleteMerchant(merId);
    }

    @Override
    public boolean updateMer(Map<String, String> map) {
        TblMerchant tblMerchant = null;
        try {
            tblMerchant = (TblMerchant)TransUtil.mapToObject(map, TblMerchant.class);
            TblMerchant tblMerchantUpd = merchantService.updateMerchant(tblMerchant);
            if (tblMerchantUpd != null) {
                logger.info("更新成功:"+tblMerchantUpd.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Transactional
    @Override
    public String orderQuery(Map<String, String> map) {
        logger.info("支付宝订单查询请求报文:"+map.toString());

        String txnSeqId = map.get(Dict.txnSeqId);
        TblOrder tblOrder = orderService.queryOrder(txnSeqId);
        if (null == tblOrder) {
            return "订单不存在";
        }

        String status = tblOrder.getStatus();
        if (StringConstans.ORDER_STATUS.STATUS_02.equals(status)
                || StringConstans.ORDER_STATUS.STATUS_03.equals(status)) {
            return TransUtil.objectToMap(tblOrder).toString();
        }

        //去支付宝查询订单信息
        InputParam inputParam = new InputParam();
        inputParam.putParam(Dict.txnSeqId, txnSeqId);
        OutputParam outputParam = aliMicroService.orderQuery(inputParam);
        String respContent = outputParam.getParam(Dict.respContent);

        JSONObject jsonObject = JSONObject.fromObject(respContent);
        String code = StringUtil.toString(jsonObject.get(Dict.code));
        String msg = StringUtil.toString(jsonObject.get(Dict.msg));
        String tradeNo = StringUtil.toString(jsonObject.get(Dict.trade_no));
        String tradeStatus = StringUtil.toString(jsonObject.get(Dict.trade_status));

        //更新订单
        TblOrder tblOrderUpd = new TblOrder();
        tblOrderUpd.setTxnSeqId(txnSeqId);
        if("10000".equals(code) && msg.equals("Success")){
//            tblOrderUpd.setOthChannelNumber(tradeNo);
            tblOrderUpd.setStatus(StringConstans.ORDER_STATUS.STATUS_02);
            tblOrderUpd.setMsg(tradeStatus);
            boolean result = orderService.updateOrder(tblOrderUpd);
            logger.info("更新订单结果:"+result);
        } else {
            tblOrderUpd.setStatus(StringConstans.ORDER_STATUS.STATUS_03);
            tblOrderUpd.setMsg("交易失败");
        }
        return respContent;
    }
}
