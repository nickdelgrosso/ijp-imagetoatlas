package ch.epfl.biop.atlas.aligner.commands;

import ch.epfl.biop.atlas.aligner.MultiSlicePositioner;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;

@Plugin(type = Command.class,
        menuPath = "Plugins>BIOP>Atlas>Multi Image To Atlas>Export>Export Regions To File",
        description = "Export the transformed atlas regions of currently selected slices as ImageJ roi zip files.")
public class ExportRegionsToFileCommand implements Command {

    @Parameter
    MultiSlicePositioner mp;

    @Parameter(label="Roi Naming",choices={"name","acronym","id","Roi Manager Index (no suffix)"})
    String namingChoice;

    @Parameter(label="Directory for ROI Saving", style = "directory")
    File dirOutput;

    @Parameter(label="Erase Previous ROIs")
    boolean erasePreviousFile;

    @Override
    public void run() {
        // Cannot be accessed
        mp.exportSelectedSlicesRegionsToFile(namingChoice, dirOutput, erasePreviousFile);
    }

}
