package ch.epfl.biop.atlas.aligner.commands;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import sc.fiji.bdvpg.bdv.config.BdvSettingsGUISetter;
import sc.fiji.bdvpg.scijava.command.BdvPlaygroundActionCommand;

import java.io.File;

@Plugin(type = BdvPlaygroundActionCommand.class,
        menuPath = "Plugins>BIOP>Atlas>Multi Image To Atlas>Preferences",
        description = "Sets actions linked to key / mouse event in ABBA (not functional)")

public class ABBASettingsCommand implements Command {

    @Override
    public void run() {
        new BdvSettingsGUISetter("plugins"+ File.separator + "bdvpgsettings"+File.separator+"abba").run();
    }

}
