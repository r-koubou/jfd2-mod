/*
 * Created on 2004/06/09
 *
 */
package com.nullfish.lib.memory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
/**
 * タイトル:  JFD
 * 説明:    A FD like file manager software written in Java.
 * 著作権:   Copyright (c) Shunji Yamaura
 * 会社名:
 * @author Shunji Yamaura
 * @version 1.0
 */

public class MemoryReport implements ActionListener {
    Timer timer;

    public MemoryReport(int interval) {
        timer = new Timer(interval, this);
        timer.start();
    }

    public void showMemory() {
        long total = Runtime.getRuntime().totalMemory();
        System.out.println(
            "Total Memory: " + total + "\n" +
            "Spent Memory : " + (total - Runtime.getRuntime().freeMemory()) + "\n"
        );
    }

    public void actionPerformed(ActionEvent e) {
        showMemory();
    }
}
