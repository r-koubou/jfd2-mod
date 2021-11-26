package com.nullfish.lib.ui.permissionpanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.ui.tristate_checkbox.TristateCheckBox;
import com.nullfish.lib.ui.tristate_checkbox.TristateState;
import com.nullfish.lib.vfs.Permission;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.permission.FileAccess;
import com.nullfish.lib.vfs.permission.PermissionType;

public class PermissionPanel extends JPanel {
	private List permissionTypes = new ArrayList();
	
	private List fileAccesses = new ArrayList();

	private Map typeAccessCheckBoxMap = new HashMap();
	
	public PermissionPanel() {
		super(new GridBagLayout());
	}
	
	public void init(List files) throws VFSException {
		Map defaulValueMap = new HashMap();
		
		for(int i=0; i<files.size(); i++) {
			VFile file = (VFile) files.get(i);
			Permission permission = file.getPermission();
			
			PermissionType[] types = permission.getTypes();
			for(int j=0; j<types.length; j++) {
				if(!permissionTypes.contains(types[j])) {
					permissionTypes.add(types[j]);
				}
			}
			
			FileAccess[] accesses = permission.getAccess();
			for(int j=0; j<accesses.length; j++) {
				if(!fileAccesses.contains(accesses[j])) {
					fileAccesses.add(accesses[j]);
				}
			}
			
			for(int j=0; j<types.length; j++) {
				for(int k=0; k<accesses.length; k++) {
					TypeAccess typeAccess = new TypeAccess(types[j], accesses[k]);
					TristateState state = (TristateState) defaulValueMap.get(typeAccess);
					TristateState newState = permission.hasPermission(types[j], accesses[k]) ? TristateState.SELECTED : TristateState.DESELECTED;
					if(state == null) {
						defaulValueMap.put(typeAccess, newState);
					} else if(state != newState) {
						defaulValueMap.put(typeAccess, TristateState.INDETERMINATE);
					}
				}
			}
		}
		
		for(int i=0; i<permissionTypes.size(); i++) {
			String label = ((PermissionType)permissionTypes.get(i)).getName();
			try {
				label = JFDResource.LABELS.getString("permission_" + label);
			} catch (Exception e) {}
			add(new JLabel(label), new GridBagConstraints(i+1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		}
		
		for(int i=0; i<fileAccesses.size(); i++) {
			String label = ((FileAccess)fileAccesses.get(i)).getName();
			try {
				label = JFDResource.LABELS.getString("access_" + label);
			} catch (Exception e) {}
			
			add(new JLabel(label), new GridBagConstraints(0, i+1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,10), 0, 0));
			
			for(int j=0; j<permissionTypes.size(); j++) {
				TypeAccess typeAccess = new TypeAccess((PermissionType) permissionTypes.get(j), (FileAccess) fileAccesses.get(i));
				TristateState initialState = (TristateState)defaulValueMap.get(typeAccess);
				TristateCheckBox checkBox = new TristateCheckBox("", null, initialState);
				checkBox.setAllowsIndetermined(initialState == TristateState.INDETERMINATE);
				typeAccessCheckBoxMap.put(typeAccess, checkBox);
				
				add(checkBox, new GridBagConstraints(j + 1, i + 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
			}
		}
	}
	
	public TristateState getState(PermissionType type, FileAccess access) {
		return ((TristateCheckBox) typeAccessCheckBoxMap.get(new TypeAccess(type, access))).getState();
	}
		
	private static class TypeAccess {
		private final PermissionType type;
		private final FileAccess access;
		
		private int hashCode = -1;
			
		TypeAccess(final PermissionType type, final FileAccess access) {
			this.type = type;
			this.access = access;
		}

		public PermissionType getType() {
			return type;
		}

		public FileAccess getAccesses() {
			return access;
		}
		
		public int hashCode() {
			if(hashCode == -1) {
				hashCode = type.hashCode() + access.hashCode();
			}
			return hashCode;
		}
		
		public boolean equals(Object o) {
			if(o == null) {
				return false;
			}
			
			if(!getClass().equals(o.getClass())) {
				return false;
			}
			
			TypeAccess other = (TypeAccess) o;
			return type.equals(other.type) && access.equals(other.access);
		}
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		Iterator checks = typeAccessCheckBoxMap.values().iterator();
		while(checks.hasNext()) {
			((TristateCheckBox) checks.next()).setEnabled(enabled);
		}
	}
}
