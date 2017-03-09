package com.thousandeyes.repository;

import com.thousandeyes.models.Person;
import com.thousandeyes.models.PersonWithPopularFollower;

import java.util.List;

public interface PersonRepository {
    List<Person> all();

    Person byId(long id);

    List<Person> followers(Person p);

    List<Person> following(Person p);

    boolean follow(Person user, Person target);

    boolean unfollow(Person user, Person target);

    boolean isFollowing(Person user, Person target);

    List<PersonWithPopularFollower> withPopularFollowers();
}
