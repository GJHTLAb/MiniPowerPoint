import model.SlideDocument;
import view.MainWindow;
import context.DocumentContext;

public class App {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            SlideDocument document = new SlideDocument();
            DocumentContext documentContext = new DocumentContext(document);
            new MainWindow(documentContext);
        });
    }
}
