package no.nav.data.catalog.policies.app;

public class LocalAppStarter {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "local");
        AppStarter.main(args);
    }
}
