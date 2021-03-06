/*
 *      Copyright (c) 2004-2015 YAMJ Members
 *      https://github.com/orgs/YAMJ/people
 *
 *      This file is part of the Yet Another Media Jukebox (YAMJ).
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
package com.moviejukebox.model.overlay;

import java.util.ArrayList;
import java.util.List;

/**
 * Logo Overlay
 *
 * @author Stuart
 */
public class LogoOverlay extends PositionOverlay {

    private boolean before = Boolean.TRUE;
    private List<String> names = new ArrayList<>();
    private List<ImageOverlay> images = new ArrayList<>();
    private List<ConditionOverlay> positions = new ArrayList<>();

    public boolean isBefore() {
        return before;
    }

    public void setBefore(boolean before) {
        this.before = before;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<ImageOverlay> getImages() {
        return images;
    }

    public void setImages(List<ImageOverlay> images) {
        this.images = images;
    }

    public List<ConditionOverlay> getPositions() {
        return positions;
    }

    public void setPositions(List<ConditionOverlay> positions) {
        this.positions = positions;
    }
}
