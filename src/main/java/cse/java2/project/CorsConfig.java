package cse.java2.project;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOrigins("http://localhost:9092") // 允许访问的前端应用的地址
        .allowedMethods("GET") // 允许的请求方法
        .allowCredentials(true) // 允许发送身份凭证（如Cookie）
        .maxAge(3600); // 预检请求的有效期
  }
}
