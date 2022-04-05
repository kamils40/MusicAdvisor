package advisor.printingMethod;

public class Printer {
    private PrintingMethod printer;

    public void setPrinter(PrintingMethod printer) {
        this.printer = printer;
    }

    public void print(int pageNumber) {
        printer.print(pageNumber);
    }
    public int maxPages() {
        return printer.getMaximumPages();
    }
}
