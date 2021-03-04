package com.tyz.util;

import javax.swing.*;
import java.awt.*;

public interface ITyzViewFrame {
    Font titleFont = new Font("楷体", Font.BOLD, 36);
    Color titleColor = new Color(0,0,192);
    Font buttonFont = new Font("宋体", Font.PLAIN, 21);
    Font textFont = new Font("新宋体", Font.PLAIN, 22);

    default void initView() {
        init();
        dealAction();
    }

    void init();

    void reinit();

    void dealAction();

    RootPaneContainer getFrame();

    void afterShowed();

    default void closeFrame() throws FrameNotFoundException {
        RootPaneContainer frame = getFrame();

        if (frame == null) {
            throw new FrameNotFoundException("Failed to get frame");
        }
        if (frame instanceof Window) {
            ((Window) frame).dispose();
        } else if (frame instanceof JInternalFrame) {
            ((JInternalFrame) frame).dispose();
        } else {
            throw new FrameNotFoundException("Can't recognize the frame");
        }
    }

    default void show() throws FrameNotFoundException {
        RootPaneContainer frame = getFrame();

        if (frame == null) {
            throw new FrameNotFoundException("Failed to get frame");
        }
        if (frame instanceof Window) {
            reinit();
            ((Window) frame).setVisible(true);
            afterShowed();
        } else if (frame instanceof JInternalFrame) {
            reinit();
            ((JInternalFrame) frame).setVisible(true);
            afterShowed();
        } else {
            throw new FrameNotFoundException("Can't recognize the frame");
        }
    }
}
