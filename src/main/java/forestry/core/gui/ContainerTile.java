/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui;

import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import forestry.api.core.IErrorState;
import forestry.core.gadgets.TileForestry;
import forestry.core.network.PacketErrorUpdate;
import forestry.core.proxy.Proxies;

public class ContainerTile<T extends TileForestry> extends ContainerForestry {

	protected final T tile;

	public ContainerTile(T tileForestry, InventoryPlayer playerInventory, int xInv, int yInv) {
		this.tile = tileForestry;

		addPlayerInventory(playerInventory, xInv, yInv);
	}

	@Override
	protected final boolean canAccess(EntityPlayer player) {
		return player != null && tile.allowsAlteration(player);
	}

	@Override
	public final boolean canInteractWith(EntityPlayer entityplayer) {
		return tile.isUseableByPlayer(entityplayer);
	}

	private ImmutableSet<IErrorState> previousErrorStates;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		ImmutableSet<IErrorState> errorStates = tile.getErrorStates();

		if ((previousErrorStates != null) && !errorStates.equals(previousErrorStates)) {
			PacketErrorUpdate packet = new PacketErrorUpdate(tile);
			for (Object crafter : crafters) {
				if (crafter instanceof EntityPlayer) {
					Proxies.net.sendToPlayer(packet, (EntityPlayer) crafter);
				}
			}
		}

		previousErrorStates = errorStates;
	}
}
