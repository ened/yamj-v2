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
package com.moviejukebox.scanner;

import com.moviejukebox.model.Artwork.ArtworkType;
import com.moviejukebox.model.Movie;
import com.moviejukebox.model.MovieFile;
import com.moviejukebox.scanner.artwork.ArtworkScanner;
import com.moviejukebox.tools.GitRepositoryState;
import com.moviejukebox.tools.PropertiesUtil;
import static com.moviejukebox.tools.StringTools.isNotValidString;
import java.util.Date;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This function will validate the current movie object and return true if the
 * movie needs to be re-scanned.
 *
 * @author Stuart
 */
public final class RecheckScanner {

    private static final Logger LOG = LoggerFactory.getLogger(RecheckScanner.class);
    // Recheck variables
    private static final int RECHECK_MAX = PropertiesUtil.getIntProperty("mjb.recheck.Max", 50);
    private static final boolean RECHECK_XML = PropertiesUtil.getBooleanProperty("mjb.recheck.XML", Boolean.TRUE);
    private static final boolean RECHECK_VERSION = PropertiesUtil.getBooleanProperty("mjb.recheck.Version", Boolean.TRUE);
    private static final int RECHECK_DAYS = PropertiesUtil.getIntProperty("mjb.recheck.Days", 30);
    private static final int RECHECK_MIN_DAYS = PropertiesUtil.getIntProperty("mjb.recheck.minDays", 7);
    private static final boolean RECHECK_UNKNOWN = PropertiesUtil.getBooleanProperty("mjb.recheck.Unknown", Boolean.TRUE);
    private static final boolean RECHECK_EPISODE_PLOTS = PropertiesUtil.getBooleanProperty("mjb.includeEpisodePlots", Boolean.FALSE);
    // How many rechecks have been performed
    private static int recheckCount = 0;
    private static final String ERROR_IS_MISSING = "{} is missing {}, will rescan";
    private static final String ERROR_TV_MISSING = "{} - Part {} XML is missing {}, will rescan";

    // Property values
    private static final boolean FANART_MOVIE_DOWNLOAD = PropertiesUtil.getBooleanProperty("fanart.movie.download", Boolean.FALSE);
    private static final boolean FANART_TV_DOWNLOAD = PropertiesUtil.getBooleanProperty("fanart.tv.download", Boolean.FALSE);
    private static final boolean VIDEOIMAGE_DOWNLOAD = PropertiesUtil.getBooleanProperty("mjb.includeVideoImages", Boolean.FALSE);
    private static final boolean BANNER_DOWNLOAD = PropertiesUtil.getBooleanProperty("mjb.includeWideBanners", Boolean.FALSE);
    private static final boolean INCLUDE_EPISODE_RATING = PropertiesUtil.getBooleanProperty("mjb.includeEpisodeRating", Boolean.FALSE);
    private static final boolean INCLUDE_PEOPLE = PropertiesUtil.getBooleanProperty("mjb.people", Boolean.FALSE);
    private static final Set<ArtworkType> ARTWORK_REQUIRED = ArtworkScanner.getRequiredArtworkTypes();

    private RecheckScanner() {
        throw new UnsupportedOperationException("Class cannot be instantiated");
    }

    public static boolean scan(Movie movie) {
        // Do we need to recheck? Or is this an extra?
        if (!RECHECK_XML || movie.isExtra()) {
            return false;
        }

        PropertiesUtil.warnDeprecatedProperty("mjb.recheck.Revision");

        LOG.debug("Checking {}", movie.getBaseName());

        // Always perform these checks, regardless of the recheckCount
        if (recheckAlways(movie)) {
            return true;
        }

        if (recheckCount >= RECHECK_MAX) {
            // We are over the recheck maximum, so we won't recheck again this run
            return false;
        } else if (recheckCount == RECHECK_MAX) {
            LOG.debug("Threshold of {} rechecked videos reached. No more will be checked until the next run.", RECHECK_MAX);
            recheckCount++; // By incrementing this variable we will only display this message once.
            return false;
        }

        Date currentDate = new Date();
        long dateDiff = (currentDate.getTime() - movie.getMjbGenerationDate().toDate().getTime()) / (1000 * 60 * 60 * 24);

        // Check the date the XML file was last written to and skip if it's less than minDays
        if ((RECHECK_MIN_DAYS > 0) && (dateDiff <= RECHECK_MIN_DAYS)) {
            LOG.debug("{} - XML is {} days old, less than recheckMinDays ({}), not checking.", movie.getBaseName(), dateDiff, RECHECK_MIN_DAYS);
            return false;
        }

        // Check the date the XML file was written vs the current date
        if ((RECHECK_DAYS > 0) && (dateDiff > RECHECK_DAYS)) {
            LOG.debug("{} XML is {} days old, will rescan", movie.getBaseName(), dateDiff);
            recheckCount++;
            return true;
        }

        // Check for "UNKNOWN" values in the XML
        if (RECHECK_UNKNOWN) {
            if (isNotValidString(movie.getTitle()) && isNotValidString(movie.getYear())) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "the title");
                recheckCount++;
                return true;
            }

