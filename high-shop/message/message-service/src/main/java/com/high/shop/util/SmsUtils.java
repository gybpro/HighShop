package com.high.shop.util;

import com.high.shop.base.BaseController;
import com.high.shop.constant.CommonConstant;
import com.high.shop.domain.SmsLog;
import com.high.shop.properties.SmsProperties;
import com.high.shop.service.SmsLogService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 短信验证码相关控制类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Component
public class SmsUtils {
    private final SmsProperties smsProperties;

    private final SmsLogService smsLogService;

    public SmsUtils(SmsProperties smsProperties, SmsLogService smsLogService) {
        this.smsProperties = smsProperties;
        this.smsLogService = smsLogService;
    }

    public Boolean sendCode(String code, String phone) {
        //获取用户id
        String userId = BaseController.getAuthenticationUserId();

        // 短信日志记录标志
        boolean flag;

        /* 阿里短信验证码测试
        Config config = new Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(smsProperties.getAccessKeyId())
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(smsProperties.getAccessKeySecret());
        // 访问的域名
        config.endpoint = smsProperties.getEndpoint();
        // 初始化 Client，采用 AK&SK 鉴权访问的方式，此方式可能会存在泄漏风险，建议使用 STS 方式。更多鉴权访问方式请参考：https://help.aliyun.com/document_detail/378657.html
        Client client = null;
        String responseCode = null;
        try {
            client = new Client(config);
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setSignName(smsProperties.getSignName())
                    .setTemplateCode(smsProperties.getTemplateCode())
                    .setPhoneNumbers(phone)
                    // String.format: 参数1：需要格式化的字符串，参数2：参数1中的第1个%s
                    .setTemplateParam(String.format(smsProperties.getTemplateParam(), code));
            RuntimeOptions runtime = new RuntimeOptions();
            SendSmsResponse resp = client.sendSmsWithOptions(sendSmsRequest, runtime);

            responseCode = resp.getBody().getCode();

            if (CommonConstant.OK_CODE.equals(responseCode)) {
                flag = saveSmsLog(userId, phone, code, responseCode, 2, 1);
            } else {
                flag = saveSmsLog(userId, phone, code, CommonConstant.ERROR_CODE, 2, 0);
            }

            String result = Common.toJSONString(resp.getBody());
            System.out.println(result);
        } catch (Exception e) {
            flag = saveSmsLog(userId, phone, code, CommonConstant.ERROR_CODE, 2, 0);
            e.printStackTrace();
        } */

        flag = saveSmsLog(userId, phone, code, CommonConstant.OK_CODE, 2, 1);

        if (!flag) {
            throw new RuntimeException("短信日志记录失败");
        }

        /* 阿里短信，系统环境变量读取AK
        java.util.List<String> args = java.util.Arrays.asList(args);
        // 请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID 和 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例使用环境变量获取 AccessKey 的方式进行调用，仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        com.aliyun.dysmsapi20170525.Client client = Sample.createClient(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"), System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"));
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setPhoneNumbers("your_value")
                .setSignName("your_value");
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.sendSmsWithOptions(sendSmsRequest, new com.aliyun.teautil.models.RuntimeOptions());
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } */

        // return CommonConstant.OK_CODE.equals(responseCode);
        return true;
    }

    private boolean saveSmsLog(String userId, String phone, String code,
                               String responseCode, Integer type, Integer status) {
        return smsLogService.save(
                SmsLog.builder()
                        .userId(userId)
                        .userPhone(phone)
                        .mobileCode(code)
                        .responseCode(responseCode)
                        .type(type)
                        .status(status)
                        .build()
        );
    }
}
