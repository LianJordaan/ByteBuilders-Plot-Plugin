package io.github.lianjordaan.bytebuildersplotplugin.worldedit;

/*
 * PlotSquared, a land and world management plugin for Minecraft.
 * Copyright (C) IntellectualSites <https://intellectualsites.com>
 * Copyright (C) IntellectualSites team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;

import java.util.Set;

public class RegionUtil {

    public static boolean maskContains(Set<CuboidRegion> mask, int x, int y, int z) {
        for (CuboidRegion region : mask) {
            if (RegionUtil.contains(region, x, y, z)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(CuboidRegion region, int x, int y, int z) {
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        return x >= min.getX() && x <= max.getX() && z >= min.getZ() && z <= max.getZ() && y >= min
                .getY() && y <= max.getY();
    }

    public static boolean isWithin(CuboidRegion inner, CuboidRegion outer) {
        BlockVector3 innerMin = inner.getMinimumPoint();
        BlockVector3 innerMax = inner.getMaximumPoint();

        BlockVector3 outerMin = outer.getMinimumPoint();
        BlockVector3 outerMax = outer.getMaximumPoint();

        return innerMin.getX() >= outerMin.getX() && innerMax.getX() <= outerMax.getX()
                && innerMin.getZ() >= outerMin.getZ() && innerMax.getZ() <= outerMax.getZ()
                && innerMin.getY() >= outerMin.getY() && innerMax.getY() <= outerMax.getY();
    }

    public static CuboidRegion clampSelection(CuboidRegion selection, CuboidRegion bounds) {
        BlockVector3 selMin = selection.getMinimumPoint();
        BlockVector3 selMax = selection.getMaximumPoint();

        BlockVector3 boundsMin = bounds.getMinimumPoint();
        BlockVector3 boundsMax = bounds.getMaximumPoint();

        // Calculate clamped coordinates
        int clampedMinX = Math.max(selMin.getX(), boundsMin.getX());
        int clampedMinY = Math.max(selMin.getY(), boundsMin.getY());
        int clampedMinZ = Math.max(selMin.getZ(), boundsMin.getZ());

        int clampedMaxX = Math.min(selMax.getX(), boundsMax.getX());
        int clampedMaxY = Math.min(selMax.getY(), boundsMax.getY());
        int clampedMaxZ = Math.min(selMax.getZ(), boundsMax.getZ());

        // Ensure that the clamped max is greater than or equal to the clamped min
        clampedMaxX = Math.max(clampedMaxX, clampedMinX);
        clampedMaxY = Math.max(clampedMaxY, clampedMinY);
        clampedMaxZ = Math.max(clampedMaxZ, clampedMinZ);

        return new CuboidRegion(BlockVector3.at(clampedMinX, clampedMinY, clampedMinZ),
                BlockVector3.at(clampedMaxX, clampedMaxY, clampedMaxZ));
    }

}