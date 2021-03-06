package com.bravson.socialalert.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.infrastructure.util.JwtUtil;

public class JwtUtilTest {
	
	@Test
	public void decodeValidAccessToken() {
		String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCVkdSRzhLbEZ3Q2VXLXdHVlVCb1VlUVg2ZlNVVEp1RG5ZUDNIekJtV2ZVIn0.eyJqdGkiOiIzMGQzOWI1NC03ZDg2LTQzY2YtYTUxZS05MGY1MmRhOWY4NzciLCJleHAiOjE1MDQ1NDk0OTgsIm5iZiI6MCwiaWF0IjoxNTA0NTQ5MTk4LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvU29jaWFsQWxlcnQtRGV2IiwiYXVkIjoic29jaWFsYWxlcnQtamVlIiwic3ViIjoiMzM5MzVkZDYtZDNiOS00MjE1LTlhZmEtOTg0ZjVmMjhjY2ZjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic29jaWFsYWxlcnQtamVlIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiOGQzOGEyZjYtMDRlZC00ZTUzLTk0ODctMWMxZDI4MGE3NWYyIiwiYWNyIjoiMSIsImNsaWVudF9zZXNzaW9uIjoiYjg4ZWRkMDctMzMyYy00Yzk4LThhY2MtZDI1MDI2MzM5MGM5IiwiYWxsb3dlZC1vcmlnaW5zIjpbIi9zb2NpYWxhbGVydC1qZWUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy11c2VycyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IiIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3RAdGVzdC5jb20iLCJlbWFpbCI6InRlc3RAdGVzdC5jb20ifQ.rG6XtBrPE8mTacgLoraY0qJVTOPvUed-cQp1FaNaJCWFpes_tVZFViC_zSCGAbS0q3qiW6D56M6Vi5iEIGJCzvJiBycBSfCy-bfAGpTNrNIjpgl8XtQUVkZArLu69qheFEbogjVI_c6bk7rsJrGZAxiBIO3TGGpzfA9mFD9uX27PWOS_Wiphw0hNj2oMhAscKw_BXP70k-LB6ZwzZp6sUJHevCAlRdrVVsys3ummVdVFqblCRY947qkNu2qqxEqeG-fnceQ_y5Y1EuP-lSh0G4k9SESR8UTmEj34s4bXXQ2ZX8FcfkZBbsLEcbPNvZqWLCR_shbtRU5y81G_4yWjpQ";
		Optional<String> result = JwtUtil.extractUserId(token);
		assertThat(result).hasValue("33935dd6-d3b9-4215-9afa-984f5f28ccfc");
	}
	
	@Test
	public void decodeInvalidAccessToken() {
		String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCVkdSRzhLbEZ3Q2VXLXdHVlVCb1VlUVg2ZlNVVEp1RG5ZUDNIekJtV2ZVIn0.fsdkiOiIzMGQzOWI1NC03ZDg2LTQzY2YtYTUxZS05MGY1MmRhOWY4NzciLCJleHAiOjE1MDQ1NDk0OTgsIm5iZiI6MCwiaWF0IjoxNTA0NTQ5MTk4LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvU29jaWFsQWxlcnQtRGV2IiwiYXVkIjoic29jaWFsYWxlcnQtamVlIiwic3ViIjoiMzM5MzVkZDYtZDNiOS00MjE1LTlhZmEtOTg0ZjVmMjhjY2ZjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic29jaWFsYWxlcnQtamVlIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiOGQzOGEyZjYtMDRlZC00ZTUzLTk0ODctMWMxZDI4MGE3NWYyIiwiYWNyIjoiMSIsImNsaWVudF9zZXNzaW9uIjoiYjg4ZWRkMDctMzMyYy00Yzk4LThhY2MtZDI1MDI2MzM5MGM5IiwiYWxsb3dlZC1vcmlnaW5zIjpbIi9zb2NpYWxhbGVydC1qZWUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy11c2VycyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IiIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3RAdGVzdC5jb20iLCJlbWFpbCI6InRlc3RAdGVzdC5jb20ifQ.rG6XtBrPE8mTacgLoraY0qJVTOPvUed-cQp1FaNaJCWFpes_tVZFViC_zSCGAbS0q3qiW6D56M6Vi5iEIGJCzvJiBycBSfCy-bfAGpTNrNIjpgl8XtQUVkZArLu69qheFEbogjVI_c6bk7rsJrGZAxiBIO3TGGpzfA9mFD9uX27PWOS_Wiphw0hNj2oMhAscKw_BXP70k-LB6ZwzZp6sUJHevCAlRdrVVsys3ummVdVFqblCRY947qkNu2qqxEqeG-fnceQ_y5Y1EuP-lSh0G4k9SESR8UTmEj34s4bXXQ2ZX8FcfkZBbsLEcbPNvZqWLCR_shbtRU5y81G_4yWjpQ";
		Optional<String> result = JwtUtil.extractUserId(token);
		assertThat(result).isEmpty();
	}
}
