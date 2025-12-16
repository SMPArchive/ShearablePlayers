package orca.shearable_players.mixin;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 *
 * @author orcagrady
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
		if (!targetPlayer.isCrouching()) return InteractionResult.PASS;
		
		for (EquipmentSlot slot : ARMOR_SLOTS) {
			
			if (tryShearSlot(targetPlayer, slot)) {
				
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
	private static boolean tryShearSlot (Player targetPlayer, EquipmentSlot slot) {
		ItemStack armor = targetPlayer.getItemBySlot(slot);
		
		if (armor.isEmpty()) return false;
		
		//if (!armor.is(Items.CARVED_PUMPKIN)) return InteractionResult.PASS;
		
		ItemEnchantments enchantments = armor.getEnchantments();
		
		boolean hasBinding = false;
		
		for (Holder<Enchantment> enchantment : enchantments.keySet()) {
			if (enchantment.is(Enchantments.BINDING_CURSE)) {
				hasBinding = true;
				break;
			}
		}
		
		if (!hasBinding) return false;
		
		boolean added = targetPlayer.getInventory().add(armor.copy());
		
		if (!added) return false;
		
		targetPlayer.setItemSlot(slot, ItemStack.EMPTY);
		
		return true;
	}
	
}