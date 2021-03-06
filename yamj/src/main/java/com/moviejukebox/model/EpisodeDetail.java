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

import com.moviejukebox.reader.MovieNFOReader;
import com.moviejukebox.tools.OverrideTools;
import com.moviejukebox.tools.StringTools;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class to hold the episode information scraped from the XBMC style TV Episode
 * NFO file.
 *
 * @author Stuart Boston
 *
 */
public class EpisodeDetail {

    private String title = Movie.UNKNOWN;
    private int season = -1;
    private int episode = -1;
    private String plot = Movie.UNKNOWN;
    private String firstAired = Movie.UNKNOWN;
    private String airsAfterSeason = Movie.UNKNOWN;
    private String airsBeforeSeason = Movie.UNKNOWN;
    private String airsBeforeEpisode = Movie.UNKNOWN;
    private String rating = Movie.UNKNOWN;

    /**
     * Set the title of the episode
     *
     * @param title
     */
    public void setTitle(String title) {
        if (StringTools.isNotValidString(title)) {
            this.title = Movie.UNKNOWN;
        } else {
            this.title = title;
        }
    }

    /**
     * return the title of the episode
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the season of the episode
     *
     * @param season
     */
    public void setSeason(int season) {
        if (season < -1) {
            this.season = -1;
        } else {
            this.season = season;
        }
    }

    /**
     * Return the season of the episode
     *
     * @return
     */
    public int getSeason() {
        return season;
    }

    /**
     * Set the Episode number
     *
     * @param episode
     */
    public void setEpisode(int episode) {
        if (episode < 0) {
            this.episode = 0;
        } else {
            this.episode = episode;
        }
    }

    /**
     * return the episode number
     *
     * @return
     */
    public int getEpisode() {
        return episode;
    }

    /**
     * Set the plot
     *
     * @param plot
     */
    public void setPlot(String plot) {
        if (plot == null || plot.isEmpty()) {
            this.plot = Movie.UNKNOWN;
        } else {
            this.plot = plot;
        }
    }

    /**
     * Return the plot
     *
     * @return
     */
    public String getPlot() {
        return plot;
    }

    /**
     * Set the rating
     *
     * @param rating
     */
    public void setRating(String rating) {
        if (rating == null || rating.isEmpty()) {
            this.rating = Movie.UNKNOWN;
        } else {
            this.rating = rating;
        }
    }

    /**
     * Return the rating
     *
     * @return
     */
    public String getRating() {
        return rating;
    }

    /**
     * Update the movie object with the episode details
     *
     * @param movie
     */
    public void updateMovie(Movie movie) {
        if (episode < 0) {
            return;
        }

        for (MovieFile mf : movie.getMovieFiles()) {
            if (episode >= mf.getFirstPart() && episode <= mf.getLastPart()) {
                mf.setSeason(season);

                if (OverrideTools.checkOverwriteEpisodeTitle(mf, episode, MovieNFOReader.NFO_PLUGIN_ID)) {
                    mf.setTitle(episode, title, MovieNFOReader.NFO_PLUGIN_ID);
                }

                if (OverrideTools.checkOverwriteEpisodePlot(mf, episode, MovieNFOReader.NFO_PLUGIN_ID)) {
                    mf.setPlot(episode, plot, MovieNFOReader.NFO_PLUGIN_ID);
                }

                if (OverrideTools.checkOverwriteEpisodeRating(mf, episode, MovieNFOReader.NFO_PLUGIN_ID)) {
                    mf.setRating(episode, rating, MovieNFOReader.NFO_PLUGIN_ID);
                }

                if (OverrideTools.checkOverwriteEpisodeFirstAired(mf, episode, MovieNFOReader.NFO_PLUGIN_ID)) {
                    mf.setFirstAired(episode, firstAired, MovieNFOReader.NFO_PLUGIN_ID);
                }

                if (StringTools.isValidString(airsAfterSeason)) {
                    mf.setAirsAfterSeason(episode, airsAfterSeason);
                }

                if (StringTools.isValidString(airsBeforeSeason)) {
                    mf.setAirsBeforeSeason(episode, airsBeforeSeason);
                }

                if (StringTools.isValidString(airsBeforeEpisode)) {
                    mf.setAirsBeforeEpisode(episode, airsBeforeEpisode);
                }

            }
        }
    }

    public String getFirstAired() {
        return firstAired;
    }

    public String getAirsAfterSeason() {
        return airsAfterSeason;
    }

    public String getAirsBeforeSeason() {
        return airsBeforeSeason;
    }

    public String getAirsBeforeEpisode() {
        return airsBeforeEpisode;
    }

    public void setFirstAired(String firstAired) {
        this.firstAired = firstAired;
    }

    public void setAirsAfterSeason(String airsAfterSeason) {
        this.airsAfterSeason = airsAfterSeason;
    }

    public void setAirsBeforeSeason(String airsBeforeSeason) {
        this.airsBeforeSeason = airsBeforeSeason;
    }

    public void setAirsBeforeEpisode(String airsBeforeEpisode) {
        this.airsBeforeEpisode = airsBeforeEpisode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
