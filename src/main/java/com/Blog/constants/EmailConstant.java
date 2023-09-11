package com.Blog.constants;


public interface EmailConstant {

    String EMAIL_CODE = "code";

    String EMAIL_NOTICE = "notice";

    String EMAIL_CODE_CONTEXT = "<p>\n" +
            "    <h1>博客系统验证</h1>\n" +
            "    <b>请在5分钟内，使用验证码</b>\n" +
            "    <br>验证码： %s \n" +
            "</p>";

    String EMAIL_NOTICE_CONTEXT = "<p>\n" +
            "    <h1>传奇网游</h1>\n" +
            "    一刀999+，是兄弟就来砍我\n" +
            "    <br>大哥都在玩的游戏\n" +
            "    <br>期待你的加入，就差你<br>\n" +
            "    <img src=\"https://hbimg.huabanimg.com/cc3934890743fa7ea163dd17e2e9e8af0de81f2a5f6d3-EIlVMH_fw658\">\n" +
            "</p>";

}
