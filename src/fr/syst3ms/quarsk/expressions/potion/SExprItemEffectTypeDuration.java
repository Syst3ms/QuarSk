package fr.syst3ms.quarsk.expressions.potion;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import fr.syst3ms.quarsk.classes.Registration;
import fr.syst3ms.quarsk.util.PotionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by PRODSEB on 27/01/2017.
 */
@SuppressWarnings({"unchecked"})
public class SExprItemEffectTypeDuration extends SimpleExpression<Timespan> {
	static {
		Registration.newExpression(
			SExprItemEffectTypeDuration.class,
			Timespan.class,
			ExpressionType.COMBINED,
			"duration of %potioneffecttype% on %itemstack%",
			"%potioneffecttype%['s] duration on %itemstack%"
		);
	}

	private Expression<PotionEffectType> effectType;
	private Expression<ItemStack> item;

	@Override
	public boolean init(Expression<?>[] expr, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		effectType = (Expression<PotionEffectType>) expr[0];
		item = (Expression<ItemStack>) expr[1];
		return true;
	}

	@Nullable
	@Override
	protected Timespan[] get(Event e) {
		ItemStack i = item.getSingle(e);
		if (i == null) {
			return null;
		}
		if (PotionUtils.isPotionItem(i)) {
			PotionMeta meta = (PotionMeta) i.getItemMeta();
			PotionEffect eff = PotionUtils.getEffectByEffectType(meta, effectType.getSingle(e));
			if (eff == null) {
				return null;
			}
			return new Timespan[]{Timespan.fromTicks_i(eff.getDuration())};
		}
		return null;
	}

	@Override
	public void change(Event e, Object[] delta, @NotNull Changer.ChangeMode mode) {
		ItemStack i = item.getSingle(e);
		if (i == null) {
			return;
		}
		if (PotionUtils.isPotionItem(i)) {
			PotionMeta meta = (PotionMeta) i.getItemMeta();
			PotionEffect potionEffect = (meta.getBasePotionData().getType() != PotionType.UNCRAFTABLE)
				? PotionUtils.getEffectByEffectType(meta, effectType.getSingle(e))
				: PotionUtils.fromPotionData(meta.getBasePotionData());
			if (potionEffect == null) {
				return;
			}
			if (meta.getBasePotionData().getType() != PotionType.UNCRAFTABLE) {
				meta.removeCustomEffect(effectType.getSingle(e));
			} else {
				meta.setBasePotionData(PotionUtils.emptyPotionData());
			}
			Timespan timespan = (Timespan) delta[0];
			switch (mode) {
				case ADD:
					meta.addCustomEffect(new PotionEffect(
						potionEffect.getType(),
						potionEffect.getDuration() + Math.toIntExact(timespan.getTicks_i()),
						potionEffect.getAmplifier(),
						potionEffect.isAmbient(),
						potionEffect.hasParticles(),
						potionEffect.getColor()
					), true);
					break;
				case SET:
					meta.addCustomEffect(new PotionEffect(
						potionEffect.getType(),
						Math.toIntExact(timespan.getTicks_i()),
						potionEffect.getAmplifier(),
						potionEffect.isAmbient(),
						potionEffect.hasParticles(),
						potionEffect.getColor()
					), true);
					break;
				case REMOVE:
					meta.addCustomEffect(new PotionEffect(
						potionEffect.getType(),
						(potionEffect.getDuration() - Math.toIntExact(timespan.getTicks_i()) > 0)
							? potionEffect.getDuration() - Math.toIntExact(timespan.getTicks_i())
							: potionEffect.getDuration(),
						potionEffect.getAmplifier(),
						potionEffect.isAmbient(),
						potionEffect.hasParticles(),
						potionEffect.getColor()
					), true);
					break;
			}
		}
	}

	@Nullable
	@Override
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode != Changer.ChangeMode.REMOVE_ALL && mode != Changer.ChangeMode.RESET
			&& mode != Changer.ChangeMode.DELETE) {
			return CollectionUtils.array(Timespan.class);
		}
		return null;
	}

	@NotNull
	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public String toString(Event event, boolean b) {
		return "duration of " + effectType.toString(event, b) + " on " + item.toString(event, b);
	}
}
