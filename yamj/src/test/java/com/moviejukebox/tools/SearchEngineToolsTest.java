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
package com.moviejukebox.tools;

import org.apache.commons.lang3.StringUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchEngineToolsTest {

    private static final Logger LOG = LoggerFactory.getLogger(SearchEngineToolsTest.class);

    @Test
    public void roundTripIMDB() {
        LOG.info("roundTripIMDB");
        SearchEngineTools search = new SearchEngineTools();

        // movie
        for (int i = 0; i < search.countSearchSites(); i++) {
            String engine = search.getCurrentSearchEngine();
            LOG.info("Testing {}", engine);
            String url = search.searchMovieURL("Avatar", "2009", "www.imdb.com/title");
            url = StringUtils.removeEnd(url, "/");
            assertEquals("Search engine '" + engine + "' failed", "http://www.imdb.com/title/tt0499549", url);
        }

        // TV show, must leave out the year and search for TV series
        for (int i = 0; i < search.countSearchSites(); i++) {
            String engine = search.getCurrentSearchEngine();
            LOG.info("Testing {}", engine);
            String url = search.searchMovieURL("Two and a Half Men", null, "www.imdb.com/title", "TV series");
            url = StringUtils.removeEnd(url, "/");
            assertEquals("Search engine '" + engine + "' failed", "http://www.imdb.com/title/tt0369179", url);
        }
    }

    @Test
    public void roundTripOFDB() {
        LOG.info("roundTripOFDB");
        SearchEngineTools search = new SearchEngineTools("de");
        search.setSearchSites("google");

        // movie
        for (int i = 0; i < search.countSearchSites(); i++) {
            String engine = search.getCurrentSearchEngine();
            LOG.info("Testing {}", engine);
            String url = search.searchMovieURL("Avatar", "2009", "www.ofdb.de/film");
            assertEquals("Search engine '" + engine + "' failed", "http://www.ofdb.de/film/188514,Avatar---Aufbruch-nach-Pandora", url);
        }

        // TV show
        for (int i = 0; i < search.countSearchSites(); i++) {
            String engine = search.getCurrentSearchEngine();
            LOG.info("Testing {}", engine);
            String url = search.searchMovieURL("Two and a Half Men", "2003", "www.ofdb.de/film");
            assertEquals("Search engine '" + engine + "' failed", "http://www.ofdb.de/film/66192,Mein-cooler-Onkel-Charlie", url);
        }
    }

    @Test
    public void roundTripAllocine() {
        LOG.info("roundTripAllocine");
        SearchEngineTools search = new SearchEngineTools("fr");

        // movie, must set search suffix
        search.setSearchSuffix("/fichefilm_gen_cfilm");
        for (int i = 0; i < search.countSearchSites(); i++) {
            String engine = search.getCurrentSearchEngine();
            LOG.info("Testing {}", engine);
            String url = search.searchMovieURL("Avatar", "2009", "www.allocine.fr/film");
            assertEquals("Search engine '" + engine + "' failed", "http://www.allocine.fr/film/fichefilm_gen_cfilm=61282.html", url);
        }
        // TV show, must set search suffix
        search.setSearchSuffix("/ficheserie_gen_cserie");
        for (int i = 0; i < search.countSearchSites(); i++) {
            String engine = search.getCurrentSearchEngine();
            LOG.info("Testing {}", engine);
            String url = search.searchMovieURL("Two and a Half Men", "2003", "www.allocine.fr/series");
            assertTrue("Search engine '" + engine + "' failed: " + url, url.startsWith("http://www.allocine.fr/series/ficheserie_gen_cserie=132.html"));
        }
    }

    @Test
    public void roundTripFilmweb() {
        LOG.info("roundTripFilmweb");
        SearchEngineTools search = new SearchEngineTools("pl");

        // movie
        for (int i = 0; i < search.countSearchSites(); i++) {
            String engine = search.getCurrentSearchEngine();
            LOG.info("Testing {}", engine);
            String url = search.searchMovieURL("Avatar", "2009", "www.filmweb.pl");
            assertEquals("Search engine '" + engine + "' failed", "http://www.filmweb.pl/Avatar", url);
        }

        // TV show
        for (int i = 0; i < search.countSearchSites(); i++) {
            String engine = search.getCurrentSearchEngine();
            LOG.info("Testing {}", engine);
            String url = search.searchMovieURL("The 4400", null, "www.filmweb.pl/serial", "sezon 3");
            assertTrue("Search engine '" + engine + "' failed: " + url, url.startsWith("http://www.filmweb.pl/serial/4400-2004-122684"));
        }
    }

    @Test
    public void roundTripMovieSratim() {
        LOG.info("fixRoundTripMovieSratim");
        SearchEngineTools search = new SearchEngineTools("il");
        search.setSearchSuffix("/view.php");
        search.setSearchSites("google");

        for (int i = 0; i < search.countSearchSites(); i++) {
            String engine = search.getCurrentSearchEngine();
            LOG.info("Testing {}", engine);
            String url = search.searchMovieURL("Avatar", "2009", "www.sratim.co.il");
            assertTrue("Search engine '" + engine + "' failed: " + url, url.startsWith("http://www.sratim.co.il/view.php"));
            assertTrue("Search engine '" + engine + "' failed: " + url, url.contains("id=143628"));
        }
    }

    @Test
    public void roundTripMovieComingSoon() {
        LOG.info("fixRoundTripMovieComingSoon");
        SearchEngineTools search = new SearchEngineTools("it");

        for (int i = 0; i < search.countSearchSites(); i++) {
            String engine = search.getCurrentSearchEngine();
            LOG.info("Testing {}", engine);
            String url = search.searchMovieURL("Avatar", "2009", "www.comingsoon.it/film");
            assertTrue("Search engine '" + engine + "' failed:" + url, url.startsWith("http://www.comingsoon.it/film/"));
            assertTrue("Search engine '" + engine + "' failed:" + url, url.contains("846"));
        }
    }
}
