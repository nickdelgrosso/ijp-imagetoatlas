package ch.epfl.biop.atlas.aligner.commands;

import ch.epfl.biop.atlas.aligner.MultiSlicePositioner;
import ch.epfl.biop.atlas.aligner.sourcepreprocessors.SourcesChannelsSelect;
import ch.epfl.biop.atlas.aligner.sourcepreprocessors.SourcesProcessor;
import ch.epfl.biop.atlas.aligner.sourcepreprocessors.SourcesProcessorHelper;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Plugin(type = Command.class,
        menuPath = "Plugins>BIOP>Atlas>Multi Image To Atlas>Align>Edit Last Registration",
        description = "Edit the last registration of the current selected slices, if possible.")
public class EditLastRegistrationCommand implements Command {

    protected static Logger logger = LoggerFactory.getLogger(EditLastRegistrationCommand.class);

    @Parameter
    MultiSlicePositioner mp;

    @Parameter(label = "Reuse original channels of the registration")
    boolean reuseOriginalChannels;

    @Parameter(label = "Atlas channels, 0-based, comma separated, '*' for all channels", description = "'0,2' for channels 0 and 2")
    String atlasStringChannel = "*";

    @Parameter(label = "Slices channels, 0-based, comma separated, '*' for all channels", description = "'0,2' for channels 0 and 2")
    String slicesStringChannel = "*";

    @Override
    public void run() {
        logger.info("Edit last registration command called.");

        SourcesProcessor preprocessSlice = SourcesProcessorHelper.Identity();

        SourcesProcessor preprocessAtlas = SourcesProcessorHelper.Identity();

        if (!slicesStringChannel.trim().equals("*")) {
            List<Integer> indices = Arrays.stream(slicesStringChannel.trim().split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
            int maxIndex = indices.stream().mapToInt(e -> e).max().getAsInt();
            if (maxIndex>=mp.getChannelBoundForSelectedSlices()) {
                mp.log.accept("Missing channel in selected slice(s).");
                mp.errlog.accept("Missing channel in selected slice(s)\n One selected slice only has "+mp.getChannelBoundForSelectedSlices()+" channel(s).\n Maximum index : "+(mp.getChannelBoundForSelectedSlices()-1) );
                return;
            }
            preprocessSlice = new SourcesChannelsSelect(indices);
        }

        if (!atlasStringChannel.trim().equals("*")) {
            List<Integer> indices = Arrays.stream(atlasStringChannel.trim().split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());

            int maxIndex = indices.stream().mapToInt(e -> e).max().getAsInt();

            int maxChannelInAtlas = mp.getReslicedAtlas().nonExtendedSlicedSources.length;
            if (maxIndex>=maxChannelInAtlas) {
                mp.log.accept("Missing channels in atlas.");
                mp.errlog.accept("The atlas only has "+maxChannelInAtlas+" channel(s).\n Maximum index : "+(maxChannelInAtlas-1) );
                return;
            }

            preprocessAtlas = new SourcesChannelsSelect(indices);
        }


        mp.editLastRegistration(reuseOriginalChannels, preprocessSlice, preprocessAtlas);
    }
}
