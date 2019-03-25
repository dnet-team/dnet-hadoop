package eu.dnetlib.data.mdstore.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableCaching
public class MainApplication {

	private static final Logger log = LoggerFactory.getLogger(MainApplication.class);

	public static void main(final String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

	@Bean
	public static Docket newSwaggerDocket() {
		log.info("Initializing SWAGGER...");

		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(p -> p.startsWith("/mdstores"))
				.build().apiInfo(new ApiInfoBuilder()
						.title("MDStore Manager APIs")
						.description("APIs documentation")
						.version("1.1")
						.contact(ApiInfo.DEFAULT_CONTACT)
						.license("Apache 2.0")
						.licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
						.build());

	}

}
