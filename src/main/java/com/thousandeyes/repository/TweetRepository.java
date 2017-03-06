package com.thousandeyes.repository;

import java.util.List;

import com.thousandeyes.models.Person;
import com.thousandeyes.models.Tweet;

public interface TweetRepository {
	List<Tweet> feedFor(Person p);
}
