package oshi.framework.remote;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RemoteObjectUtil {
    public static RestTemplate getSimpleRestTemplate() {
        return getSimpleRestTemplate(1500, 15 * 1000);
    }

    public static RestTemplate getSimpleRestTemplate(int connectTimeOut, int readTimeOut) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeOut);
        requestFactory.setReadTimeout(readTimeOut);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        // 排除XML转换器(项目包含jackson-dataformat-xml包就会自动添加xml转换器，并且优先级高于json)
        restTemplate.getMessageConverters().removeIf(e -> e instanceof MappingJackson2XmlHttpMessageConverter);
        return restTemplate;
    }
}
