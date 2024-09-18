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

}