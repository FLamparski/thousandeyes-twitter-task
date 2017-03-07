package com.thousandeyes.models;

public class PersonWithPopularFollower {
    private Person person;
    private Person popularFollower;
    private int popularFollowerFollowerCount;

    public PersonWithPopularFollower(Person person, Person popularFollower, int popularFollowerFollowerCount) {
        this.person = person;
        this.popularFollower = popularFollower;
        this.popularFollowerFollowerCount = popularFollowerFollowerCount;
    }

    public Person getPerson() {
        return person;
    }

    public Person getPopularFollower() {
        return popularFollower;
    }

    public int getPopularFollowerFollowerCount() {
        return popularFollowerFollowerCount;
    }
}
