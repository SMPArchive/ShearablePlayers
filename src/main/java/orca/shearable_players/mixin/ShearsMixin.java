package orca.shearable_players.mixin;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.gameevent.GameEvent;
import orca.shearable_players.ShearablePlayers;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static orca.shearable_players.ShearablePlayersConfigModel.*;

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
 *
 * @author orca
 *
 */


@Mixin(ShearsItem.class)
public class ShearsMixin extends Item {
	
	
	public ShearsMixin (Properties properties) {
		super(properties);
	}
	
	
	@Unique
	private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[] {
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};
	
	public @NotNull InteractionResult interactLivingEntity (ItemStack itemStack, Player sourcePlayer, LivingEntity livingEntity, InteractionHand interactionHand) {
		if (sourcePlayer.isLocalPlayer()) return InteractionResult.PASS;
		if (!(sourcePlayer.level() instanceof ServerLevel serverLevel)) return InteractionResult.PASS;
		if (!(livingEntity instanceof Player targetPlayer)) return InteractionResult.PASS;
		if (targetPlayer.getAbilities().invulnerable) return InteractionResult.PASS;
		
		if (ShearablePlayers.CONFIG.requireSneaking() != SneakRequirement.IGNORE) {
			boolean crouching = targetPlayer.isCrouching();
			
			boolean shouldCrouch = ShearablePlayers.CONFIG.requireSneaking() == SneakRequirement.REQUIRE_SNEAKING;
			
			if (crouching != shouldCrouch) return InteractionResult.PASS;
		}
		
		for (EquipmentSlot slot : ARMOR_SLOTS) {
			
			if (tryShearSlot(serverLevel, targetPlayer, slot)) {
				
				//Make act like shears
				targetPlayer.gameEvent(GameEvent.SHEAR, sourcePlayer);
				itemStack.hurtAndBreak(1, sourcePlayer, LivingEntity.getSlotForHand(interactionHand));
				
				serverLevel.playSound(null, targetPlayer, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
				
				return InteractionResult.SUCCESS_SERVER;
			}
		}
		
		return  InteractionResult.PASS;
	}

	@Unique
	private static boolean tryShearSlot (ServerLevel level, Player targetPlayer, EquipmentSlot slot) {
		ItemStack armor = targetPlayer.getItemBySlot(slot);
		
		if (armor.isEmpty()) return false;
		
		
		boolean valid = switch (ShearablePlayers.CONFIG.validToShear()) {
			case ValidToShear.PUMPKIN -> armor.is(Items.CARVED_PUMPKIN);
			case ValidToShear.NO_DURABILITY -> !armor.isDamageableItem();
			default -> true;
		};
		
		if (!valid) return false;
		
		ItemEnchantments enchantments = armor.getEnchantments();
		
		boolean hasBinding = false;
		boolean hasVanishing = false;
		
		for (Holder<Enchantment> enchantment : enchantments.keySet()) {
			if (enchantment.is(Enchantments.BINDING_CURSE)) {
				hasBinding = true;
			} else if (enchantment.is(Enchantments.VANISHING_CURSE)) {
				hasVanishing = true;
			}
		}
		
		if (ShearablePlayers.CONFIG.requireBinding() != RequireBinding.IGNORE) {
			
			boolean requireBinding = (ShearablePlayers.CONFIG.requireBinding() == RequireBinding.REQUIRE);
			
			if (hasBinding != requireBinding) return false;
		}
		
		if (ShearablePlayers.CONFIG.vanishIfCursed() && hasVanishing) {
			targetPlayer.setItemSlot(slot, ItemStack.EMPTY);
			
			return true;
		} else {
			
			boolean sheared = switch (ShearablePlayers.CONFIG.itemDropRule()) {
				case ItemDrop.TRY_INVENTORY_THEN_DROP	-> tryInventoryThenDrop(level, targetPlayer, armor, false);
				case ItemDrop.CANCEL_INSTEAD_OF_DROP	-> tryInventoryThenDrop(level, targetPlayer, armor, true);
				case ItemDrop.ALWAYS_DROP				-> dropItem(level, targetPlayer, armor);
				case null -> false;
			};
			
			
			if (!sheared) return false;
			
			targetPlayer.setItemSlot(slot, ItemStack.EMPTY);
			
			return true;
		}
		
	}
	
	@Unique
	private static boolean tryInventoryThenDrop (ServerLevel level, Player targetPlayer, ItemStack armor, boolean cancelIfFailed) {
		
		boolean added = targetPlayer.getInventory().add(armor.copy());
		
		if (added) return true;
		
		if (cancelIfFailed) return false;
		
		return dropItem(level, targetPlayer, armor);
	}
	
	@Unique
	private static boolean dropItem (ServerLevel level, Player targetPlayer, ItemStack armor) {
		
		ItemEntity itemEntity = targetPlayer.spawnAtLocation(level, armor.copyWithCount(1), 1.0F);
		
		if (itemEntity != null) {
			RandomSource random = targetPlayer.getRandom();
			
			//Based on sheep shearing drops
			double x = ((random.nextFloat() - random.nextFloat()) * 0.1F);
			double y = (random.nextFloat() * 0.05F);
			double z = ((random.nextFloat() - random.nextFloat()) * 0.1F);
			
			itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add(x, y, z));
		}
		
		return itemEntity != null;
	}
}