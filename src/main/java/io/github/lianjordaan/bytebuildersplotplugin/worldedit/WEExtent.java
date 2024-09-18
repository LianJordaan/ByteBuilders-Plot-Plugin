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

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;

import java.util.Set;

public class WEExtent extends AbstractDelegateExtent {

    public static BlockState AIRSTATE = BlockTypes.AIR.getDefaultState();
    public static BaseBlock AIRBASE = BlockTypes.AIR.getDefaultState().toBaseBlock();
    private final Set<CuboidRegion> mask;

    public WEExtent(Set<CuboidRegion> mask, Extent extent) {
        super(extent);
        this.mask = mask;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean setBlock(BlockVector3 location, BlockStateHolder block)
            throws WorldEditException {
        return RegionUtil.maskContains(this.mask, location.getX(), location.getY(), location.getZ())
                && super.setBlock(location, block);
    }

    @Override
    public BlockState getBlock(BlockVector3 location) {
        if (RegionUtil.maskContains(this.mask, location.getX(), location.getY(), location.getZ())) {
            return super.getBlock(location);
        }
        return AIRSTATE;
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 location) {
        if (RegionUtil.maskContains(this.mask, location.getX(), location.getY(), location.getZ())) {
            return super.getFullBlock(location);
        }
        return AIRBASE;
    }

}