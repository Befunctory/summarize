package com.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {

    /**
     * 文件上传配置
     *
     * @return MultipartConfigElement
     */

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小
        factory.setMaxFileSize("1024000KB");
        /// 总上传数据大小
        factory.setMaxRequestSize("10240000KB");
        return factory.createMultipartConfig();
    }

}