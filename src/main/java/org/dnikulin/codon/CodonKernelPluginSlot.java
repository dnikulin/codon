package org.dnikulin.codon;

import org.dnikulin.codon.pipe.except.PipeException;
import org.dnikulin.codon.plugin.PluginNode;
import org.dnikulin.codon.plugin.PluginSlot;

public class CodonKernelPluginSlot implements PluginSlot {
    private final CodonKernel kernel;

    public CodonKernelPluginSlot(CodonKernel kernel) {
        this.kernel = kernel;
    }

    @Override
    public String getPluginSlotName() {
        return "Codon kernel";
    }

    @Override
    public Class<? extends PluginNode> getPluginInterface() {
        return CodonKernelPluginNode.class;
    }

    @Override
    public void installPlugin(PluginNode plugin) {
        try {
            ((CodonKernelPluginNode) plugin).addToCodonKernel(kernel);
        } catch (PipeException ex) {
            ex.printStackTrace();
        }
    }
}
