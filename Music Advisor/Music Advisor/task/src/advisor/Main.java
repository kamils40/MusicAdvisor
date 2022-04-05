package advisor;

import advisor.controllers.RequestController;

public class Main {
    public static void main(String[] args) {
        RequestController requestHandler = new RequestController();
        Menu menu = new Menu(requestHandler);
        menu.userMenu();
    }
}
