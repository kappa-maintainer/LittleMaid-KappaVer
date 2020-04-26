package net.firis.lmt.command;

import net.blacklab.lmr.LittleMaidReengaged;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class DebugCommand extends CommandBase implements ICommand {

	public static float X = 0.0F;
	public static float Y = 0.0F;
	public static float Z = 0.0F;
	
	@Override
	public String getName() {
		return "lmd";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "lmd X value";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		switch(args[0]) {
		case "x":
			X = Float.parseFloat(args[1]);
			break;
		case "y":
			Y = Float.parseFloat(args[1]);
			break;
		case "z":
			Z = Float.parseFloat(args[1]);
			break;
		case "print":
			LittleMaidReengaged.logger.info(((Float)X).toString() + " " + ((Float)Y).toString() + " " + ((Float)Z).toString());
		default:
		}
		
	}

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
