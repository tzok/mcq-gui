package pl.poznan.put.gui;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import pl.poznan.put.interfaces.Clusterable;
import pl.poznan.put.interfaces.Exportable;
import pl.poznan.put.interfaces.Visualizable;
import pl.poznan.put.types.ExportFormat;

public class ProcessingResult implements Clusterable, Visualizable, Exportable {
    public static ProcessingResult emptyInstance() {
        return new ProcessingResult(null, null, null);
    }

    private final Clusterable clusterable;
    private final Visualizable visualizable;
    private final Exportable exportable;

    public ProcessingResult(Clusterable clusterable, Visualizable visualizable,
            Exportable exportable) {
        super();
        this.clusterable = clusterable;
        this.visualizable = visualizable;
        this.exportable = exportable;
    }

    public ProcessingResult(Object object) {
        super();
        this.clusterable = (Clusterable) (object instanceof Clusterable ? object : null);
        this.visualizable = (Visualizable) (object instanceof Visualizable ? object : null);
        this.exportable = (Exportable) (object instanceof Exportable ? object : null);
    }

    public boolean canCluster() {
        return clusterable != null;
    }

    public boolean canVisualize() {
        return visualizable != null;
    }

    public boolean canExport() {
        return exportable != null;
    }

    @Override
    public void cluster() {
        clusterable.cluster();
    }

    @Override
    public void visualize() {
        visualizable.visualize();
    }

    @Override
    public void visualize3D() {
        visualizable.visualize3D();
    }

    @Override
    public void export(OutputStream stream) throws IOException {
        exportable.export(stream);
    }

    @Override
    public ExportFormat getExportFormat() {
        return exportable.getExportFormat();
    }

    @Override
    public File suggestName() {
        return exportable.suggestName();
    }
}
