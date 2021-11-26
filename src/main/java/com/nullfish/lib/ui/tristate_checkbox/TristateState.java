package com.nullfish.lib.ui.tristate_checkbox;

//package eu.javaspecialists.tjsn.gui;

public abstract class TristateState {
	public static final TristateState SELECTED = new TristateState() {
		public TristateState next() {
			return INDETERMINATE;
		}
	};
	public static final TristateState INDETERMINATE = new TristateState() {
		public TristateState next() {
			return DESELECTED;
		}
	};
	public static final TristateState DESELECTED = new TristateState() {
		public TristateState next() {
			return SELECTED;
		}
	};

	public abstract TristateState next();
}
