package context;

import model.SlideDocument;

public class DocumentContext {

    private SlideDocument document;

    public DocumentContext(SlideDocument document) {
        this.document = document;
    }

    public SlideDocument getDocument() {
        return document;
    }

    public void setDocument(SlideDocument newDocument) {
        this.document = newDocument;
    }
}
