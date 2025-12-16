package orca.shearable_players;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

/*
    ShearablePlayers
    Copyright (C) 2025 Maxwellcrafter

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

/**
 * @author orca
 */

@Modmenu(modId = ShearablePlayers.MOD_ID)
@Config(name = "shearable-players-config", wrapperName = "ShearablePlayersConfig")
public class ShearablePlayersConfigModel {
	
	public SneakRequirement requireSneaking = SneakRequirement.IGNORE;
	
	public enum SneakRequirement {
		IGNORE, NOT_SNEAKING, REQUIRE_SNEAKING
	}
	
	public ValidToShear validToShear = ValidToShear.NO_DURABILITY;
	
	public enum ValidToShear {
		PUMPKIN,
		NO_DURABILITY,
		ANY
	}
	
	public RequireBinding requireBinding = RequireBinding.IGNORE;
	
	public enum RequireBinding {
		IGNORE, REQUIRE, REQUIRE_NO_CURSE
	}
	
	public boolean vanishIfCursed = true;
	
	public ItemDrop itemDropRule = ItemDrop.TRY_INVENTORY_THEN_DROP;
	
	public enum ItemDrop {
		ALWAYS_DROP,
		TRY_INVENTORY_THEN_DROP,
		CANCEL_INSTEAD_OF_DROP
	}
	
}
