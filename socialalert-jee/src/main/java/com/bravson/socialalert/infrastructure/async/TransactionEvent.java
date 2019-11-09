package com.bravson.socialalert.infrastructure.async;

import java.io.Serializable;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor="of")
class TransactionEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private AsyncEvent sourceEvent;
}
