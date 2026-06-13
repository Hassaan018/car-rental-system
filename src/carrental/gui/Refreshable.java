package carrental.gui;

/* Refreshable — implemented by GUI screens that display live data
   and need to reload when the user switches to them. */

public interface Refreshable {
    void refresh();
}
