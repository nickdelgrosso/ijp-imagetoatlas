package ch.epfl.biop.atlas.aligner;


import ch.epfl.biop.atlas.SliceToImagePlus;
import ch.epfl.biop.atlas.aligner.sourcepreprocessors.SourcesProcessor;
import ij.ImagePlus;

import java.awt.*;

public class ExportAtlasSliceToImagePlus extends CancelableAction {

    final SliceSources slice;
    final SourcesProcessor preprocess;
    final double px, py, sx, sy, pixel_size_mm;
    final int timepoint;
    final boolean interpolate;

    ImagePlus resultImage = null;

    public ExportAtlasSliceToImagePlus(MultiSlicePositioner mp,
                                       SliceSources slice,
                                       SourcesProcessor preprocess,
                                       double px, double py, double sx, double sy,
                                       double pixel_size_millimeter, int timepoint,
                                       boolean interpolate) {
        super(mp);
        this.slice = slice;
        this.preprocess = preprocess;
        this.px = px;
        this.py = py;
        this.sx = sx;
        this.sy = sy;
        this.pixel_size_mm = pixel_size_millimeter;
        this.timepoint = timepoint;
        this.interpolate = interpolate;
    }

    @Override
    public SliceSources getSliceSources() {
        return slice;
    }

    @Override
    protected boolean run() {
        resultImage = SliceToImagePlus.exportAtlas(mp,slice,preprocess,px,py,sx,sy,pixel_size_mm,timepoint,interpolate);
        return resultImage!=null;
    }

    @Override
    protected boolean cancel() {
        clean(); // Allows GC
        return true;
    }

    public ImagePlus getImagePlus() {
        return resultImage;
    }

    public void clean() {
        resultImage = null;
    }

    public void drawAction(Graphics2D g, double px, double py, double scale) {
        switch (slice.getActionState(this)) {
            case "(done)":
                g.setColor(new Color(0, 255, 0, 200));
                break;
            case "(locked)":
                g.setColor(new Color(255, 0, 0, 200));
                break;
            case "(pending)":
                g.setColor(new Color(255, 255, 0, 200));
                break;
        }
        g.fillRect((int) (px - 7), (int) (py - 7), 14, 14);
        g.setColor(new Color(255, 255, 255, 200));
        g.drawString("E", (int) px - 4, (int) py + 5);
    }

}