            if (isNotValidString(movie.getPlot())) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "plot");
                recheckCount++;
                return true;
            }

            if (isNotValidString(movie.getYear())) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "year");
                recheckCount++;
                return true;
            }

            if (movie.getGenres().isEmpty()) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "genres");
                recheckCount++;
                return true;
            }

            if (isNotValidString(movie.getPosterURL())) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "poster");
                recheckCount++;
                return true;
            }

            if (isNotValidString(movie.getFanartURL()) && ((FANART_MOVIE_DOWNLOAD && !movie.isTVShow()) || (FANART_TV_DOWNLOAD && movie.isTVShow()))) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "fanart");
                recheckCount++;
                return true;
            }

            // Check the FanartTV URLs
            if (fanartTvCheck(movie)) {
                return true;
            }

            // Only get ratings if the rating list is null or empty - We assume it's OK to have a -1 rating if there are entries in the array
            if (movie.getRatings() == null || movie.getRatings().isEmpty()) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "rating");
                recheckCount++;
                return true;
            }

            if (movie.isTVShow()) {
                if (BANNER_DOWNLOAD && isNotValidString(movie.getBannerURL())) {
                    LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "banner artwork");
                    recheckCount++;
                    return true;
                }

                if (isNotValidString(movie.getShowStatus())) {
                    LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "show status");
                    recheckCount++;
                    return true;
                }

                if (isNotValidString(movie.getReleaseDate())) {
                    LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "show release date");
                    recheckCount++;
                    return true;
                }

                // Check the TV Episodes
                if (tvEpisodesCheck(movie)) {
                    return true;
                }
            } // isTVShow
        }
        return false;
    }

    /**
     * Always perform these checks regardless of recheck count.
     *
     * @param movie
     * @return
     */
    private static boolean recheckAlways(Movie movie) {
        // Check for the version of YAMJ that wrote the XML file vs the current version
        if (RECHECK_VERSION && !movie.getMjbVersion().equalsIgnoreCase(GitRepositoryState.getVersion())) {
            LOG.debug("{} XML is from a previous version, will rescan", movie.getBaseName());
            return true;
        }

        if (INCLUDE_PEOPLE && movie.getPeople().isEmpty()) {
            LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "people data");
            return true;
        }
        return false;
    }

    /**
     * FANART.TV checking
     *
     * @param movie
     * @return
     */
    private static boolean fanartTvCheck(Movie movie) {
        if (movie.isTVShow()) {
            if (isNotValidString(movie.getClearArtURL()) && ARTWORK_REQUIRED.contains(ArtworkType.CLEARART)) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "ClearArt");
                recheckCount++;
                return true;
            }

            if (isNotValidString(movie.getClearLogoURL()) && ARTWORK_REQUIRED.contains(ArtworkType.CLEARLOGO)) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "ClearLogo");
                recheckCount++;
                return true;
            }

            if (isNotValidString(movie.getSeasonThumbURL()) && ARTWORK_REQUIRED.contains(ArtworkType.SEASONTHUMB)) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "SeasonThumb");
                recheckCount++;
                return true;
            }

            if (isNotValidString(movie.getTvThumbURL()) && ARTWORK_REQUIRED.contains(ArtworkType.TVTHUMB)) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "TvThumb");
                recheckCount++;
                return true;
            }
        } else {
            if (isNotValidString(movie.getClearArtURL()) && ARTWORK_REQUIRED.contains(ArtworkType.MOVIEART)) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "MovieArt");
                recheckCount++;
                return true;
            }

            if (isNotValidString(movie.getClearLogoURL()) && ARTWORK_REQUIRED.contains(ArtworkType.MOVIELOGO)) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "MovieLogo");
                recheckCount++;
                return true;
            }

            if (isNotValidString(movie.getMovieDiscURL()) && ARTWORK_REQUIRED.contains(ArtworkType.MOVIEDISC)) {
                LOG.debug(ERROR_IS_MISSING, movie.getBaseName(), "MovieDisc");
                recheckCount++;
                return true;
            }
        }
        return false;
    }

    /**
     * Scan the TV episodes
     *
     * @param movie
     * @return
     */
    private static boolean tvEpisodesCheck(Movie movie) {
        for (MovieFile mf : movie.getMovieFiles()) {
            if (isNotValidString(mf.getTitle())) {
                LOG.debug(ERROR_TV_MISSING, movie.getBaseName(), mf.getFirstPart(), "Title");
                mf.setNewFile(true); // This forces the episodes to be rechecked
                recheckCount++;
                return true;
            }

            if (RECHECK_EPISODE_PLOTS || VIDEOIMAGE_DOWNLOAD) {
                for (int part = mf.getFirstPart(); part <= mf.getLastPart(); part++) {
                    if (RECHECK_EPISODE_PLOTS && isNotValidString(mf.getPlot(part))) {
                        LOG.debug(ERROR_TV_MISSING, movie.getBaseName(), mf.getFirstPart(), "TV plot");
                        mf.setNewFile(true); // This forces the episodes to be rechecked
                        recheckCount++;
                        return true;
                    } // plots

                    if (VIDEOIMAGE_DOWNLOAD && isNotValidString(mf.getVideoImageURL(part))) {
                        LOG.debug(ERROR_TV_MISSING, movie.getBaseName(), mf.getFirstPart(), "TV video image");
                        mf.setNewFile(true); // This forces the episodes to be rechecked
                        recheckCount++;
                        return true;
                    } // videoimages
                } // moviefile parts loop
            } // if

            for (int part = mf.getFirstPart(); part <= mf.getLastPart(); part++) {
                if (isNotValidString(mf.getFirstAired(part))) {
                    LOG.debug(ERROR_TV_MISSING, movie.getBaseName(), mf.getFirstPart(), "TV first aired date");
                    mf.setNewFile(true); // This forces the episodes to be rechecked
                    recheckCount++;
                    return true;
                }
            }

            for (int part = mf.getFirstPart(); part <= mf.getLastPart(); part++) {
                if (INCLUDE_EPISODE_RATING && isNotValidString(mf.getRating(part))) {
                    LOG.debug(ERROR_TV_MISSING, movie.getBaseName(), mf.getFirstPart(), "TV rating");
                    mf.setNewFile(true); // This forces the episodes to be rechecked
                    recheckCount++;
                    return true;
                }
            }
        } // moviefiles loop

        return false;
    }
}
