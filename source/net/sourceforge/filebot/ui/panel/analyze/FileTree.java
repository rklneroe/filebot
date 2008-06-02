
package net.sourceforge.filebot.ui.panel.analyze;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.filebot.ui.FileBotTree;
import net.sourceforge.filebot.ui.transfer.DefaultTransferHandler;
import net.sourceforge.filebot.ui.transfer.FileTransferable;
import net.sourceforge.filebot.ui.transfer.TransferablePolicyImportHandler;
import net.sourceforge.filebot.ui.transferablepolicies.TransferablePolicy;


class FileTree extends FileBotTree {
	
	public static final String LOADING_PROPERTY = "loading";
	public static final String CONTENT_PROPERTY = "content";
	
	private PostProcessor postProcessor;
	
	private final FileTreeTransferablePolicy transferablePolicy;
	
	
	public FileTree() {
		transferablePolicy = new FileTreeTransferablePolicy(this);
		transferablePolicy.addPropertyChangeListener(LOADING_PROPERTY, new LoadingPropertyChangeListener());
		
		setTransferHandler(new DefaultTransferHandler(new TransferablePolicyImportHandler(transferablePolicy), null));
	}
	

	public TransferablePolicy getTransferablePolicy() {
		return transferablePolicy;
	}
	

	public void removeTreeItems(TreePath paths[]) {
		List<TreeNode> changedNodes = new ArrayList<TreeNode>(paths.length);
		
		for (TreePath element : paths) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) (element.getLastPathComponent());
			
			if (!node.isRoot()) {
				changedNodes.add(node.getParent());
				node.removeFromParent();
			}
		}
		
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		
		for (TreeNode treeNode : changedNodes) {
			model.reload(treeNode);
		}
		
		contentChanged();
	}
	

	public void load(List<File> files) {
		FileTransferable tr = new FileTransferable(files);
		
		if (transferablePolicy.accept(tr))
			transferablePolicy.handleTransferable(tr, true);
	}
	

	@Override
	public void clear() {
		transferablePolicy.reset();
		
		super.clear();
		contentChanged();
	}
	

	private void contentChanged() {
		synchronized (this) {
			if (postProcessor != null)
				postProcessor.cancel(true);
			
			postProcessor = new PostProcessor();
			postProcessor.execute();
		}
	};
	
	
	private class LoadingPropertyChangeListener implements PropertyChangeListener {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Boolean loading = (Boolean) evt.getNewValue();
			
			firePropertyChange(FileTree.LOADING_PROPERTY, null, loading);
			
			if (!loading) {
				((DefaultTreeModel) getModel()).reload();
				contentChanged();
			}
		}
	}
	

	private class PostProcessor extends SwingWorker<List<File>, Void> {
		
		@Override
		protected List<File> doInBackground() throws Exception {
			return convertToList();
		}
		

		@Override
		protected void done() {
			if (isCancelled())
				return;
			
			try {
				List<File> files = get();
				FileTree.this.firePropertyChange(CONTENT_PROPERTY, null, files);
			} catch (Exception e) {
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, e.toString(), e);
			}
		}
		
	}
	
}
