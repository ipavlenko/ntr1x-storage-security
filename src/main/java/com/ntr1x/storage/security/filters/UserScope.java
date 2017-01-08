package com.ntr1x.storage.security.filters;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UserScope implements IUserScope, Serializable {
	
	private static final long serialVersionUID = -75045510472354672L;
	
	@Getter
	private long id;
}
