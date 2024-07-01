package src;

public class Movies {
    public static void main(String[] args) {
        InitialiseDB.main(args);
        PopulateDB.main(args);
        String[] query = {"2", "Titanic"};
        QueryDB.main(query);
    }
}
