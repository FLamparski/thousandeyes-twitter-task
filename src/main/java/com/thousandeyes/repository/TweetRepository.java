package com.thousandeyes.repository;

import com.thousandeyes.models.Person;
import com.thousandeyes.models.Tweet;

import java.util.List;

public interface TweetRepository {
    List<Tweet> feedFor(Person p);
}
