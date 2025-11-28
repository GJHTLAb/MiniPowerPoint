package model;

import java.util.ArrayList;
import java.util.List;

public class SlideDocument {

    private final List<SlidePage> pages = new ArrayList<>();
    private int currentPageIndex = 0;

    public SlideDocument() {
        pages.add(new SlidePage());
    }

    public void addPage(SlidePage page) {
        pages.add(page);
        currentPageIndex = pages.size() - 1;
    }

    public void removePage(int index) {
        if (pages.size() > 1) {
            pages.remove(index);
            currentPageIndex = Math.max(0, index - 1);
        }
    }

    public SlidePage getCurrentPage() {
        return pages.get(currentPageIndex);
    }

    public void setCurrentPage(int Index) {
        currentPageIndex = Index;
    }

    public List<SlidePage> getPages() {
        return pages;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public void insertPage(int index, SlidePage page) {
        pages.add(index, page);
    }
}
