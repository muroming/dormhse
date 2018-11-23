package com.dorm.muro.dormitory;

public class Constants {
    public static final String NOTIFICATIONS = "NOTIFICATIONS";
    public static final String SHARED_PREFERENCES = "APP_DORMITORY_PREFS";
    public static final String IS_LOGGED = "LOGIN_STATUS";
    public static final String USER_FIO = "USER_FIO";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String CONTRACT_ID = "CONTRACT_ID";
    public static final String MONTHLY_COST = "MONTHLY_COST";
    public static final String MONTHS_FROM = "MONTHS_FROM";
    public static final String MONTHS_TO = "MONTHS_TO";
    public static final String CARD_NUMBER = "CARD_NUMBER";
    public static final String CARDHOLDER_NAME = "CARDHOLDER_NAME";
    public static final String CARD_YEAR = "CARD_YEAR";
    public static final String CARD_MONTH = "CARD_MONTH";
    public static final String DATE_SEPARATOR = "/";
    public static final String PAYMENT_URL = "https://pay.hse.ru/moscow/prg";
    public static final String FIRST_QUERY = "(function(){\n" +
            "var check = document.getElementById('amount');\n" +
            "var alertMsg = document.getElementsByClassName('error-message')[0];\n" +
            "document.getElementById('fio').value='%s';\n" +
            "document.getElementById('order').value='%s';\n" +
            "document.getElementsByClassName('pay_button')[0].click();\n" +
            "function checkFlag() {\n" +
            "if(check.offsetParent == null) {\n" +
            "if(alertMsg.innerText != \"\"){\n" +
            "window.CallBack.handleErrorMsg();\n" +
            "}else{ \n" +
            "window.setTimeout(checkFlag, 100);}\n" +
            "} else {\n" +
            "window.CallBack.incrementStep();\n" +
            "var a=document.getElementById('desination').value='%s';\n" +
            "document.getElementById('amount').value='%s';\n" +
            "var c=document.getElementById('kop').value='%s';\n" +
            "var d=document.getElementsByClassName('pay_button')[0].click();return 'ok';}}\n" +
            "checkFlag();\n" +
            "})();";
    public static final String SKIP_CHECKOUT = "document.getElementById('button-checkout').click();";
    public static final String ENTER_CARD_AND_CONFIRM_INFO = "var a=document.getElementById(\"iPAN_sub\").value='%s';" +
            "var b=document.getElementById(\"input-month\").value=%s;" +
            "var c=document.getElementById(\"input-year\").value=%s;" +
            "var d=document.getElementById(\"iTEXT\").value='%s';";
}
