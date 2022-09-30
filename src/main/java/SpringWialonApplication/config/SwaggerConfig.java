package SpringWialonApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    /**
     * Создать приложение API
     * apiInfo () добавляет информацию, связанную с API
     * Верните экземпляр ApiSelectorBuilder с помощью функции select (), чтобы контролировать, какие интерфейсы отображаются в Swagger для отображения,
     * В этом примере для определения каталога, в котором будет создан API, используется указанный путь к отсканированному пакету.
     *
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis (RequestHandlerSelectors.basePackage ("SpringWialonApplication.restController")) // Пакет сканирования Swagger
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * Создайте основную информацию API (основная информация будет отображаться на странице документа)
     * Адрес для посещения: http: // фактический адрес проекта / swagger-ui.html
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //Заголовок страницы
                .title ("Описание запросов RESTful API")
                //Описание
                .description ("Простой Restful")
                //номер версии
                .version("1.0")
                .build();
    }
}