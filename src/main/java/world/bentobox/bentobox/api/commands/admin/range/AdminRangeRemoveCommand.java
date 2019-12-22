package world.bentobox.bentobox.api.commands.admin.range;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jdt.annotation.NonNull;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;

import java.util.List;
import java.util.UUID;

/**
 * @since 1.10.0
 * @author Poslovitch
 */
public class AdminRangeRemoveCommand extends CompositeCommand {

    public AdminRangeRemoveCommand(AdminRangeCommand parent) {
        super(parent, "remove");
    }

    @Override
    public void setup() {
        setDescription("commands.admin.range.remove.description");
        setParametersHelp("commands.admin.range.remove.parameters");
    }

    @Override
    public boolean execute(User user, String label, @NonNull List<String> args) {
        if (args.size() != 2) {
            showHelp(this, user);
            return false;
        }

        UUID target = getPlayers().getUUID(args.get(0));
        if (target == null) {
            user.sendMessage("general.errors.unknown-player", TextVariables.NAME, args.get(0));
            return false;
        }

        if (!(getIslands().hasIsland(getWorld(), target) || getIslands().inTeam(getWorld(), target))) {
            user.sendMessage("general.errors.player-has-no-island");
            return false;
        }

        if (!Util.isInteger(args.get(1), true) || Integer.valueOf(args.get(1)) < 0) {
            user.sendMessage("general.errors.must-be-positive-number", TextVariables.NUMBER, args.get(1));
            return false;
        }

        Island island = getIslands().getIsland(getWorld(), target);
        int newRange = island.getProtectionRange() + Integer.valueOf(args.get(1));

        if (newRange <= 1) {
            user.sendMessage("commands.admin.range.invalid-value.too-low", TextVariables.NUMBER, String.valueOf(island.getRange()));
            return false;
        } else if (newRange == island.getProtectionRange()) {
            user.sendMessage("commands.admin.range.invalid-value.same-as-before", TextVariables.NUMBER, args.get(1));
            return false;
        }

        // Well, now it can be applied without taking any risks !
        island.setProtectionRange(newRange);
        user.sendMessage("commands.admin.range.remove.success",
                TextVariables.NAME, args.get(0), TextVariables.NUMBER, args.get(1),
                "[total]", String.valueOf(newRange));

        return true;
    }
}