package com.thousandeyes.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tweet {
	private long id;
	private long person_id;
	private String content;
	
	@JsonCreator
	public Tweet(@JsonProperty("id") long id, @JsonProperty("person_id") long person_id, @JsonProperty("content") String content) {
		super();
		this.id = id;
		this.person_id = person_id;
		this.content = content;
	}

	public final long getId() {
		return id;
	}

	public final long getPerson_id() {
		return person_id;
	}

	public final String getContent() {
		return content;
	}
}
