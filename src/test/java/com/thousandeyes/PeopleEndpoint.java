package com.thousandeyes;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.JVM)
public class PeopleEndpoint {	
	@Autowired
	private TestRestTemplate api;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void people_shouldRequireAuthentication() {
		ResponseEntity<Object> e = api.getForEntity("/people/1", Object.class);
		assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
	}
	
	@Test
	public void people_shouldGetFollowers() {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) api.withBasicAuth("1", "thousandeyes").getForObject("/people/1/followers", List.class);
		assertEquals(6, result.size());
		assertEquals(10, result.get(0).get("id"));
	}
	
	@Test
	public void people_shouldCreateFollower() {
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) api.withBasicAuth("1", "thousandeyes").postForObject("/people/9/follow", null, Map.class);
		assertEquals("ok", result.get("status"));
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> newFollowers = (List<Map<String, Object>>) api.withBasicAuth("1", "thousandeyes").getForObject("/people/1/followers", List.class);
		assertEquals(6, newFollowers.size());
		assertEquals(9, newFollowers.get(newFollowers.size() - 1).get("id"));
	}

}
