package com.dorm.muro.dormitory;

public class Constants {
    public static final int TODO_CREATED = 10;
    public static final int TODO_CANCELED = -10;
    public static final String TODO_SERIALIZED = "TODO_SERIALIZED";
    public static final String ROOM_KEY = "ROOM_KEY";
    public static final String NOTIFICATIONS = "NOTIFICATIONS";
    public static final String SIGNED_IN_ROOM = "SIGNED_IN_ROOM";
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
    public static final String DATE_SEPARATOR = "\\.";
    public static final String DATE_SPLITTER = "\\.";
    public static final String PAYMENT_URL = "https://pay.hse.ru/moscow/prg";
    public static final String FIRST_QUERY =
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
            "window.CallBack.nextStep();\n" +
            "var a=document.getElementById('desination').value='%s';\n" +
            "document.getElementById('amount').value='%s';\n" +
            "var c=document.getElementById('kop').value='%s';\n" +
            "var d=document.getElementsByClassName('pay_button')[0].click();return 'ok';}}\n" +
            "checkFlag();";
    public static final String SKIP_CHECKOUT = "document.getElementById('button-checkout').click();";
    public static final String ENTER_CARD_AND_CONFIRM_INFO = "var a=document.getElementById(\"iPAN_sub\").value='%s';" +
            "var b=document.getElementById(\"input-month\").value=%s;" +
            "var c=document.getElementById(\"input-year\").value=%s;" +
            "var d=document.getElementById(\"iTEXT\").value='%s';" +
            "window.CallBack.inputCVV();";
    public static final String CONFIRM_CARD = "var a=document.getElementById(\"iCVC\").value='%s';" +
            "var c=document.getElementById(\"buttonPayment\").click();" +
            "window.CallBack.inputSMSCode();";

    public static final String INPUT_SMS = "var a=document.getElementById(\"psw_id\").value='%s';" +
            "var a=document.getElementById(\"btnSubmit\").click();";

    public static final int TODO_MAX_CHARACTERS = 80;

    public static final String USER_INFO_DATABASE = "user_info";
    public static final String USER_NAME_FIELD = "name";
    public static final String USER_SURNAME_FIELD = "surname";
    public static final String USER_PATRONYMIC_FIELD = "patronymic";
    public static final String USER_CONTRACT_ID_FIELD = "contract_id";
    public static final String USER_ROOM_ID = "room_id";

    public static final String TODOS_DATABASE = "todos";
    public static final String USERS_TODOS_DATABASE = "users_todos";
    public static final String ROOM_USERS_DATABASE = "room_users";
    public static final String ROOMS_DATABASE = "rooms";
    public static final String ID_ROOM_DATABASE = "room_id";

    public static final String DUTY_START = "DUTY_START";
    public static final String DUTY_END = "DUTY_END";
    public static final String DUTY_ROOM = "DUTY_ROOM";
    public static final String DUTIES_DATABASE = "duties";
    public static final String ROOM_DUTIES_DATABASE = "room_duties";

    public static final String TARGET_FRAGMENT = "TARGET_FRAGMENT";
}
