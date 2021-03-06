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
package com.moviejukebox.plugin;

import com.moviejukebox.model.Movie;
import com.moviejukebox.model.MovieFile;
import com.moviejukebox.model.enumerations.OverrideFlag;
import com.moviejukebox.tools.OverrideTools;
import com.moviejukebox.tools.PropertiesUtil;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SratimPluginTest {

    private SratimPlugin sratimPlugin;

    @BeforeClass
    public static void configure() {
        PropertiesUtil.setPropertiesStreamName("./properties/apikeys.properties");
        PropertiesUtil.setProperty("mjb.internet.plugin", "com.moviejukebox.plugin.SratimPlugin");
    }

    @Before
    public void setup() {
        sratimPlugin = new SratimPlugin();
    }

    @Test
    public void testMovie() {
        Movie movie = new Movie();
        movie.addMovieFile(new MovieFile());
        movie.setMovieType(Movie.TYPE_MOVIE);
        movie.setTitle("The Croods", Movie.UNKNOWN);
        movie.setId(ImdbPlugin.IMDB_PLUGIN_ID, "tt0481499");

        // Force sratim to be the first priority
        OverrideTools.putMoviePriorities(OverrideFlag.DIRECTORS, "sratim,imdb");
        sratimPlugin.scan(movie);
        assertEquals("1123786", movie.getId(SratimPlugin.SRATIM_PLUGIN_ID));
        assertEquals("כריס סנדרס", movie.getDirector());
    }
}
