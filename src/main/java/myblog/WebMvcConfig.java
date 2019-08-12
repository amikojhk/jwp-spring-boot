package myblog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import support.version.BlogVersion;
import support.web.BasicAuthInterceptor;
import support.web.argumentresolver.LoginUserHandlerMethodArgumentResolver;

import javax.servlet.Filter;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    public static final String PREFIX_STATIC_RESOURCES = "/resources";

    @Autowired
    private BlogVersion version;

    @Autowired
    private BasicAuthInterceptor basicAuthInterceptor;

    @Autowired
    private LoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(60 * 60 * 24 * 365);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(basicAuthInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserHandlerMethodArgumentResolver);
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter etagHeaderFilter = new ShallowEtagHeaderFilter();
        registration.setFilter(etagHeaderFilter);
        registration.addUrlPatterns(PREFIX_STATIC_RESOURCES + "/*");
        return registration;
    }
}
