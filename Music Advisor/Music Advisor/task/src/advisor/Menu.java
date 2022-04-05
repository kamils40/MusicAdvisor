package advisor;


import advisor.controllers.RequestController;
import advisor.printingMethod.PrintCategories;
import advisor.printingMethod.PrintNewSongs;
import advisor.printingMethod.PrintPlaylists;
import advisor.printingMethod.Printer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Menu {

    private Scanner scanner;
    private boolean isAuthorized;
    private Map<String, String> categories = new HashMap<>();
    private Printer printer;
    private RequestController requestHandler;
    public Menu(RequestController requestHandler) {
        isAuthorized=false;
        scanner = new Scanner(System.in);
        this.requestHandler = requestHandler;
        this.printer = new Printer();
    }
    private void authenticateUser() {
        System.out.println("Starting authenticating process!");
        requestHandler.getAccessCode();
        requestHandler.getAccessToken();
    }

    private void optionMenu() {
        String userInput = "";
        int pageNumber = 1;
        int maxPageNumber = 0;
        while (!userInput.equals("exit")) {
            printOptions();
            userInput = scanner.nextLine();
            switch (userInput) {

                case "new":
                    pageNumber = 1;
                    printer.setPrinter(new PrintNewSongs(requestHandler.getNewReleases()));
                    printer.print(pageNumber);
                    maxPageNumber = printer.maxPages();
                    break;
                case "featured":
                    pageNumber = 1;
                    printer.setPrinter(new PrintPlaylists
                            (requestHandler.getPlaylists("/v1/browse/featured-playlists")));
                    printer.print(pageNumber);
                    maxPageNumber = printer.maxPages();
                    break;
                case "categories":
                    pageNumber = 1;
                    printer.setPrinter(new PrintCategories(requestHandler.getCategories()));
                    printer.print(pageNumber);
                    maxPageNumber = printer.maxPages();
                    break;
                case "next":
                    pageNumber += 1;
                    printer.print(pageNumber);
                    if (pageNumber > maxPageNumber) {
                        pageNumber = maxPageNumber;
                    }
                    break;
                case "prev":
                    pageNumber -= 1;
                    printer.print(pageNumber);
                    if (pageNumber <= 0) {
                        pageNumber = 1;
                    }
                    break;
                default:
                   if(userInput.startsWith("playlists")) {
                       if(categories.isEmpty()) {
                          categories = requestHandler.getCategoriesNameIdMap();
                       }
                       String[] command = userInput.split("playlists ");
                       if(command.length > 1) {
                           if (categories.containsKey(command[1])) {
                               pageNumber = 1;
                               printer.setPrinter(new PrintPlaylists(
                                       requestHandler.getPlaylists("/v1/browse/categories/" +
                                       categories.get(command[1]) + "/playlists")));
                               printer.print(pageNumber);
                               maxPageNumber = printer.maxPages();
                           } else {
                               System.out.println("Specified id doesn't exist");
                           }
                       }
                   }
                   else {
                       System.out.println("Unknown Command");
                   }
                    break;
                case "exit":
                    System.out.println("---GOODBYE!---");
                    break;
            }
        }
    }
    public void userMenu() {
        authenticateUser();
        optionMenu();
    }
    private void printOptions() {
        System.out.println("\nChoose one of the following options: new for newest songs," +
                "\n featured for featured playlists \n playlists *category name* for playlists of given category" +
                "\n categories for available categories \n next for next page, previous for previous page" +
                "\n exit for exiting");
    }
}
