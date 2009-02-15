
package net.sourceforge.filebot.ui;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.filebot.ui.transfer.FileTransferable;
import net.sourceforge.filebot.ui.transfer.TransferablePolicy;
import net.sourceforge.filebot.ui.transfer.TransferablePolicy.TransferAction;
import net.sourceforge.tuned.MessageBus;
import net.sourceforge.tuned.MessageHandler;


public class FileTransferableMessageHandler implements MessageHandler {
	
	private final FileBotPanel panel;
	private final TransferablePolicy transferablePolicy;
	
	
	public FileTransferableMessageHandler(FileBotPanel panel, TransferablePolicy transferablePolicy) {
		this.panel = panel;
		this.transferablePolicy = transferablePolicy;
	}
	

	@Override
	public void handle(String topic, Object... messages) throws Exception {
		// switch to panel
		MessageBus.getDefault().publish("panel", panel);
		
		List<File> files = new ArrayList<File>(messages.length);
		
		for (Object message : messages) {
			File file = fromMessage(message);
			
			if (file == null)
				continue;
			
			if (file.exists()) {
				try {
					// path may be relative, use absolute path
					files.add(file.getCanonicalFile());
				} catch (IOException e) {
					Logger.getLogger("global").log(Level.SEVERE, e.toString(), e);
				}
			} else {
				// file doesn't exist
				Logger.getLogger("global").log(Level.WARNING, "Invalid File: " + file);
			}
		}
		
		transferablePolicy.handleTransferable(new FileTransferable(files), TransferAction.PUT);
	}
	

	private File fromMessage(Object message) {
		
		if (message instanceof File)
			return (File) message;
		
		if (message instanceof String)
			return new File((String) message);
		
		return null;
	}
	
}
