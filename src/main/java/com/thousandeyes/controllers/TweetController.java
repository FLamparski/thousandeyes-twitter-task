package com.thousandeyes.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thousandeyes.models.Person;
import com.thousandeyes.models.Tweet;
import com.thousandeyes.repository.TweetRepository;

@RestController
@RequestMapping("/tweets")
public class TweetController {
	
	@Autowired
	private TweetRepository tweets;
	
	@RequestMapping("/")
	public List<Tweet> feed(@AuthenticationPrincipal final Person p, @RequestParam(name = "search", required = false) final String search) {
		if (search != null) {
			return tweets
					.feedFor(p)
					.stream()
					.filter(tweet -> tweet.getContent().contains(search))
					.collect(Collectors.toList());
		} else {
			return tweets.feedFor(p);
		}
	}
}
