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
package com.moviejukebox.model.Comparator;

import com.moviejukebox.model.Movie;
import com.moviejukebox.tools.PropertiesUtil;
import static com.moviejukebox.tools.StringTools.isValidString;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ilgizar
 */
public class MovieReleaseComparator extends MovieYearComparator {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MovieReleaseComparator.class);
    private Locale locale = Locale.ENGLISH;
    private final String dateLocale = PropertiesUtil.getProperty("mjb.locale", "en_US");

    public MovieReleaseComparator() {
        super(Boolean.TRUE);
        setLocale();
    }

    public MovieReleaseComparator(boolean ascending) {
        super(ascending);
        setLocale();
    }

    private void setLocale() {
        if (isValidString(dateLocale) && (dateLocale.length() == 2 || dateLocale.length() == 5)) {
            locale = new Locale(dateLocale.substring(0, 2), dateLocale.length() == 2 ? "" : dateLocale.substring(3, 5));
        }
    }

    @Override
    public int compare(Movie movie1, Movie movie2, boolean ascending) {
        int res = super.compare(movie1, movie2, ascending);
        String date1 = movie1.getReleaseDate();
        String date2 = movie2.getReleaseDate();
        if (res == 0 && isValidString(date1) && isValidString(date2)) {
            date1 = convertDate(date1);
            date2 = convertDate(date2);
            if (isValidString(date1) && isValidString(date2)) {
                try {
                    return ascending ? Integer.parseInt(date1) - Integer.parseInt(date2) : Integer.parseInt(date2) - Integer.parseInt(date1);
                } catch (NumberFormatException e) {
                }
            }
            return isValidString(date1) ? ascending ? 1 : - 1 : isValidString(date2) ? ascending ? -1 : 1 : 0;
        }
        return res;
    }

    private String convertDate(String date) {
        // output date pattern: 19931205
        SimpleDateFormat dstDate = new SimpleDateFormat("yyyyMMdd");

        // pattern for date: 05 December 1993
        Pattern dateRegex = Pattern.compile("(\\d{2}) (\\S+) (\\d{4})");
        Matcher dateMatch = dateRegex.matcher(date);
        if (dateMatch.find()) {
            SimpleDateFormat srcDate = new SimpleDateFormat("dd MMM yyyy", locale);
            try {
                return dstDate.format(srcDate.parse(dateMatch.group(0)));
            } catch (ParseException ex) {
            }

            try {
                return dstDate.format(srcDate.parse(dateMatch.group(1) + " " + correctShortMonth(dateMatch.group(2).substring(0, 3)) + " " + dateMatch.group(3)));
            } catch (ParseException ex) {
            }

            srcDate = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            try {
                return dstDate.format(srcDate.parse(dateMatch.group(0)));
            } catch (ParseException e) {
                LOG.debug("Unparseable date: {} ({})", dateMatch.group(0), dateLocale);
            }

            return Movie.UNKNOWN;
        } else {
            // pattern for date: December 1993
            dateRegex = Pattern.compile("(\\S+) (\\d{4})");
            dateMatch = dateRegex.matcher(date);
            if (dateMatch.find()) {
                SimpleDateFormat srcDate = new SimpleDateFormat("MMM yyyy", locale);
                try {
                    return dstDate.format(srcDate.parse(dateMatch.group(0)));
                } catch (ParseException ex) {
                }

                try {
                    return dstDate.format(srcDate.parse(correctShortMonth(dateMatch.group(1).substring(0, 3)) + " " + dateMatch.group(2)));
                } catch (ParseException ex) {
                }

                srcDate = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                try {
                    return dstDate.format(srcDate.parse(dateMatch.group(0)));
                } catch (ParseException ex) {
                    LOG.debug("Unparseable date: {} ({})", dateMatch.group(0), dateLocale);
                }

                return Movie.UNKNOWN;
            }
        }

        // pattern for date: 1993-12-05
        dateRegex = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        dateMatch = dateRegex.matcher(date);
        if (dateMatch.find()) {
            SimpleDateFormat srcDate = new SimpleDateFormat("yyyy-MM-dd", locale);
            try {
                return dstDate.format(srcDate.parse(dateMatch.group(0)));
            } catch (ParseException e) {
            }
        }

        // pattern for date: 05.12.1993
        dateRegex = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");
        dateMatch = dateRegex.matcher(date);
        if (dateMatch.find()) {
            SimpleDateFormat srcDate = new SimpleDateFormat("dd.MM.yyyy", locale);
            try {
                return dstDate.format(srcDate.parse(dateMatch.group(0)));
            } catch (ParseException e) {
            }
        }

        // pattern for date: 05.12.93
        dateRegex = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{2}");
        dateMatch = dateRegex.matcher(date);
        if (dateMatch.find()) {
            SimpleDateFormat srcDate = new SimpleDateFormat("dd.MM.yy", locale);
            try {
                return dstDate.format(srcDate.parse(dateMatch.group(0)));
            } catch (ParseException e) {
            }
        }

        // pattern for date: 05/12/1993
        dateRegex = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
        dateMatch = dateRegex.matcher(date);
        if (dateMatch.find()) {
            SimpleDateFormat srcDate = new SimpleDateFormat("dd/MM/yyyy", locale);
            try {
                return dstDate.format(srcDate.parse(dateMatch.group(0)));
            } catch (ParseException e) {
            }
        }

        return Movie.UNKNOWN;
    }

    private String correctShortMonth(String month) {
        if ("ru_RU".equals(dateLocale) && "мая".equals(month)) {
            return "май";
        }
        return month;
    }
}
