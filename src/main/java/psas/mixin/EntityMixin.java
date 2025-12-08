package psas.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Entity.class)
public class EntityMixin {
	@Inject(method = "positionRider(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private void psas_positionRider(Entity passenger, CallbackInfo ci){
		if (!((Object) this instanceof ArmorStand self)) return;
		if (!(passenger instanceof Parrot parrot)) return;

		double x = self.getX();
		double y = self.getY() + self.getBbHeight();
		if (self.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
			y -= 0.1;
		}
		double z = self.getZ();

		parrot.setPos(x, y, z);
		parrot.setYRot(self.getYRot());
		parrot.setYRot(0.0f);
		ci.cancel();
	}
}
