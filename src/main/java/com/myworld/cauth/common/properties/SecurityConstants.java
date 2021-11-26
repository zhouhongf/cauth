package com.myworld.cauth.common.properties;

public interface SecurityConstants {
    // String HOST_GATEWAY = "http://122.114.50.172:9005";
    // String HOST_AUTH = "http://122.114.50.172:9999";
    String HOST_AUTH = "http://localhost:9999";
    // String HOST_WEBCOMMON = "http://122.114.96.124:7070";
    String HOST_WEALTH = "http://localhost:7070";
    // String HOST_KAFKA = "http://122.114.96.124:8080";
    String HOST_CHAT = "http://localhost:8080";


    /**
     * 正式上线后结合angular得重新调整一下
     */
    // String DEFAULT_LOGIN_FULL_PAGE = "http://www.jingrongbank.com/#/auth/login";
    String DEFAULT_LOGIN_FULL_PAGE = "http://localhost:9005/#/auth/login";

    // 在auth模块的loginSuccessHandler文件中引用
    String SERVICE_AUTH = "cauth";
    String SERVICE_WEALTH = "swealth";
    String SERVICE_CHAT = "schat";
    String SERVICE_SSTUDENT = "sstudent";


    String ROLE_ADMIN = "ADMIN";
    String ROLE_USER = "USER";
    String ROLE_BORROWER = "BORROWER";
    String ROLE_LENDER = "LENDER";

    /**
     * UserInfoReal即真实用户idDetail的前缀
     */
    String USER_ID_DETAIL_PREFIX = "MYUSER";
    String ADMIN_ID_DETAIL_PREFIX = "MYADMN";
    String ADMIN_WID_PREFIX = "WADMN";



    /**
     * 默认的处理验证码的url前缀
     */
    String DEFAULT_VALIDATE_CODE_URL_PREFIX = "/validatecode";
    /**
     * 用户名密码登录请求处理url
     */
    String DEFAULT_LOGIN_URL_FORM = "/doLogin";
    /**
     * 手机验证码登录请求处理url
     */
    String DEFAULT_LOGIN_URL_MOBILE = "/doSmsLogin";
    /**
     * 登出请求处理url
     */
    String DEFAULT_LOGOUT_URL = "/doLogout";
    /**
     * 注册请求处理url
     */
    String DEFAULT_REGISTER_URL = "/doRegister";
    /**
     * 登录页面
     */
    String DEFAULT_LOGIN_PAGE = "/login";
    /**
     * 登出页面
     */
    String DEFAULT_LOGOUT_PAGE = "/";
    /**
     * 管理员登录页面
     */
    String DEFAULT_LOGIN_ADMIN_PAGE = "/adminlogin";






    /**
     * 网站默认页面
     */
    String DEFAULT_UNAUTHENTICATION_URL = "/introduction";

    /**
     * token请求前缀
     */
    String DEFAULT_JWT_TOKEN_PREFIX_URL = "/myToken/";
    /**
     * jwtToken的两种类型
     */
    String DEFAULT_JWT_ACCESS_TOKEN = "access-token";
    String DEFAULT_JWT_REFRESH_TOKEN = "refresh-token";

    /**
     * 验证图片验证码时，http请求中默认的携带图片验证码信息的参数的名称
     */
    String DEFAULT_PARAMETER_NAME_CODE_IMAGE = "imageCode";

    /**
     * 验证短信验证码时，http请求中默认的携带短信验证码信息的参数的名称
     */
    String DEFAULT_PARAMETER_NAME_CODE_SMS = "smsCode";
    /**
     * 发送短信验证码 或 验证短信验证码时，传递手机号的参数的名称
     */
    String DEFAULT_PARAMETER_NAME_MOBILE = "mobile";



    /**
     * redis缓存验证码的keyhead
     */
    String VALIDATE_CODE = ":VALIDATE:";
    /**
     *  redis缓存jwt黑名单
     */
    String JWT_BLACKLIST = "MYJWT:BLACKLIST:";

    /**
     * 手机APP officialName
     */
    String APP_OFFICIAL_NAME = "JINGRONGAPK";


    String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36";
}
