package com.nullfish.app.jfd2.command.embed;

import java.text.MessageFormat;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.manipulation.DeleteFailurePolicy;

public class JFDDeleteFailurePolicy implements DeleteFailurePolicy {
	private JFD jfd;

	private boolean ignoreAll = false;

	public JFDDeleteFailurePolicy(JFD jfd) {
		this.jfd = jfd;
	}

	public int getDeleteFailurePolicy(VFile file) {
		JFDDialog dialog = null;
		try {
			if (ignoreAll) {
				return DeleteFailurePolicy.IGNORE;
			}

			dialog = jfd.createDialog();
			dialog.setTitle(JFDResource.LABELS.getString("title_delete"));
			Object[] values = {
					file.getSecurePath()
			};
			dialog.addMessage(MessageFormat.format(JFDResource.MESSAGES.getString("delete_failed"), values));
			dialog.addButton("retry", JFDResource.LABELS.getString("retry"), 'r', false);
			dialog.addButton("ignore", JFDResource.LABELS.getString("ignore"), 'i', true);
			dialog.addButton("ignore_all", JFDResource.LABELS.getString("ignore_all"), 'r', false);
			dialog.addButton("cancel", JFDResource.LABELS.getString("cancel"), 'c', false);

			dialog.pack();
			dialog.setVisible(true);

			String answer = dialog.getButtonAnswer();
			if ("retry".equals(answer)) {
				return DeleteFailurePolicy.RETRY;
			} else if ("ignore".equals(answer)) {
				return DeleteFailurePolicy.IGNORE;
			} else if ("ignore_all".equals(answer)) {
				ignoreAll = true;
				return DeleteFailurePolicy.IGNORE;
			} else {
				return DeleteFailurePolicy.FAIL;
			}
		} finally {
			dialog.dispose();
		}
	}
}
