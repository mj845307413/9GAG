package me.storm.ninegag.api;

/**
 * Created by storm on 14-3-25.
 */
public class GagApi {
    //http://image.baidu.com/channel/listjson?pn=1&rn=10&tag1=%E7%BE%8E%E5%A5%B3&tag2=%E5%85%A8%E9%83%A8&ie=utf8
    //http://image.baidu.com/channel/listjson?pn=%d&rn=%d&tag1=%s&tag2=%s&ie=utf8
    private static final String HOST = "http://image.baidu.com/channel/listjson";

    public static final String LIST = HOST + "?pn=%d&rn=10&ie=utf8&tag2=";
}
