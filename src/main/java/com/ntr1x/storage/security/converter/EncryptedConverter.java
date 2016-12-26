package com.ntr1x.storage.security.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.io.Charsets;

import com.ntr1x.storage.core.utils.ConversionUtils;
import com.ntr1x.storage.security.services.ISecurityService;

@Converter(autoApply = false)
public class EncryptedConverter implements AttributeConverter<String, String> {
	
	private static ISecurityService security;
	
	public static void setSecurityService(ISecurityService security) {
		EncryptedConverter.security = security;
	}
	
	@Override
	public String convertToDatabaseColumn(String data) {
		
		if (data == null) return null;
		return ConversionUtils.BASE64.encode(security.encrypt(data.getBytes(Charsets.UTF_8)));
	}

	@Override
	public String convertToEntityAttribute(String encrypted) {
				
		if (encrypted == null) return null;
		return new String(security.decrypt(ConversionUtils.BASE64.decode(encrypted)), Charsets.UTF_8);
	}
}
