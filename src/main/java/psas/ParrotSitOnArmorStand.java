package psas;

import net.fabricmc.api.ModInitializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import psas.mixin.ServerPlayerAccessor;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ParrotSitOnArmorStand implements ModInitializer {
	public static final String MOD_ID = "psas";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Parrot Sit on ArmorStand is loading!");
	}

	public static void handleParrotTransfer(Player player, ArmorStand armorStand) {
		if (!(player instanceof ServerPlayer serverPlayer)) return;

		Optional<Parrot.Variant> parrot = serverPlayer.getShoulderParrotLeft();
		if (parrot.isEmpty()) {
			parrot = serverPlayer.getShoulderParrotRight();
		}
		if (parrot.isPresent() && armorStand.getPassengers().isEmpty() && !armorStand.isRemoved()) {
			ServerLevel level = (ServerLevel) armorStand.level();

			ServerPlayerAccessor serverPlayerAccessor = (ServerPlayerAccessor) serverPlayer;

			Parrot newParrotLeft = respawnParrot(serverPlayer.getShoulderEntityLeft(), level, serverPlayer);
			Parrot newParrotRight = respawnParrot(serverPlayer.getShoulderEntityRight(), level, serverPlayer);

			serverPlayerAccessor.invokeSetShoulderEntityLeft(new CompoundTag());
			serverPlayerAccessor.invokeSetShoulderEntityRight(new CompoundTag());

			if (newParrotLeft != null) {
				newParrotLeft.startRiding(armorStand, true, true);
			}
			if (newParrotRight != null) {
				newParrotRight.startRiding(armorStand, true, true);
			}
		} else {
			for (Entity entity : armorStand.getPassengers()) {
				if (entity instanceof Parrot parrotEntity) {
					parrotEntity.stopRiding();
				}
			}
		}
	}

	private static Parrot respawnParrot(CompoundTag compoundTag, ServerLevel var3, LivingEntity owner) {
		AtomicReference<Parrot> newParrot = new AtomicReference<>();
		if (var3 instanceof ServerLevel serverLevel) {
			if (!compoundTag.isEmpty()) {
				ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(owner.problemPath(), LOGGER);

				try {
					EntityType.create(TagValueInput.create(scopedCollector.forChild(() -> {
						return ".shoulder";
					}), serverLevel.registryAccess(), compoundTag), serverLevel, EntitySpawnReason.LOAD).ifPresent((entity) -> {
						if (entity instanceof TamableAnimal tamableAnimal) {
							tamableAnimal.setOwner(owner);
						}

						entity.setPos(owner.getX(), owner.getY() + 0.699999988079071, owner.getZ());
						serverLevel.addWithUUID(entity);
						newParrot.set((Parrot) entity);
					});
				} catch (Throwable var7) {
					try {
						scopedCollector.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}

					throw var7;
				}

				scopedCollector.close();
				if (newParrot.get() != null) {
					return newParrot.get();
				}
			}
		}
		return null;
	}
}