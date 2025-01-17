package ch.epfl.biop.atlas.aligner.serializers;

import ch.epfl.biop.atlas.aligner.CancelableAction;
import ch.epfl.biop.atlas.aligner.SliceSources;
import com.google.gson.*;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.RealTransform;
import spimdata.util.Displaysettings;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SliceSourcesStateDeserializer implements JsonDeserializer<AlignerState.SliceSourcesState> {

    Consumer<SliceSources> sliceSourceConsumer;

    public SliceSourcesStateDeserializer(Consumer<SliceSources> sliceSourceConsumer) {
        this.sliceSourceConsumer = sliceSourceConsumer;
    }

    SliceSources slice;

    @Override
    public AlignerState.SliceSourcesState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        List<CancelableAction> actions = new ArrayList<>();

        JsonObject obj = json.getAsJsonObject();

        obj.get("actions").getAsJsonArray().forEach(jsonElement -> {
            System.out.println(jsonElement);
            CancelableAction action = context.deserialize(jsonElement, CancelableAction.class);
            action.runRequest();
            sliceSourceConsumer.accept(action.getSliceSources());
            slice = action.getSliceSources();
            slice.getGUIState().sliceDisplayModeChanged();
            if (slice!=null) slice.getGUIState().setSliceInvisible();
            actions.add(action);
        });

        Displaysettings[] ds = context.deserialize(obj.get("settings_per_channel"), Displaysettings[].class);
        boolean[] visible = context.deserialize(obj.get("channelsVisibility"), boolean[].class);
        boolean visibleUser = context.deserialize(obj.get("sliceVisibleUser"), boolean.class);
        AffineTransform3D preTransform = context.deserialize(obj.get("preTransform"), AffineTransform3D.class);

        AlignerState.SliceSourcesState sliceState = new AlignerState.SliceSourcesState();
        sliceState.actions = actions;
        sliceState.channelsVisibility = visible;
        sliceState.sliceVisibleUser = visibleUser;
        if (visibleUser) slice.getGUIState().setSliceVisible();
        sliceState.settings_per_channel = ds;
        sliceState.slice = slice;
        sliceState.preTransform = preTransform;

        return sliceState;
    }
}
