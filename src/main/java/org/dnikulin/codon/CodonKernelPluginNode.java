package org.dnikulin.codon;

import org.dnikulin.codon.pipe.except.PipeException;
import org.dnikulin.codon.plugin.PluginNode;

public interface CodonKernelPluginNode extends PluginNode {
    public void addToCodonKernel(CodonKernel kernel) throws PipeException;
}
