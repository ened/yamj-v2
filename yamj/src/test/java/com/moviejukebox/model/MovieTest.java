/*
 *      Copyright (c) 2004-2015 YAMJ Members
 *      https://github.com/orgs/YAMJ/people
 *
 *      This file is part of the Yet Another Movie Jukebox (YAMJ) project.
 *
 *      YAMJ is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      YAMJ is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with YAMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 *      Web: https://github.com/YAMJ/yamj-v2
 *
 */
package com.moviejukebox.model;

import com.moviejukebox.plugin.ImdbPlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

public class MovieTest {

    private static final int ACTOR_MAX = 10;
    private static final int DIRECTOR_MAX = 2;
    private static final int WRITER_MAX = 3;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSetPeopleCast() {
        List<String> actors = createList("Actor", ACTOR_MAX);

        Movie movie = new Movie();
        movie.setPeopleCast(actors, ImdbPlugin.IMDB_PLUGIN_ID);
        Collection<String> people = movie.getPerson(Filmography.DEPT_ACTORS);
        assertEquals("Wrong number of actors returned", ACTOR_MAX, people.size());
    }

    @Test
    public void testSetPeopleDirectors() {
        List<String> directors = createList("Director", DIRECTOR_MAX);

        Movie movie = new Movie();
        movie.setPeopleDirectors(directors, ImdbPlugin.IMDB_PLUGIN_ID);
        Collection<String> people = movie.getPerson(Filmography.DEPT_DIRECTING);
        assertEquals("Wrong number of directors returned", DIRECTOR_MAX, people.size());
    }

    @Test
    public void testSetPeopleWriters() {
        List<String> writers = createList("Writer", WRITER_MAX);

        Movie movie = new Movie();
        movie.setPeopleWriters(writers, ImdbPlugin.IMDB_PLUGIN_ID);
        Collection<String> people = movie.getPerson(Filmography.DEPT_WRITING);
        assertEquals("Wrong number of writers returned", WRITER_MAX, people.size());
    }

    private List<String> createList(String title, int count) {
        List<String> testList = new ArrayList<>(count);

        for (int i = 1; i <= count + 2; i++) {
            testList.add(String.format("%s %d", title, i));
        }

        return testList;
    }
}
