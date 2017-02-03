package com.ntr1x.storage.security.resources;

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Swagger;
import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.In;

@SwaggerDefinition(
	info = @Info(
		title = "Storage API",
		description = "Storage API",
		version = "1.0.0"
	),
	schemes = { SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS },
	tags = {
		@Tag(name = "Me",				description = "User data accessors"),
		@Tag(name = "Security",     	description = "Registration & Authorization"),
		@Tag(name = "Resources",    	description = "Universal resource access"),
		@Tag(name = "Uploads",     		description = "Files & Images"),
		@Tag(name = "Media",        	description = "Publications & Events"),
        @Tag(name = "Store",        	description = "Offers & Orders"),
		@Tag(name = "Archery",     		description = "Archery portals"),
	}
)
public class APIV1 implements ReaderListener {

    @Override
    public void beforeScan(Reader reader, Swagger swagger) {
    }

    @Override
    public void afterScan(Reader reader, Swagger swagger) {

        ApiKeyAuthDefinition  tokenScheme = new ApiKeyAuthDefinition();
        tokenScheme.setType("apiKey");
        tokenScheme.setName("Authorization");
        tokenScheme.setIn(In.HEADER);
        swagger.addSecurityDefinition("api_key", tokenScheme);
    }
}