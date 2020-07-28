package com.voverc.provisioning.utill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voverc.provisioning.controller.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ExceptionProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionProcessor.class);

	@ExceptionHandler({ Exception.class, IOException.class, JsonProcessingException.class })
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		LOGGER.debug("Error occurred {}", e.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}