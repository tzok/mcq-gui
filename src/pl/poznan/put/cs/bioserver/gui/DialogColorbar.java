package pl.poznan.put.cs.bioserver.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.apache.commons.math3.stat.StatUtils;

import pl.poznan.put.cs.bioserver.beans.ComparisonLocal;
import pl.poznan.put.cs.bioserver.beans.ComparisonLocalMulti;

public class DialogColorbar extends JDialog {
    private static final long serialVersionUID = 2659329749184089277L;

    public DialogColorbar(final ComparisonLocalMulti localMulti) {
        super();

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        final List<Colorbar> list = new ArrayList<>();
        for (ComparisonLocal local : localMulti.getResults()) {
            c.gridx = 0;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            Colorbar colorbar = new Colorbar(local);
            list.add(colorbar);
            add(colorbar, c);
            // add(new JLabel(local.getTitle()), c);
            c.gridx++;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            add(new JLabel(local.getTitle()), c);
            c.gridy++;
        }

        final JCheckBox checkRelative = new JCheckBox("Relative");
        c.gridx = 1;
        add(checkRelative, c);

        setTitle("Colorbar");
        pack();

        checkRelative.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                double min = 0;
                double max = Math.PI;

                if (checkRelative.isSelected()) {
                    double lmin = Math.PI;
                    double lmax = 0;
                    for (ComparisonLocal local : localMulti.getResults()) {
                        double[] deltas = local.getAngles().get("AVERAGE")
                                .getDeltas();
                        lmin = Math.min(lmin, StatUtils.min(deltas));
                        lmax = Math.max(lmax, StatUtils.max(deltas));
                    }
                    min = lmin;
                    max = lmax;
                }

                for (Colorbar colorbar : list) {
                    colorbar.setMinMax(min, max);
                }
                repaint();
            }
        });
    }
}
