package com.Utils;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName PageConfig
 * @Description TODO
 * @Author 凌雄
 * @Date 2019/4/18 14:34
 * @Version 1.0
 **/
@Configuration
@EnableTransactionManagement
@MapperScan("com.baomidou.lx.mapper")
public class PageConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        System.out.println("进入分页 插件");
        return new PaginationInterceptor();
    }

}
