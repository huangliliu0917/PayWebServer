package com.work.payweb.gray;

import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RibbonConfig {

    @Bean
    public IRule ribbonRule(){
        //必须关闭熔断器才能生效
        return new GrayRibbonRule();
    }

}