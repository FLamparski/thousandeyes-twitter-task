package com.thousandeyes.repository;

import java.util.List;

import com.thousandeyes.models.Person;

public interface PersonRepository {
	List<Person> all();
	
	Person byId(long id);
	
	List<Person> followers(Person p);
	
	List<Person> following(Person p);
	
	boolean follow(Person user, Person target);

	boolean unfollow(Person user, Person target);
	
	boolean isFollowing(Person user, Person target);
}
