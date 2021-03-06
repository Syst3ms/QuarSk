package fr.syst3ms.quarsk.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import fr.syst3ms.quarsk.classes.Registration;
import org.bukkit.block.Block;
import org.bukkit.material.Banner;
import org.jetbrains.annotations.NotNull;

/**
 * Created by PRODSEB on 30/01/2017.
 */
@Name("Is Wall Banner")
@Description("Checks if a banner is attached to a wall")
@Examples({
	"on right click:",
	"	if clicked block is a wall banner:",
	"		send \"You just clicked a wall banner !\""
})
@Since("1.2")
@SuppressWarnings({"unchecked"})
public class CondIsWallBanner extends PropertyCondition<Block> {

	static {
		Registration.newPropertyCondition(CondIsWallBanner.class, "[a] wall banner", "block");
	}

	@Override
	public boolean check(Block block) {
		return block instanceof Banner && ((Banner) block).isWallBanner();
	}

	@NotNull
	@Override
	protected String getPropertyName() {
		return "a wall banner";
	}
}
