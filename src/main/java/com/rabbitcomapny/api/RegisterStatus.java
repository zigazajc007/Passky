package com.rabbitcomapny.api;

/**
 * Enum representing possible outcomes of a force register attempt.
 */
public enum RegisterStatus {
	SUCCESS,
	ALREADY_REGISTERED,
	PASSWORD_TOO_SHORT,
	PASSWORD_TOO_LONG,
	INVALID_UUID,
	UNKNOWN_ERROR
}