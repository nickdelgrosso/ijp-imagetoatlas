package ch.epfl.biop.atlas.aligner.commands;

import bdv.viewer.SourceAndConverter;
import ch.epfl.biop.atlas.aligner.MultiSlicePositioner;
import ch.epfl.biop.bdv.command.importer.QuPathProjectToBDVDatasetCommand;
import ch.epfl.biop.spimdata.qupath.QuPathEntryEntity;
import mpicbg.spim.data.generic.AbstractSpimData;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import sc.fiji.bdvpg.scijava.services.SourceAndConverterService;

import java.io.File;
import java.util.concurrent.ExecutionException;

@Plugin(type = Command.class, menuPath = "Plugins>BIOP>Atlas>Multi Image To Atlas>Import>Import QuPath Project")
public class ImportQuPathProjectCommand implements Command {

    @Parameter
    MultiSlicePositioner mp;

    @Parameter(label = "QuPath project file (.qpproj)")
    File quPathProject;

    @Parameter(label = "Initial axis position (0 = front, mm units)")
    double sliceAxisInitial;

    @Parameter(label = "Axis increment between slices (mm, can be negative for reverse order)")
    double incrementBetweenSlices;

    @Parameter
    CommandService command_service;

    @Parameter
    SourceAndConverterService sac_service;

    @Override
    public void run() {
        try {
            AbstractSpimData spimdata = (AbstractSpimData) command_service
                    .run(QuPathProjectToBDVDatasetCommand.class,true,
                            "quPathProject", quPathProject,
                            "unit", "MILLIMETER").get().getOutput("spimData");
            SourceAndConverter[] sacs =
                    sac_service.getSourceAndConverterFromSpimdata(spimdata)
                            .toArray(new SourceAndConverter[0]);
            mp.createSlice(sacs,sliceAxisInitial, incrementBetweenSlices, QuPathEntryEntity.class, new QuPathEntryEntity(-1));
            mp.selectSlice(mp.getSortedSlices());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            mp.errorMessageForUser.accept("QuPath Import Error",
                    "QuPath project couldn't be imported.\n"+
                       "Check whether the project can be opened in QuPath (v0.2+), fix URI if necessary.\n"+
                       "Only (rotated) Bio-Formats image server are supported.");
            e.printStackTrace();
        }
    }

}
