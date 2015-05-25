package pl.poznan.put.comparison.local;

import java.util.List;

import pl.poznan.put.comparison.IncomparableStructuresException;
import pl.poznan.put.matching.StructureSelection;
import pl.poznan.put.pdb.analysis.PdbCompactFragment;

public interface LocalComparator {
    LocalComparisonResult comparePair(StructureSelection target,
            StructureSelection model) throws IncomparableStructuresException;

    ModelsComparisonResult compareModels(PdbCompactFragment target,
            List<PdbCompactFragment> models) throws IncomparableStructuresException;
}
