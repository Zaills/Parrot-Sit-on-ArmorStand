package psas;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;

public class ParrotSitOnArmorStandClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		UseEntityCallback.EVENT.register(((player, level, interactionHand, entity, entityHitResult) -> {
			if (entity.getType() == EntityType.ARMOR_STAND && !level.isClientSide()
					&& (player.getShoulderParrotLeft().isPresent() || player.getShoulderParrotRight().isPresent()
						|| entity.getPassengers().stream().anyMatch(e -> e.getType() == EntityType.PARROT) && player.isCrouching())
			) {
				ParrotSitOnArmorStand.handleParrotTransfer(player, (net.minecraft.world.entity.decoration.ArmorStand) entity);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		}));
	}
}